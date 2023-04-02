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
import extendedui.ui.EUIBase;
import pinacolada.actions.PCLActions;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLCreature;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SummonPool extends EUIBase
{
    public static int BASE_LIMIT = 3;
    public static float OFFSET = scale(120);
    public DamageMode damageMode = DamageMode.Half;
    public ArrayList<PCLCardAlly> summons = new ArrayList<>();
    public HashMap<AbstractCreature, AbstractCreature> assignedTargets = new HashMap<>();

    public void initialize()
    {
        summons.clear();
        assignedTargets.clear();

        if (AbstractDungeon.player != null)
        {
            for (int i = 0; i < BASE_LIMIT; i++)
            {
                summons.add(addSummon(i));
            }
        }
    }

    protected PCLCardAlly addSummon(int i)
    {
        float dist = OFFSET + BASE_LIMIT * 10.0F * Settings.scale;
        float angle = 100.0F + BASE_LIMIT * 12.0F;
        float offsetAngle = angle / 2.0F;
        angle *= i / (BASE_LIMIT - 1.0F);
        angle += 90.0F - offsetAngle;
        float x = dist * MathUtils.cosDeg(angle) + AbstractDungeon.player.drawX;
        float y = dist * MathUtils.sinDeg(angle) + AbstractDungeon.player.drawY + AbstractDungeon.player.hb_h / 2.0F;
        return new PCLCardAlly(x, y);
    }

    public void add(int times)
    {
        float baseX = 0;
        float baseY = 0;
        if (AbstractDungeon.player != null)
        {
            baseX = AbstractDungeon.player.drawX;
            baseY = AbstractDungeon.player.drawY;
        }
        for (int i = 0; i < times; i++)
        {
            summons.add(new PCLCardAlly(baseX + (times + 3 + summons.size()) * OFFSET, baseY));
        }
    }

    public void applyPowers()
    {
        for (PCLCardAlly ally : summons)
        {
            if (ally.hasCard())
            {
                ally.applyPowers();
            }
        }
    }

    public void clear()
    {
        summons.clear();
    }

    public ArrayList<AbstractMonster> getSummons(boolean activeOnly)
    {
        ArrayList<AbstractMonster> returned = new ArrayList<>();
        if (activeOnly)
        {
            for (PCLCardAlly a : summons)
            {
                if (a.hasCard())
                {
                    returned.add(a);
                }
            }
        }
        else
        {
            returned.addAll(summons);
        }
        return returned;
    }

    @Override
    public void updateImpl()
    {
        // Update empty animation independently so alpha is the same for all slots
        PCLCardAlly.emptyAnimation.update(EUI.delta(), 0, 0);
        for (PCLCardAlly ally : summons)
        {
            ally.update();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        for (PCLCardAlly ally : summons)
        {
            ally.render(sb);
        }
    }

    public void onStartOfTurn()
    {
        assignedTargets.clear();
        List<PCLCardAlly> sorted = summons.stream().filter(a -> a.priority >= PCLCreature.PRIORITY_START_FIRST).sorted((a, b) -> b.priority - a.priority).collect(Collectors.toList());
        for (PCLCardAlly ally : summons)
        {
            ally.loseBlock();
            ally.applyStartOfTurnPowers();
        }
        for (PCLCardAlly ally : sorted)
        {
            PCLActions.bottom.triggerAlly(ally);
        }
    }

    public void onStartOfTurnPostDraw()
    {
        for (PCLCardAlly ally : summons)
        {
            ally.applyStartOfTurnPostDrawPowers();
            if (ally.priority == PCLCreature.PRIORITY_START_LAST)
            {
                PCLActions.bottom.triggerAlly(ally);
            }
        }
    }

    public void onEndOfTurnFirst()
    {
        for (PCLCardAlly ally : summons)
        {
            if (ally.priority == PCLCreature.PRIORITY_END_FIRST)
            {
                PCLActions.bottom.triggerAlly(ally);
            }
        }
    }

    public void onEndOfTurnLast()
    {
        List<PCLCardAlly> sorted = summons.stream().filter(a -> a.priority <= PCLCreature.PRIORITY_END_LAST).sorted((a, b) -> b.priority - a.priority).collect(Collectors.toList());
        for (PCLCardAlly ally : sorted)
        {
            PCLActions.bottom.triggerAlly(ally);
        }
    }

    public void onEndOfRound()
    {
        for (PCLCardAlly ally : summons)
        {
            ally.atEndOfRound();
        }
    }

    public void onBattleEnd()
    {
        // TODO end of battle checks for cards
        for (PCLCardAlly ally : summons)
        {
            ally.releaseCard();
        }
    }

    public int tryDamage(DamageInfo info, int damageAmount)
    {
        int leftover = damageAmount;
        int remainder = 0;

        if (damageMode == DamageMode.Half)
        {
            remainder = leftover / 2;
            leftover -= remainder;
        }

        switch (damageMode)
        {
            case Half:
            case Full:
                for (PCLCardAlly ally : summons)
                {
                    if (leftover > 0 && ally.hasCard())
                    {
                        final int amount = Math.min(leftover, ally.currentHealth);
                        ally.damage(new DamageInfo(info.owner, amount, info.type));
                        leftover -= amount;
                    }
                }
                break;
            case Distributed:
                int livingCount = 1 + EUIUtils.count(summons, PCLCardAlly::hasCard);
                int cut = leftover / livingCount;
                leftover = leftover - cut * (livingCount - 1);
                for (PCLCardAlly ally : summons)
                {
                    if (ally.hasCard())
                    {
                        int amount = cut - ally.currentHealth;
                        if (amount > 0)
                        {
                            leftover += amount;
                        }
                        ally.damage(new DamageInfo(info.owner, cut, info.type));
                    }
                }
                break;
        }
        return leftover + remainder;
    }

    public AbstractCreature getTarget(AbstractCreature mo)
    {
        return assignedTargets.containsKey(mo) ? assignedTargets.get(mo) : AbstractDungeon.player;
    }

    @Deprecated
    public void assignMonsterTargets()
    {
        for (AbstractMonster mo : GameUtilities.getEnemies(true))
        {
            assignedTargets.put(mo, getRightmostTarget());
        }
    }

    public AbstractCreature getRightmostTarget()
    {
        for (int i = summons.size() - 1; i >= 0; i--)
        {
            PCLCardAlly summon = summons.get(i);
            if (summon.hasCard())
            {
                return summon;
            }
        }
        return AbstractDungeon.player;
    }

    public enum DamageMode
    {
        Full,
        Half,
        Distributed,
        None
    }
}
