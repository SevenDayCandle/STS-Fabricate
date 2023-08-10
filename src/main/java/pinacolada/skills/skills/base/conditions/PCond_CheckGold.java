package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckGold extends PPassiveCond<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PCond_CheckGold.class, PField_Not.class).selfTarget();

    public PCond_CheckGold(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_CheckGold() {
        super(DATA, PCLCardTarget.Self, 1);
    }

    public PCond_CheckGold(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, m -> fields.doesValueMatchThreshold(m.gold));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, PGR.core.tooltips.gold.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        String baseString = fields.getThresholdRawString(PGR.core.tooltips.gold.title);
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_gain(baseString), perspective);
        }

        return getTargetHasStringPerspective(perspective, baseString);
    }
}
