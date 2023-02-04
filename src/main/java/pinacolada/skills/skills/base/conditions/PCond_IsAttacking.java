package pinacolada.skills.skills.base.conditions;

import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_IsAttacking extends PPassiveCond<PField_Not>
{
    public static final PSkillData<PField_Not> DATA = register(PCond_IsAttacking.class, PField_Not.class);

    public PCond_IsAttacking(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_IsAttacking()
    {
        super(DATA, PCLCardTarget.AllEnemy, 1);
    }

    public PCond_IsAttacking(PCLCardTarget target)
    {
        super(DATA, target, 1);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (target == PCLCardTarget.Single)
        {
            return fields.not ^ (GameUtilities.isAttacking(info.target));
        }
        return fields.not ^ EUIUtils.any(GameUtilities.getIntents(), i -> fields.not ^ i.isAttacking());
    }

    @Override
    public String getSampleText()
    {
        return TEXT.cond_objIs(TEXT.subjects_target, TEXT.subjects_attacking);
    }

    @Override
    public String getSubText()
    {
        String base = fields.not ? TEXT.cond_not(TEXT.subjects_attacking) : TEXT.subjects_attacking;
        return target == PCLCardTarget.Single ? TEXT.cond_ifTheEnemyIs(base) : TEXT.cond_ifAnyEnemyIs(base);
    }
}
