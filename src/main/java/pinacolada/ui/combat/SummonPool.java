package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUI;
import extendedui.ui.EUIBase;
import pinacolada.interfaces.subscribers.OnEndOfTurnFirstSubscriber;
import pinacolada.interfaces.subscribers.OnEndOfTurnLastSubscriber;
import pinacolada.interfaces.subscribers.OnStartOfTurnPostDrawSubscriber;
import pinacolada.interfaces.subscribers.OnStartOfTurnSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLCreature;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class SummonPool extends EUIBase implements OnStartOfTurnSubscriber, OnStartOfTurnPostDrawSubscriber, OnEndOfTurnFirstSubscriber, OnEndOfTurnLastSubscriber
{
    public static int BASE_LIMIT = 3;
    public static float OFFSET = scale(120);
    public ArrayList<PCLCardAlly> summons = new ArrayList<>();
    public HashMap<AbstractCreature, AbstractCreature> assignedTargets = new HashMap<>();

    public void initialize()
    {
        CombatManager.onStartOfTurn.subscribe(this);
        CombatManager.onStartOfTurnPostDraw.subscribe(this);
        CombatManager.onEndOfTurnFirst.subscribe(this);
        CombatManager.onEndOfTurnLast.subscribe(this);

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

    @Override
    public void onStartOfTurn()
    {
        assignedTargets.clear();
        List<PCLCardAlly> sorted = summons.stream().filter(a -> a.priority >= PCLCreature.PRIORITY_START_FIRST).sorted((a, b) -> b.priority - a.priority).collect(Collectors.toList());
        for (PCLCardAlly ally : summons)
        {
            ally.applyStartOfTurnPowers();
        }
        for (PCLCardAlly ally : sorted)
        {
            ally.takeTurn();
        }
    }

    @Override
    public void onStartOfTurnPostDraw()
    {
        for (PCLCardAlly ally : summons)
        {
            ally.applyStartOfTurnPostDrawPowers();
            if (ally.priority == PCLCreature.PRIORITY_START_LAST)
            {
                ally.takeTurn();
            }
        }
    }

    @Override
    public void onEndOfTurnFirst(boolean isPlayer)
    {
        for (PCLCardAlly ally : summons)
        {
            ally.applyEndOfTurnTriggers();
            if (ally.priority == PCLCreature.PRIORITY_END_FIRST)
            {
                ally.takeTurn();
            }
        }
    }

    @Override
    public void onEndOfTurnLast(boolean isPlayer)
    {
        List<PCLCardAlly> sorted = summons.stream().filter(a -> a.priority <= PCLCreature.PRIORITY_END_LAST).sorted((a, b) -> b.priority - a.priority).collect(Collectors.toList());
        for (PCLCardAlly ally : sorted)
        {
            ally.takeTurn();
        }
    }

    public int tryDamage(DamageInfo info, int damageAmount)
    {
        int leftover = damageAmount;
        for (PCLCardAlly ally : summons)
        {
            if (leftover > 0 && ally.hasCard())
            {
                final int amount = Math.min(leftover, ally.currentHealth);
                ally.damage(new DamageInfo(info.owner, amount, info.type));
                leftover -= amount;
            }
        }
        return leftover;
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
}
