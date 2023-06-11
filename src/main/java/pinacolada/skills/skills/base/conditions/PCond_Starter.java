package pinacolada.skills.skills.base.conditions;

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
public class PCond_Starter extends PPassiveCond<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PCond_Starter.class, PField_Not.class, 1, 1)
            .selfTarget();

    public PCond_Starter() {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_Starter(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return fields.not ^ info.isStarter;
    }

    @Override
    public String getSubText() {
        return TEXT.cond_ifX(fields.not ? TEXT.cond_not(PGR.core.tooltips.starter.title) : PGR.core.tooltips.starter.title);
    }
}
