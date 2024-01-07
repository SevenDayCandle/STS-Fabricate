package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerData;
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

    public PMod_PerDistinctPower(int amount, PCLPowerData... powerHelpers) {
        this(PCLCardTarget.AllEnemy, amount, powerHelpers);
    }

    public PMod_PerDistinctPower(PCLCardTarget target, int amount, PCLPowerData... powerHelpers) {
        super(DATA, target, amount);
        fields.setPower(powerHelpers);
    }

    public String getConditionText(PCLCardTarget perspective, Object requestor, String childText) {
        if (fields.not) {
            return TEXT.cond_xConditional(childText, TEXT.cond_xPerY(getAmountRawString(), getSubText(perspective, requestor)));
        }
        return TEXT.cond_xPerY(childText,
                this.amount <= 1 ? getSubText(perspective, requestor) : EUIRM.strings.numNoun(getAmountRawString(), getSubText(perspective, requestor)));
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        AbstractPower.PowerType targetType = fields.debuff ? AbstractPower.PowerType.DEBUFF : AbstractPower.PowerType.BUFF;
        return fields.powers.isEmpty() ?
                sumTargets(info, t -> EUIUtils.count(t.powers, po -> po.type == targetType)) :
                sumTargets(info, t -> EUIUtils.count(fields.powers, po -> GameUtilities.getPower(t, po) != null));
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
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return TEXT.subjects_distinct(getTargetOnStringPerspective(perspective, fields.getPowerSubjectString()));
    }
}
