package pinacolada.skills.skills;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.CardTriggerConnection;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.markers.TriggerConnection;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.monsters.PCLCardAlly;
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

    public static PTrigger_When whenEveryTimes(int perTurn, PSkill<?>... effects) {
        return (PTrigger_When) chain(new PTrigger_When().setAmount(perTurn).edit(f -> f.setForced(true)), effects);
    }

    public static PTrigger_When whenEveryTimesCombat(int perTurn, PSkill<?>... effects) {
        return (PTrigger_When) chain(new PTrigger_When().setAmount(perTurn).edit(f -> f.setForced(true).setNot(true)), effects);
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
        else if (source instanceof AbstractBlight) {
            ((AbstractBlight) source).flash();
        }
    }

    public void forceResetUses() {
        updateUsesAmount();
        onUpdateUsesPerTurn();
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
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = null;
        if (source instanceof EditorCard && ((EditorCard) source).getEffects().contains(this)) {
            String gString = fields.getGroupString();
            base = TEXT.cond_whileIn(gString.isEmpty() ? TEXT.subjects_anyPile() : gString);
        }

        // Times per combat. Overwritten by Every x times
        if (amount > 0 && !fields.forced) {
            String amountStr = fields.not ? TEXT.cond_timesPerCombat(getAmountRawString(requestor)) : TEXT.cond_timesPerTurn(getAmountRawString(requestor));
            return base != null ? amountStr + COMMA_SEPARATOR + base : amountStr != null ? amountStr : "";
        }
        return base != null ? base : "";
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String subText = getCapitalSubText(perspective, requestor, addPeriod);
        String childText = (childEffect != null ? childEffect.getText(perspective, requestor, addPeriod) : "");
        return subText.isEmpty() ? childText : subText + COLON_SEPARATOR + StringUtils.capitalize(childText);
    }

    public int getUses() {
        return this.usesThisTurn;
    }

    @Override
    public PTrigger makeCopy() {
        PTrigger copy = (PTrigger) super.makeCopy();
        copy.usesThisTurn = this.usesThisTurn;
        copy.onUpdateUsesPerTurn();
        return copy;
    }

    protected void propagateUpdate() {
        if (controller != null) {
            controller.onReceiveUpdate();
        }
    }

    public void resetUses() {
        if (!fields.not) {
            forceResetUses();
        }
        else {
            onUpdateUsesPerTurn();
        }
    }

    public PTrigger setAmount(int amount, int upgrade) {
        super.setAmount(amount, upgrade);
        updateUsesAmount();
        onUpdateUsesPerTurn();
        return this;
    }

    public PTrigger setAmount(int amount) {
        super.setAmount(amount);
        updateUsesAmount();
        onUpdateUsesPerTurn();
        return this;
    }

    @Override
    public PTrigger setAmountFromCard() {
        super.setAmountFromCard();
        updateUsesAmount();
        onUpdateUsesPerTurn();
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
        onUpdateUsesPerTurn();
        return this;
    }

    public PTrigger setExtra(int amount) {
        super.setExtra(amount);
        updateUsesAmount();
        onUpdateUsesPerTurn();
        return this;
    }

    public PTrigger setSource(Object card) {
        super.setSource(card);
        if (source instanceof TriggerConnection && controller == null) {
            controller = (TriggerConnection) source;
        }
        return this;
    }

    public PTrigger setUpgrade(int upgrade) {
        super.setUpgrade(upgrade);
        updateUsesAmount();
        onUpdateUsesPerTurn();
        return this;
    }

    public PTrigger setUpgradeExtra(int upgrade) {
        super.setUpgradeExtra(upgrade);
        updateUsesAmount();
        onUpdateUsesPerTurn();
        return this;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerNotBoolean(editor, TEXT.cedit_combat, null);
        fields.registerFBoolean(editor, TEXT.cedit_every, null);
    }

    public PTrigger stack(PSkill<?> other) {
        // Do not update effects if use-based
        if ((fields.forced || baseAmount == -1) && this.childEffect != null && other.getChild() != null) {
            this.childEffect.stack(other.getChild());
        }
        else if (baseAmount > 0 && other.baseAmount > 0) {
            int tempDiff = amount - baseAmount;
            setAmount(baseAmount + other.baseAmount);
            addAmountForCombat(tempDiff, Integer.MAX_VALUE);
        }
        if (baseExtra > 0 && other.baseExtra > 0) {
            setExtra(baseExtra + other.baseExtra);
        }
        if (baseExtra2 > 0 && other.baseExtra2 > 0) {
            setExtra2(baseExtra2 + other.baseExtra2);
        }
        setAmountFromCard();
        forceResetUses();

        return this;
    }

    // Do not trigger card triggers underneath
    public void triggerOnAllyDeath(PCLCard c, PCLCardAlly ally) {
    }

    public void triggerOnAllySummon(PCLCard c, PCLCardAlly ally) {
    }

    public void triggerOnAllyTrigger(PCLCard c, AbstractCreature target, PCLCardAlly ally, PCLCardAlly caller) {
    }

    public void triggerOnAllyWithdraw(PCLCard c, PCLCardAlly ally, boolean triggerEffects) {
    }

    public void triggerOnCreate(AbstractCard c, boolean startOfBattle) {
        if (controller == null) {
            CardTriggerConnection ct = new CardTriggerConnection(this, c);
            ct.initialize();
            controller = ct;
        }
    }

    public void triggerOnDiscard(AbstractCard c) {
    }

    public void triggerOnDraw(AbstractCard c) {
    }

    public boolean triggerOnEndOfTurn(boolean isUsing) {
        return false;
    }

    public void triggerOnExhaust(AbstractCard c) {
    }

    public void triggerOnFetch(AbstractCard c, CardGroup sourcePile) {
    }

    public void triggerOnOtherCardPlayed(AbstractCard c) {
    }

    public void triggerOnPurge(AbstractCard c) {
    }

    public void triggerOnReshuffle(AbstractCard c, CardGroup sourcePile) {
    }

    public void triggerOnRetain(AbstractCard c) {
    }

    public void triggerOnScry(AbstractCard c) {
    }

    public void triggerOnShuffle() {
    }

    public boolean triggerOnStartOfTurn() {
        return false;
    }

    public void triggerOnUpgrade(AbstractCard c) {
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
        if (fields.forced) {
            if (super.tryPassParent(source, info)) {
                usesThisTurn += 1;
                if (usesThisTurn >= amount) {
                    usesThisTurn = 0;
                    onUpdateUsesPerTurn();
                    flash();
                    return true;
                }
                else {
                    onUpdateUsesPerTurn();
                }
            }
            return false;
        }
        if (usesThisTurn != 0) {
            boolean result = super.tryPassParent(source, info);

            if (result) {
                if (usesThisTurn > 0) {
                    usesThisTurn -= 1;
                    onUpdateUsesPerTurn();
                }
                flash();
            }

            return result;
        }
        return false;
    }

    protected void onUpdateUsesPerTurn() {
        if (source instanceof PointerProvider) {
            ((PointerProvider) source).onUpdateUsesPerTurn(this.usesThisTurn);
        }
    }

    // When initialized, treat 0 like -1
    // For every x triggers, instead reset it to 0
    protected void updateUsesAmount() {
        usesThisTurn = fields.forced ? 0 :
                this.amount > 0 ? this.amount : -1;
    }

    // Triggers can only activate through subscribers or clickables
    @Override
    public void use(PCLUseInfo info, PCLActions order) {
    }

    // Called when executed through a clickable
    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if (fields.forced) {
            usesThisTurn += 1;
            if (usesThisTurn >= amount) {
                usesThisTurn = 0;
                onUpdateUsesPerTurn();
                this.childEffect.use(info, order, shouldPay);
                flash();
            }
            else {
                onUpdateUsesPerTurn();
            }
        }
        else if (usesThisTurn != 0) {
            if (usesThisTurn > 0) {
                usesThisTurn -= 1;
                onUpdateUsesPerTurn();
            }
            flash();
            this.childEffect.use(info, order, shouldPay);
        }
    }
}
