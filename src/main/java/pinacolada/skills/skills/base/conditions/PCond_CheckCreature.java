package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Creature;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_CheckCreature extends PPassiveCond<PField_Creature> {
    public static final PSkillData<PField_Creature> DATA = register(PCond_CheckCreature.class, PField_Creature.class);

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
        int count = fields.creatures.isEmpty() ? getTargetList(info).size() : EUIUtils.count(getTargetList(info), fields::filter);
        return fields.doesValueMatchThreshold(info, count);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return EUIRM.strings.numNoun(TEXT.subjects_x, TEXT.subjects_character);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (fields.creatures.isEmpty()) {
            return TEXT.cond_ifThere(getAmountRawString(), fields.getThresholdRawString(getTargetStringPluralSuffix()));
        }
        return getTargetIsString(getTargetForPerspective(perspective), fields.getString());
    }
}
