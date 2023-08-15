package pinacolada.skills.skills;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.interfaces.delegates.FuncT0;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.CardTriggerConnection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.TriggerConnection;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PPrimary;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;
import pinacolada.skills.skills.base.primary.PTrigger_CombatEnd;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.skills.skills.base.primary.PTrigger_Passive;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public abstract class PTrigger extends PPrimary<PField_CardGeneric> {
    public static final int TRIGGER_PRIORITY = 0;
    protected int usesThisTurn;
    public TriggerConnection controller;

    public PTrigger(PSkillData<PField_CardGeneric> data) {
        this(data, PCLCardTarget.None, -1);
    }

    public PTrigger(PSkillData<PField_CardGeneric> data, PCLCardTarget target, int maxUses) {
        super(data, target, maxUses);
        updateUsesAmount();
    }

    public PTrigger(PSkillData<PField_CardGeneric> data, PSkillSaveData content) {
        super(data, content);
        updateUsesAmount();
    }

    public static PTrigger_CombatEnd combatEnd(PSkill<?>... effects) {
        return chain(new PTrigger_CombatEnd(), effects);
    }

    public static PTrigger_Interactable interactAny(PSkill<?>... effects) {
        return (PTrigger_Interactable) chain(new PTrigger_Interactable(), effects).setAmount(-1);
    }

    public static PTrigger_Interactable interactable(PSkill<?>... effects) {
        return chain(new PTrigger_Interactable(), effects);
    }

    public static PTrigger_Interactable interactable(int amount, PSkill<?>... effects) {
        return chain(new PTrigger_Interactable(amount), effects);
    }

    public static PTrigger_Interactable interactablePerCombat(int amount, PSkill<?>... effects) {
        return chain((PTrigger_Interactable) new PTrigger_Interactable(amount).edit(f -> f.setNot(true)), effects);
    }

    public static PTrigger_Passive passive(PSkill<?>... effects) {
        return chain(new PTrigger_Passive(), effects);
    }

    public static PTrigger_When when(PSkill<?>... effects) {
        return chain(new PTrigger_When(), effects);
    }

    public static PTrigger_When when(int perTurn, PSkill<?>... effects) {
        return (PTrigger_When) chain(new PTrigger_When().setAmount(perTurn), effects);
    }

    public static PTrigger_When whenPerCombat(int perTurn, PSkill<?>... effects) {
        return chain((PTrigger_When) new PTrigger_When().setAmount(perTurn).edit(f -> f.setNot(true)), effects);
    }

    public PTrigger chain(PSkill<?>... effects) {
        return PSkill.chain(this, effects);
    }

    protected void flash() {
        if (controller != null) {
            controller.onActivate();
        }
        if (source instanceof AbstractRelic) {
            ((AbstractRelic) source).flash();
        }
    }

    public void forceResetUses() {
        updateUsesAmount();
        updateCounter();
    }

    @Override
    public String getHeaderTextForAmount() {
        return PGR.core.strings.combat_uses;
    }

    // When attached to a power, get the power this is attached to
    @Override
    public AbstractCreature getOwnerCreature() {
        return controller != null ? controller.getOwner() : super.getOwnerCreature();
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String base = null;
        if (sourceCard instanceof PointerProvider && ((PointerProvider) sourceCard).getEffects().contains(this)) {
            String gString = fields.getGroupString();
            base = TEXT.cond_whileIn(gString.isEmpty() ? TEXT.subjects_anyPile() : gString);
        }
        String amountStr = null;
        if (amount > 0) {
            amountStr = fields.not ? TEXT.cond_timesPerCombat(getAmountRawString()) : TEXT.cond_timesPerTurn(getAmountRawString());
        }
        return base != null && amountStr != null ? amountStr + COMMA_SEPARATOR + base : base != null ? base : amountStr != null ? amountStr : "";
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        String subText = getCapitalSubText(perspective, addPeriod);
        String childText = (childEffect != null ? childEffect.getText(perspective, addPeriod) : "");
        return subText.isEmpty() ? childText : subText + COLON_SEPARATOR + StringUtils.capitalize(childText);
    }

    public int getUses() {
        return this.usesThisTurn;
    }

    @Override
    public PTrigger makeCopy() {
        PTrigger copy = (PTrigger) super.makeCopy();
        copy.usesThisTurn = this.usesThisTurn;
        copy.updateCounter();
        return copy;
    }

    public void resetUses() {
        if (!fields.not) {
            forceResetUses();
        }
    }

    public PTrigger setAmount(int amount, int upgrade) {
        super.setAmount(amount, upgrade);
        updateUsesAmount();
        updateCounter();
        return this;
    }

    public PTrigger setAmount(int amount) {
        super.setAmount(amount);
        updateUsesAmount();
        updateCounter();
        return this;
    }

    @Override
    public PTrigger setAmountFromCard() {
        super.setAmountFromCard();
        updateUsesAmount();
        updateCounter();
        return this;
    }

    public PTrigger setChild(PSkill<?> effect) {
        super.setChild(effect);
        return this;
    }

    public PTrigger setChild(PSkill<?>... effects) {
        super.setChild(effects);
        return this;
    }

    public PTrigger setExtra(int amount, int upgrade) {
        super.setExtra(amount, upgrade);
        updateUsesAmount();
        updateCounter();
        return this;
    }

    public PTrigger setExtra(int amount) {
        super.setExtra(amount);
        updateUsesAmount();
        updateCounter();
        return this;
    }

    public PTrigger setTemporaryAmount(int amount) {
        super.setTemporaryAmount(amount);
        updateUsesAmount();
        updateCounter();
        return this;
    }

    public PTrigger setUpgrade(int upgrade) {
        super.setUpgrade(upgrade);
        updateUsesAmount();
        updateCounter();
        return this;
    }

    public PTrigger setUpgradeExtra(int upgrade) {
        super.setUpgradeExtra(upgrade);
        updateUsesAmount();
        updateCounter();
        return this;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerNotBoolean(editor, TEXT.cedit_combat, null);
    }

    public PTrigger stack(PSkill<?> other) {
        if (rootAmount > 0 && other.rootAmount > 0) {
            setAmount(rootAmount + other.rootAmount);
        }
        if (rootExtra > 0 && other.rootExtra > 0) {
            setExtra(rootExtra + other.rootExtra);
        }
        setAmountFromCard();
        forceResetUses();

        // Only update child effects if uses per turn is infinite
        if (rootAmount <= 0 && this.childEffect != null && other.getChild() != null) {
            this.childEffect.stack(other.getChild());
        }
        return this;
    }

    protected boolean triggerOn(FuncT0<Boolean> childAction, PCLUseInfo info) {
        if (this.childEffect != null && sourceCard != null && usesThisTurn != 0) {
            if (usesThisTurn > 0) {
                usesThisTurn -= 1;
                updateCounter();
            }
            flash();
            return childAction.invoke();
        }
        return false;
    }

    protected boolean triggerOn(FuncT0<Boolean> childAction) {
        return triggerOn(childAction, getInfo(null));
    }

    @Override
    public void triggerOnCreate(AbstractCard c, boolean startOfBattle) {
        super.triggerOnCreate(c, startOfBattle);
        if (controller == null) {
            CardTriggerConnection ct = new CardTriggerConnection(this, c);
            ct.initialize();
            controller = ct;
        }
    }

    @Override
    public void triggerOnStartOfBattleForRelic() {
        super.triggerOnStartOfBattleForRelic();
        forceResetUses();
    }

    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info) {
        if (controller != null && !controller.canActivate(this)) {
            return false;
        }
        if (usesThisTurn != 0) {
            boolean result = super.tryPassParent(source, info);

            if (result) {
                if (usesThisTurn > 0) {
                    usesThisTurn -= 1;
                    updateCounter();
                }
                flash();
            }

            return result;
        }
        return false;
    }

    protected void updateCounter() {
        if (source instanceof AbstractRelic) {
            ((AbstractRelic) source).counter = this.usesThisTurn;
        }
    }

    // When initialized, treat 0 like -1
    protected void updateUsesAmount() {
        usesThisTurn = this.amount > 0 ? this.amount : -1;
    }

    // Triggers can only activate through subscribers or clickables
    @Override
    public void use(PCLUseInfo info, PCLActions order) {
    }

    // Called when executed through a clickable
    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if (usesThisTurn != 0) {
            if (usesThisTurn > 0) {
                usesThisTurn -= 1;
                updateCounter();
            }
            flash();
            this.childEffect.use(info, order, shouldPay);
        }
    }
}
