package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT2;
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
    public static float OFFSET = scale(120);
    public DamageMode damageMode = DamageMode.Half;
    public ArrayList<PCLCardAlly> summons = new ArrayList<>();
    public HashMap<AbstractCreature, AbstractCreature> assignedTargets = new HashMap<>();
    public int triggerTimes = BASE_TRIGGER;

    public void add(int times) {
        float baseX = 0;
        float baseY = 0;
        if (AbstractDungeon.player != null) {
            baseX = AbstractDungeon.player.drawX;
            baseY = AbstractDungeon.player.drawY;
        }
        for (int i = 0; i < times; i++) {
            summons.add(new PCLCardAlly(baseX + (times + 3 + summons.size()) * OFFSET, baseY));
        }
    }

    protected PCLCardAlly addSummon(int i) {
        float dist = OFFSET + BASE_LIMIT * 10.0F * Settings.scale;
        float angle = 100.0F + BASE_LIMIT * 12.0F;
        float offsetAngle = angle / 2.0F;
        angle *= i / (BASE_LIMIT - 1.0F);
        angle += 90.0F - offsetAngle;
        float x = dist * MathUtils.cosDeg(angle) + AbstractDungeon.player.drawX;
        float y = dist * MathUtils.sinDeg(angle) + AbstractDungeon.player.drawY + AbstractDungeon.player.hb_h / 2.0F;
        return new PCLCardAlly(x, y);
    }

    public void applyPowers() {
        for (PCLCardAlly ally : summons) {
            if (ally.hasCard()) {
                ally.applyPowers();
            }
        }
    }

    @Deprecated
    public void assignMonsterTargets() {
        for (AbstractMonster mo : GameUtilities.getEnemies(true)) {
            assignedTargets.put(mo, getRightmostTarget());
        }
    }

    public void clear() {
        summons.clear();
    }

    /**
     * Returns the amount of damage that the player should be receiving after factoring in allies
     */
    public int countDamage(int damageAmount, ActionT2<PCLCardAlly, Integer> onAllyAction, FuncT2<Integer, PCLCardAlly, Integer> calculateDamage) {
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
                        final int amount = calculateDamage.invoke(ally, leftover);
                        onAllyAction.invoke(ally, amount);
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
                        int amount = calculateDamage.invoke(ally, cut);
                        onAllyAction.invoke(ally, cut);
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
            if (amount > 0) {
                estimatedMap.put(ally, amount);
            }
        }, GameUtilities::getHealthBarAmount);
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

    public AbstractCreature getTarget(AbstractCreature mo) {
        return assignedTargets.containsKey(mo) ? assignedTargets.get(mo) : AbstractDungeon.player;
    }

    public void initialize() {
        summons.clear();
        assignedTargets.clear();
        triggerTimes = BASE_TRIGGER;

        if (AbstractDungeon.player != null) {
            for (int i = 0; i < BASE_LIMIT; i++) {
                summons.add(addSummon(i));
            }
        }
    }

    public void onBattleEnd() {
        // TODO end of battle checks for cards
        for (PCLCardAlly ally : summons) {
            ally.releaseCard();
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
        assignedTargets.clear();
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

    @Override
    public void renderImpl(SpriteBatch sb) {
        for (PCLCardAlly ally : summons) {
            ally.render(sb);
        }
    }

    public SummonAllyAction summon(PCLCard card, PCLCardAlly target)
    {
        return PCLActions.bottom.summonAlly(card, target);
    }

    public WithdrawAllyAction withdraw(PCLCardAlly target)
    {
        return PCLActions.top.withdrawAlly(target).setTriggerTimes(triggerTimes);
    }

    public WithdrawAllyAction withdraw(Collection<PCLCardAlly> target)
    {
        return PCLActions.top.withdrawAlly(target).setTriggerTimes(triggerTimes);
    }

    @Override
    public void updateImpl() {
        // Update empty animation independently so alpha is the same for all slots
        PCLCardAlly.emptyAnimation.update(EUI.delta(), 0, 0);
        for (PCLCardAlly ally : summons) {
            ally.update();
        }
    }

    public int tryDamage(DamageInfo info, int damageAmount) {
        return countDamage(damageAmount, (ally, amount) -> ally.damage(new DamageInfo(info.owner, amount, info.type)), (creature, amount) -> Math.min(amount, creature.currentHealth));
    }

    public enum DamageMode {
        Full,
        Half,
        Distributed,
        None
    }
}
