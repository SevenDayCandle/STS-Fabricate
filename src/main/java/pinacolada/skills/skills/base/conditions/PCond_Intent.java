package pinacolada.skills.skills.base.conditions;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLIntentType;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Intent;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_Intent extends PPassiveCond<PField_Intent> {
    public static final PSkillData<PField_Intent> DATA = register(PCond_Intent.class, PField_Intent.class, 1, 1);

    public PCond_Intent(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_Intent() {
        super(DATA, PCLCardTarget.AllEnemy, 1);
    }

    public PCond_Intent(PCLCardTarget target) {
        super(DATA, target, 1);
    }

    public PCond_Intent(PCLCardTarget target, PCLIntentType... intents) {
        super(DATA, target, 1);
        fields.setIntent(intents);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return evaluateTargets(info, t -> fields.hasIntent(t));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_ifX(TEXT.subjects_intent);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return getTargetIsString(getTargetForPerspective(perspective), fields.not ? TEXT.cond_not(fields.getAnyIntentString()) : fields.getAnyIntentString());
    }
}
