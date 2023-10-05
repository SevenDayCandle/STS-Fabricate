package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_CheckCreatureSummon extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckCreatureSummon.class, PField_Not.class, 1, 1)
            .pclOnly();

    public PCond_CheckCreatureSummon(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckCreatureSummon() {
        super(DATA, PCLCardTarget.Single, 1);
    }

    public PCond_CheckCreatureSummon(PCLCardTarget target) {
        super(DATA, target, 1);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, m -> !GameUtilities.isDeadOrEscaped(m) && m instanceof PCLCardAlly);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.subjects_character);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.cond_ifTargetIs(getTargetStringPerspective(perspective), getTargetOrdinal(target), PGR.core.tooltips.summon.title);
    }
}
