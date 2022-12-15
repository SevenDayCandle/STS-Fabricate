package pinacolada.powers;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUI;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLPowerTriggerCondition
{
    protected static final FuncT1<Boolean, PCLPowerTriggerCondition> EMPTY_FUNCTION = __ -> true;
    public final PCLPower power;
    public EUITooltip tooltip;
    public FuncT1<Boolean, PCLPowerTriggerCondition> checkCondition;
    public PCLPowerUsePool pool;
    public PSkill move;
    public boolean clickable;
    private boolean canUse;
    private GameActionManager.Phase currentPhase;

    public PCLPowerTriggerCondition(PCLPower power, ActionT2<PSpecialSkill, PCLUseInfo> onUse)
    {
        this(power, new PSpecialSkill(power.ID, "", onUse));
    }

    public PCLPowerTriggerCondition(PCLPower power, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        this(power, new PSpecialSkill(power.ID, "", onUse), new PCLPowerUsePool(uses, refreshEachTurn, stackAutomatically));
    }

    public PCLPowerTriggerCondition(PCLPower power, ActionT2<PSpecialSkill, PCLUseInfo> onUse, PCLPowerUsePool pool)
    {
        this(power, new PSpecialSkill(power.ID, "", onUse), pool);
    }

    public PCLPowerTriggerCondition(PCLPower power, PSkill move)
    {
        this(power, move, new PCLPowerUsePool());
    }

    public PCLPowerTriggerCondition(PCLPower power, PSkill move, int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        this(power, move, new PCLPowerUsePool(uses, refreshEachTurn, stackAutomatically));
    }


    public PCLPowerTriggerCondition(PCLPower power, PSkill move, PCLPowerUsePool pool)
    {
        this.power = power;
        this.move = move;
        this.pool = pool;
    }

    public PCLPowerTriggerCondition addUses(int uses)
    {
        this.pool.addUses(uses);
        this.power.updateDescription();
        refresh(false, true);

        return this;
    }

    public boolean canUse()
    {
        return canUse;
    }

    public boolean checkCondition()
    {
        return (checkCondition == null || checkCondition.invoke(this)) && (!(move instanceof PCond) || ((PCond) move).checkCondition(null, false, false));
    }

    public boolean hasInfiniteUses()
    {
        return pool.hasInfiniteUses();
    }

    public boolean interactable()
    {
        return (currentPhase == GameActionManager.Phase.WAITING_ON_USER && GameUtilities.isPlayerTurn(true)) && canUse;
    }

    public void refresh(boolean startOfTurn, boolean forceUpdate)
    {
        if (startOfTurn)
        {
            pool.refresh();
        }
        final GameActionManager.Phase phase = AbstractDungeon.actionManager.phase;
        if (startOfTurn || forceUpdate || currentPhase != phase || EUI.elapsed50())
        {
            canUse = pool.canUse() && checkCondition();
            currentPhase = phase;
            power.updateDescription();
        }
    }

    public PCLPowerTriggerCondition setCheckCondition(FuncT1<Boolean, PCLPowerTriggerCondition> checkCondition)
    {
        this.checkCondition = checkCondition == null ? EMPTY_FUNCTION : checkCondition;

        return this;
    }

    public PCLPowerTriggerCondition setOneUsePerPower(boolean refreshEachTurn)
    {
        return setUses(1, refreshEachTurn, true);
    }

    public PCLPowerTriggerCondition setTarget(PCLCardTarget target)
    {
        this.move.setTarget(target);
        return this;
    }

    public PCLPowerTriggerCondition setUses(PCLPowerUsePool pool)
    {
        this.pool = pool;
        this.power.updateDescription();
        refresh(false, true);

        return this;
    }

    public PCLPowerTriggerCondition setUses(int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        this.pool.setUses(uses, refreshEachTurn, stackAutomatically);
        this.power.updateDescription();
        refresh(false, true);

        return this;
    }

    public void targetToUse()
    {
        final ArrayList<AbstractMonster> enemies = GameUtilities.getEnemies(true);
        if (enemies.size() == 1)
        {
            this.use(enemies.get(0));
        }
        else if (move.requiresTarget())
        {
            PCLActions.bottom.selectCreature(PCLCardTarget.Single, power.name)
                    .addCallback(c ->
                    {
                        if (c != null)
                        {
                            this.use((AbstractMonster) c);
                        }
                    });
        }
        else
        {
            this.use(null);
        }
    }

    public void updateTip(EUITooltip tip)
    {
        if (tip != null)
        {
            if (tip.subHeader == null)
            {
                tip.subHeader = new ColoredString();
            }
            tip.subHeader.color = pool.uses == 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR;
            tip.subHeader.text = (hasInfiniteUses() ? "Infinite" : (pool.uses + "/" + pool.baseUses)) + " " + PGR.core.strings.combat.uses;
        }
    }

    public void use(AbstractMonster m)
    {
        // Manually check to use to prevent abuse
        if (!(pool.canUse() && checkCondition()))
        {
            return;
        }

        move.use(null, CombatManager.onClickablePowerUsed(power, m));
        pool.use();
        power.flashWithoutSound();
        refresh(false, true);
        power.updateDescription();
    }
}