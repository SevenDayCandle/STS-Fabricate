package pinacolada.skills.skills.base.moves;

import extendedui.EUIRM;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public abstract class PMove_Gain extends PMove<PField_Empty> {

    public PMove_Gain(PSkillData<PField_Empty> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMove_Gain(PSkillData<PField_Empty> data, int amount) {
        super(data, PCLCardTarget.Self, amount);
    }

    public PMove_Gain(PSkillData<PField_Empty> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public String gainAmountText(Object requestor) {
        return getAmountRawString(requestor);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x, gainText(null));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isSelfOnlyTarget(perspective)) {
            return amount < 0 ? TEXT.act_loseAmount(gainAmountText(requestor), gainText(requestor)) : TEXT.act_gainAmount(gainAmountText(requestor), gainText(requestor));
        }
        return amount < 0 ?
                TEXT.act_removeFrom(EUIRM.strings.numNoun(gainAmountText(requestor), gainText(requestor)), getTargetStringPerspective(perspective)) :
                TEXT.act_giveTargetAmount(getTargetStringPerspective(perspective), gainAmountText(requestor), gainText(requestor));
    }

    @Override
    public boolean isDetrimental() {
        return (target.targetsAllies() || target.targetsSelf()) ? amount < 0 : amount > 0;
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return String.valueOf(Math.abs(input));
    }

    public abstract String gainText(Object requestor);
}
