package pinacolada.powers;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class PCLClickableUse {
    public final ClickableProvider source;
    private boolean canUse;
    private GameActionManager.Phase currentPhase;
    public PCLTriggerUsePool pool;
    public PSkill<?> move;
    public boolean clickable;

    public PCLClickableUse(ClickableProvider power, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse) {
        this(power, new PSpecialSkill(power.getID(), power.getDescription(), onUse));
    }

    public PCLClickableUse(ClickableProvider power, PSkill<?> move) {
        this(power, move, new PCLTriggerUsePool());
    }

    public PCLClickableUse(ClickableProvider power, PSkill<?> move, PCLTriggerUsePool pool) {
        this.source = power;
        this.move = move;
        this.pool = pool;
        canUse = move != null;
    }

    public PCLClickableUse(ClickableProvider power, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, int uses, boolean refreshEachTurn, boolean stackAutomatically) {
        this(power, new PSpecialSkill(power.getID(), power.getDescription(), onUse), new PCLTriggerUsePool(uses, refreshEachTurn, stackAutomatically));
    }

    public PCLClickableUse(ClickableProvider power, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, PCLCardTarget target, int uses, boolean refreshEachTurn, boolean stackAutomatically) {
        this(power, new PSpecialSkill(power.getID(), power.getDescription(), onUse).setTarget(target), new PCLTriggerUsePool(uses, refreshEachTurn, stackAutomatically));
    }

    public PCLClickableUse(ClickableProvider power, ActionT3<PSpecialSkill, PCLUseInfo, PCLActions> onUse, PCLTriggerUsePool pool) {
        this(power, new PSpecialSkill(power.getID(), power.getDescription(), onUse), pool);
    }


    public PCLClickableUse(ClickableProvider power, PSkill<?> move, int uses, boolean refreshEachTurn, boolean stackAutomatically) {
        this(power, move, new PCLTriggerUsePool(uses, refreshEachTurn, stackAutomatically));
    }

    public PCLClickableUse addUses(int uses) {
        if (!pool.hasInfiniteUses()) {
            this.pool.addUses(uses);
        }
        refresh(false, true);

        return this;
    }

    public boolean canUse() {
        return canUse;
    }

    public boolean checkCondition() {
        return pool.canUse() && (!(move instanceof PCond) || ((PCond<?>) move).checkCondition(move.getInfo(source.getSource()), false, null));
    }

    public int getCurrentUses() {
        return pool.uses;
    }

    public boolean hasInfiniteUses() {
        return pool.hasInfiniteUses();
    }

    public boolean interactable() {
        return (currentPhase == GameActionManager.Phase.WAITING_ON_USER && GameUtilities.isPlayerTurn(true)) && canUse;
    }

    public void refresh(boolean startOfTurn, boolean forceUpdate) {
        if (startOfTurn) {
            pool.refresh();
        }
        final GameActionManager.Phase phase = AbstractDungeon.actionManager.phase;
        if (startOfTurn || forceUpdate || currentPhase != phase || EUI.elapsed50()) {
            canUse = checkCondition();
            currentPhase = phase;
        }
        updateHeader();
    }

    public PCLClickableUse setOneUsePerPower(boolean refreshEachTurn) {
        return setUses(1, refreshEachTurn, true);
    }

    public PCLClickableUse setTarget(PCLCardTarget target) {
        this.move.setTarget(target);
        return this;
    }

    public PCLClickableUse setUses(int uses, boolean refreshEachTurn, boolean stackAutomatically) {
        return setUses(uses, uses, refreshEachTurn, stackAutomatically);
    }

    public PCLClickableUse setUses(int uses, int baseUses, boolean refreshEachTurn, boolean stackAutomatically) {
        this.pool.setUses(uses, baseUses, refreshEachTurn, stackAutomatically);
        updateHeader();
        refresh(false, true);

        return this;
    }

    public PCLClickableUse setUses(PCLTriggerUsePool pool) {
        this.pool = pool;
        updateHeader();
        refresh(false, true);

        return this;
    }

    public PCLClickableUse setUses(int uses) {
        return setUses(uses, uses, true, true);
    }

    public void targetToUse(int amount) {
        if (move.requiresTarget()) {
            PCLActions.bottom.selectCreature(PCLCardTarget.Single, source.getName())
                    .addCallback(c ->
                    {
                        if (c != null) {
                            this.use((AbstractMonster) c, amount);
                        }
                    });
        }
        else {
            this.use(null, amount);
        }
    }

    public void updateHeader() {
        EUITooltip tooltip = source.getTooltip();
        if (tooltip != null) {
            if (tooltip.subHeader == null) {
                tooltip.subHeader = new ColoredString();
                tooltip.invalidateHeight();
            }
            tooltip.subHeader.color = pool.uses == 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR;
            tooltip.subHeader.text = (hasInfiniteUses() ? PGR.core.strings.subjects_infinite :
                    EUIRM.strings.numNoun((pool.uses + "/" + pool.baseUses), pool.refreshEachTurn ? PGR.core.strings.subjects_thisTurn(PGR.core.strings.combat_uses) : PGR.core.strings.subjects_thisCombat(PGR.core.strings.combat_uses)));
        }
    }

    public void use(AbstractMonster m, int amount) {
        // Manually check to use to prevent abuse
        AbstractCreature owner = move.getSourceCreature();
        if (owner == null) {
            owner = source.getSource();
        }
        PCLUseInfo info = CombatManager.playerSystem.generateInfo(EUIUtils.safeCast(move.source, AbstractCard.class), owner, m);
        move.refresh(info, true, true);
        if (!(checkCondition())) {
            return;
        }

        info.setData(amount);
        move.use(info, PCLActions.bottom, CombatManager.onClickableUsed(this, m, amount));
        pool.use(amount);
        source.onClicked();
        refresh(false, true);
    }

    public void use(AbstractMonster m) {
        use(m, 1);
    }
}