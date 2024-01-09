package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveMod;
import pinacolada.utilities.GameUtilities;

// While paying X energy is active, the effect can be determined before you actually pay the power
@VisibleSkill
public class PMod_XEnergy extends PPassiveMod<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMod_XEnergy.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .noTarget();

    public PMod_XEnergy(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_XEnergy() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PMod_XEnergy(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public int getModifiedAmount(PCLUseInfo info, int baseAmount, boolean isUsing) {
        return baseAmount * GameUtilities.getXCostEnergy(EUIUtils.safeCast(source, AbstractCard.class)) + this.amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_pay(TEXT.subjects_x, PGR.core.tooltips.energy.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return PGR.core.tooltips.energy.getTitleOrIcon();
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return childEffect != null ? childEffect.getText(perspective, requestor, addPeriod) : EUIUtils.EMPTY_STRING;
    }

    @Override
    public String getThemString() {
        return PCLCoreStrings.pluralForce(TEXT.subjects_themX);
    }

    @Override
    public String getTheyString() {
        return PCLCoreStrings.pluralForce(TEXT.subjects_theyX);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        order.callback(() -> {
            if (this.childEffect != null) {
                this.childEffect.use(info, order, shouldPay);
                if (source instanceof AbstractCard) {
                    GameUtilities.useXCostEnergy((AbstractCard) source);
                }
            }
        });
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(() -> {
            if (this.childEffect != null) {
                this.childEffect.use(info, order);
                if (source instanceof AbstractCard) {
                    GameUtilities.useXCostEnergy((AbstractCard) source);
                }
            }
        });
    }

    @Override
    public String wrapTextAmountChild(String input) {
        // If the value is not parseable, don't remove the numbers
        int value = EUIUtils.parseInt(input, 2);
        if (value == 1) {
            input = EUIUtils.EMPTY_STRING;
        }
        input = this.amount > 0 ? input + TEXT.subjects_x + "+" + this.amount : input + TEXT.subjects_x;
        return parent != null ? parent.wrapTextAmountChild(input) : super.wrapTextAmountChild(input);
    }
}
