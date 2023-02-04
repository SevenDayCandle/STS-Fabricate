package pinacolada.powers;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUI;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.ClickableProvider;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class PCLClickableUse
{
    public FuncT1<Boolean, PCLClickableUse> checkCondition;
    public PCLTriggerUsePool pool;
    public PSkill move;
    public boolean clickable;
    public final ClickableProvider source;
    private boolean canUse;
    private GameActionManager.Phase currentPhase;

    public PCLClickableUse(ClickableProvider power, ActionT2<PSpecialSkill, PCLUseInfo> onUse)
    {
        this(power, new PSpecialSkill(power.getID(), power.getDescription(), onUse));
    }

    public PCLClickableUse(ClickableProvider power, ActionT2<PSpecialSkill, PCLUseInfo> onUse, int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        this(power, new PSpecialSkill(power.getID(), power.getDescription(), onUse), new PCLTriggerUsePool(uses, refreshEachTurn, stackAutomatically));
    }

    public PCLClickableUse(ClickableProvider power, ActionT2<PSpecialSkill, PCLUseInfo> onUse, PCLCardTarget target, int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        this(power, new PSpecialSkill(power.getID(), power.getDescription(), onUse).setTarget(target), new PCLTriggerUsePool(uses, refreshEachTurn, stackAutomatically));
    }

    public PCLClickableUse(ClickableProvider power, ActionT2<PSpecialSkill, PCLUseInfo> onUse, PCLTriggerUsePool pool)
    {
        this(power, new PSpecialSkill(power.getID(), power.getDescription(), onUse), pool);
    }

    public PCLClickableUse(ClickableProvider power, PSkill move)
    {
        this(power, move, new PCLTriggerUsePool());
    }

    public PCLClickableUse(ClickableProvider power, PSkill move, int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        this(power, move, new PCLTriggerUsePool(uses, refreshEachTurn, stackAutomatically));
    }


    public PCLClickableUse(ClickableProvider power, PSkill move, PCLTriggerUsePool pool)
    {
        this.source = power;
        this.move = move;
        this.pool = pool;
    }

    public PCLClickableUse addUses(int uses)
    {
        this.pool.addUses(uses);
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

    public int getCurrentUses()
    {
        return pool.uses;
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
        }
        updateHeader();
    }

    public PCLClickableUse setCheckCondition(FuncT1<Boolean, PCLClickableUse> checkCondition)
    {
        this.checkCondition = checkCondition;

        return this;
    }

    public PCLClickableUse setOneUsePerPower(boolean refreshEachTurn)
    {
        return setUses(1, refreshEachTurn, true);
    }

    public PCLClickableUse setTarget(PCLCardTarget target)
    {
        this.move.setTarget(target);
        return this;
    }

    public PCLClickableUse setUses(PCLTriggerUsePool pool)
    {
        this.pool = pool;
        updateHeader();
        refresh(false, true);

        return this;
    }

    public PCLClickableUse setUses(int uses)
    {
        return setUses(uses, uses, true, true);
    }

    public PCLClickableUse setUses(int uses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        return setUses(uses, uses, refreshEachTurn, stackAutomatically);
    }

    public PCLClickableUse setUses(int uses, int baseUses, boolean refreshEachTurn, boolean stackAutomatically)
    {
        this.pool.setUses(uses, baseUses, refreshEachTurn, stackAutomatically);
        updateHeader();
        refresh(false, true);

        return this;
    }

    public void targetToUse(int amount)
    {
        if (move.requiresTarget())
        {
            PCLActions.bottom.selectCreature(PCLCardTarget.Single, source.getName())
                    .addCallback(c ->
                    {
                        if (c != null)
                        {
                            this.use((AbstractMonster) c, amount);
                        }
                    });
        }
        else
        {
            this.use(null, amount);
        }
    }

    public void updateHeader()
    {
        EUITooltip tooltip = source.getTooltip();
        if (tooltip != null)
        {
            if (tooltip.subHeader == null)
            {
                tooltip.subHeader = new ColoredString();
            }
            tooltip.subHeader.color = pool.uses == 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR;
            tooltip.subHeader.text = (hasInfiniteUses() ? PGR.core.strings.subjects_infinite : (pool.uses + "/" + pool.baseUses)) + " " + PGR.core.strings.combat_uses;
        }
    }

    public void use(AbstractMonster m)
    {
        use(m, 1);
    }

    public void use(AbstractMonster m, int amount)
    {
        // Manually check to use to prevent abuse
        if (!(pool.canUse(amount) && checkCondition()))
        {
            return;
        }

        AbstractCreature owner = move.getSourceCreature();
        if (owner == null)
        {
            owner = source.getSource();
        }
        PCLUseInfo info = CombatManager.playerSystem.generateInfo(move.sourceCard, owner, m);
        info.setData(amount);
        move.use(info, CombatManager.onClickableUsed(this, m, amount));
        pool.use(amount);
        source.onClicked();
        refresh(false, true);
    }
}