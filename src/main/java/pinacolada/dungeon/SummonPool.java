package pinacolada.dungeon;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.EUIBase;
import pinacolada.actions.PCLActions;
import pinacolada.actions.creature.SummonAllyAction;
import pinacolada.actions.creature.WithdrawAllyAction;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SummonPool extends EUIBase {
    public static int BASE_TRIGGER = 2;
    public static int BASE_LIMIT = 3;
    public static float OFFSET = scale(160);
    public DamageMode damageMode = DamageMode.Half;
    public ArrayList<PCLCardAlly> summons = new ArrayList<>();
    public int triggerTimes = BASE_TRIGGER;

    public void addSummon(int times) {
        float baseX = 0;
        float baseY = 0;
        if (AbstractDungeon.player != null) {
            baseX = AbstractDungeon.player.drawX;
            baseY = AbstractDungeon.player.drawY;
        }
        int curSize = summons.size();
        for (int i = curSize; i < times + curSize; i++) {
            summons.add(new PCLCardAlly(i, baseX, baseY));
        }
        for (PCLCardAlly summon : summons) {
            repositionSummon(summon);
        }
    }

    public void applyPowers() {
        for (PCLCardAlly ally : summons) {
            if (ally.hasCard()) {
                ally.applyPowers();
            }
        }
    }

    public void clear() {
        summons.clear();
    }

    /**
     * Returns the amount of damage that the player should be receiving after factoring in allies
     */
    public int countDamage(int damageAmount, FuncT2<Integer, PCLCardAlly, Integer> onAllyAction) {
        int leftover = damageAmount;
        int remainder = 0;

        if (damageMode == DamageMode.Half) {
            remainder = leftover / 2;
            leftover -= remainder;
        }

        switch (damageMode) {
            case Half:
            case Full:
                for (PCLCardAlly ally : summons) {
                    if (leftover > 0 && ally.hasCard()) {
                        int amount = Math.min(ally.getEffectiveHPForTurn(), leftover);
                        amount = onAllyAction.invoke(ally, amount);
                        leftover -= amount;
                    }
                }
                break;
            case Distributed:
                int livingCount = 1 + EUIUtils.count(summons, PCLCardAlly::hasCard);
                int cut = leftover / livingCount;
                leftover = leftover - cut * (livingCount - 1);
                for (PCLCardAlly ally : summons) {
                    if (ally.hasCard()) {
                        int amount = Math.min(ally.getEffectiveHPForTurn(), cut);
                        amount = onAllyAction.invoke(ally, cut);
                        leftover += amount;
                    }
                }
                break;
        }
        return leftover + remainder;
    }

    public HashMap<AbstractCreature, Integer> estimateDamage(int damageAmount) {
        final HashMap<AbstractCreature, Integer> estimatedMap = new HashMap<>();
        int finalResult = countDamage(damageAmount, (ally, amount) -> {
            int selfHPEstimation = GameUtilities.getHealthBarAmount(ally, amount);
            if (selfHPEstimation > 0) {
                estimatedMap.put(ally, selfHPEstimation);
            }
            return amount;
        });
        if (finalResult > 0) {
            estimatedMap.put(AbstractDungeon.player, finalResult);
        }

        return estimatedMap;
    }

    public AbstractCreature getRightmostTarget() {
        for (int i = summons.size() - 1; i >= 0; i--) {
            PCLCardAlly summon = summons.get(i);
            if (summon.hasCard()) {
                return summon;
            }
        }
        return AbstractDungeon.player;
    }

    public ArrayList<AbstractMonster> getSummons(boolean activeOnly) {
        ArrayList<AbstractMonster> returned = new ArrayList<>();
        if (activeOnly) {
            for (PCLCardAlly a : summons) {
                if (a.hasCard()) {
                    returned.add(a);
                }
            }
        }
        else {
            returned.addAll(summons);
        }
        return returned;
    }

    public void initialize() {
        summons.clear();
        triggerTimes = BASE_TRIGGER;

        if (AbstractDungeon.player != null) {
            addSummon(BASE_LIMIT);
        }
    }

    public void onBattleEnd() {
        for (PCLCardAlly ally : summons) {
            ally.releaseCard(true);
        }
    }

    public void onEndOfRound() {
        for (PCLCardAlly ally : summons) {
            ally.atEndOfRound();
        }
    }

    public void onEndOfTurnFirst() {
        for (PCLCardAlly ally : summons) {
            if (ally.priority == DelayTiming.EndOfTurnFirst && !ally.hasTakenTurn) {
                PCLActions.last.triggerAlly(ally, false);
            }
        }
    }

    public void onEndOfTurnLast() {
        for (PCLCardAlly ally : summons) {
            if (ally.priority == DelayTiming.EndOfTurnLast && !ally.hasTakenTurn) {
                PCLActions.last.triggerAlly(ally);
            }
        }
    }

    public void onStartOfTurn() {
        for (PCLCardAlly ally : summons) {
            ally.loseBlock();
            ally.applyStartOfTurnPowers();
            if (ally.priority == DelayTiming.StartOfTurnFirst) {
                PCLActions.last.triggerAlly(ally, false);
            }
        }
    }

    public void onStartOfTurnPostDraw() {
        for (PCLCardAlly ally : summons) {
            ally.applyStartOfTurnPostDrawPowers();
            if (ally.priority == DelayTiming.StartOfTurnLast) {
                PCLActions.last.triggerAlly(ally, false);
            }
        }
    }

    public void removeSummon(int amount) {
        int curSize = summons.size();
        for (int i = 0; i < amount; i++) {
            if (summons.size() > 0) {
                PCLCardAlly ally = summons.remove(summons.size() - 1);
                if (ally.card != null) {
                    PCLActions.bottom.withdrawAlly(ally);
                }
            }
        }
        for (PCLCardAlly summon : summons) {
            repositionSummon(summon);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        for (PCLCardAlly ally : summons) {
            ally.render(sb);
        }
    }

    public void repositionSummon(PCLCardAlly ally) {
        int size = summons.size();
        float dist = OFFSET + size * 10.0F * Settings.scale;
        float angle = 100.0F + size * 12.0F;
        float offsetAngle = angle / 2.0F;
        angle *= ally.index / (size - 1.0F);
        angle += 90.0F - offsetAngle;
        ally.refreshLocation(dist * MathUtils.cosDeg(angle) + AbstractDungeon.player.drawX, dist * MathUtils.sinDeg(angle) + AbstractDungeon.player.drawY + AbstractDungeon.player.hb_h / 2.0F);
    }

    public SummonAllyAction summon(PCLCard card, PCLCardAlly target) {
        return PCLActions.bottom.summonAlly(card, target);
    }

    public int tryDamage(DamageInfo info, int damageAmount) {
        return countDamage(damageAmount, (ally, amount) -> {
            ally.damage(new DamageInfo(info.owner, amount, info.type));
            return amount;
        });
    }

    @Override
    public void updateImpl() {
        // Update empty animation independently so alpha is the same for all slots
        PCLCardAlly.emptyAnimation.update(EUI.delta(), 0, 0);
        for (PCLCardAlly ally : summons) {
            ally.update();
        }
    }

    public WithdrawAllyAction withdraw(PCLCardAlly target) {
        return PCLActions.top.withdrawAlly(target).setTriggerTimes(triggerTimes);
    }

    public WithdrawAllyAction withdraw(Collection<PCLCardAlly> target) {
        return PCLActions.top.withdrawAlly(target).setTriggerTimes(triggerTimes);
    }

    public enum DamageMode {
        Full,
        Half,
        Distributed,
        None
    }
}
