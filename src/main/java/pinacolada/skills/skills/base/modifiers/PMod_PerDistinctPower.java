package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerDistinctPower extends PMod_Per<PField_Power> {

    public static final PSkillData<PField_Power> DATA = register(PMod_PerDistinctPower.class, PField_Power.class);

    public PMod_PerDistinctPower(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerDistinctPower() {
        super(DATA);
    }

    public PMod_PerDistinctPower(int amount, PCLPowerHelper... powerHelpers) {
        this(PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_PerDistinctPower(PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers) {
        super(DATA, target, amount);
        fields.setPower(powerHelpers);
    }

    @Override
    public int getModifiedAmount(PSkill<?> be, PCLUseInfo info, boolean isUsing) {
        return be.baseAmount * getMultiplier(info, isUsing);
    }

    public String getConditionText(String childText) {
        if (fields.not) {
            return TEXT.cond_xConditional(childText, TEXT.cond_xPerY(getAmountRawString(), getSubText()));
        }
        return TEXT.cond_xPerY(childText,
                this.amount <= 1 ? getSubText() : EUIRM.strings.numNoun(getAmountRawString(), getSubText()));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_xPerY(TEXT.subjects_x, getSubSampleText());
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_distinct(TEXT.cedit_powers);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        AbstractPower.PowerType targetType = fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF;
        return fields.powers.isEmpty() ?
                sumTargets(info, t -> EUIUtils.count(t.powers, po -> po.type == targetType)) :
                sumTargets(info, t -> EUIUtils.count(fields.powers, po -> GameUtilities.getPowerAmount(t, po.ID) >= this.amount));
    }

    @Override
    public String getSubText() {
        return TEXT.subjects_distinct(getTargetOnString(fields.getPowerSubjectString()));
    }
}
