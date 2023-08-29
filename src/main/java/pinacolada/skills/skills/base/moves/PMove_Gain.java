package pinacolada.skills.skills.base.moves;

import extendedui.EUIRM;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
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

    public String gainAmountText() {
        return getAmountRawString();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x, gainText());
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        if (isSelfOnlyTarget(perspective)) {
            return amount < 0 ? TEXT.act_loseAmount(gainAmountText(), gainText()) : TEXT.act_gainAmount(gainAmountText(), gainText());
        }
        return amount < 0 ?
                TEXT.act_removeFrom(EUIRM.strings.numNoun(gainAmountText(), gainText()), getTargetStringPerspective(perspective)) :
                TEXT.act_giveTargetAmount(getTargetStringPerspective(perspective), gainAmountText(), gainText());
    }

    @Override
    public boolean isDetrimental() {
        return (target.targetsAllies() || target.targetsSelf()) ? amount < 0 : amount > 0;
    }

    @Override
    public String wrapAmount(int input) {
        return String.valueOf(Math.abs(input));
    }

    public abstract String gainText();
}
