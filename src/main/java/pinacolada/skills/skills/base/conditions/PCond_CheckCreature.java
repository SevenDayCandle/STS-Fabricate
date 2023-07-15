package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_CheckCreature extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckCreature.class, PField_Not.class);

    public PCond_CheckCreature(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckCreature() {
        super(DATA, PCLCardTarget.AllEnemy, 1);
    }

    public PCond_CheckCreature(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, m -> !GameUtilities.isDeadOrEscaped(m));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.subjects_character);
    }

    @Override
    public String getSubText() {
        String baseString = fields.getThresholdRawString(TEXT.subjects_character);
        return TEXT.cond_thereIs(getAmountRawString(), baseString);
    }
}
