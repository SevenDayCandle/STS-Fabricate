package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.utilities.ColoredString;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.CooldownProgressAction;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.CooldownProvider;
import pinacolada.interfaces.subscribers.OnCooldownTriggeredSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.skills.skills.PBranchCond;

// TODO move to primary
@VisibleSkill
public class PCond_Cooldown extends PActiveCond<PField_Empty> implements CooldownProvider, OnCooldownTriggeredSubscriber {
    public static final PSkillData<PField_Empty> DATA = register(PCond_Cooldown.class, PField_Empty.class)
            .selfTarget();

    public PCond_Cooldown(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_Cooldown() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_Cooldown(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    // Must return true when using or cooldown will not progress in a multicond
    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return isUsing || getCooldown() <= 0;
    }

    @Override
    public int getBaseCooldown() {
        return baseAmount;
    }

    @Override
    public int getCooldown() {
        return amount;
    }

    @Override
    public boolean isDisplayingUpgrade() {
        return displayUpgrades && getUpgrade() != 0;
    }

    @Override
    public void setCooldown(int value) {
        this.amount = value;
    }

    @Override
    public final ColoredString getColoredValueString() {
        return getCooldownString();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return isUnderWhen(callingSkill) ? TEXT.cond_whenSingle(TEXT.act_trigger(PGR.core.tooltips.cooldown.title)) : EUIRM.strings.generic2(PGR.core.tooltips.cooldown.title, TEXT.subjects_x);
    }

    @Override
    public String getSubText() {
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_trigger(PGR.core.tooltips.cooldown.title));
        }
        return EUIRM.strings.generic2(PGR.core.tooltips.cooldown.title, getAmountRawString());
    }

    @Override
    public String getText(boolean addPeriod) {
        return getConditionRawString() + (childEffect != null ? ((childEffect instanceof PCond && !(childEffect instanceof PBranchCond) ? EFFECT_SEPARATOR : ": ") + childEffect.getText(addPeriod)) : "");
    }

    @Override
    public PCond_Cooldown onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        return this;
    }

    @Override
    public PCond_Cooldown onRemoveFromCard(AbstractCard card) {
        super.onRemoveFromCard(card);
        return this;
    }

    // No-op to avoid refreshing effects changing amount
    public PCond_Cooldown setTemporaryAmount(int amount) {
        return this;
    }

    @Override
    public boolean onCooldownTriggered(CooldownProvider cooldown, AbstractCreature s, AbstractCreature m) {
        if (cooldown.canActivate()) {
            useFromTrigger(makeInfo(m));
        }
        return true;
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        return order.add(new CooldownProgressAction(this, info.source, info.target, 1))
                .addCallback(result -> {
                    if (result) {
                        onComplete.invoke(info);
                    }
                    else {
                        onFail.invoke(info);
                    }
                });
    }
}
