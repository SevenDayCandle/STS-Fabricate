package pinacolada.skills.skills.base.conditions;

import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.utilities.GameUtilities;

public class PCond_IsAttacking extends PCond<PField_Not>
{
    public static final PSkillData<PField_Not> DATA = register(PCond_IsAttacking.class, PField_Not.class);

    public PCond_IsAttacking(PSkillSaveData content)
    {
        super(content);
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
        return TEXT.conditions.objIs(TEXT.subjects.target, TEXT.subjects.attacking);
    }

    @Override
    public String getSubText()
    {
        String base = fields.not ? TEXT.conditions.not(TEXT.subjects.attacking) : TEXT.subjects.attacking;
        return target == PCLCardTarget.Single ? TEXT.conditions.ifTheEnemyIs(base) : TEXT.conditions.ifAnyEnemyIs(base);
    }
}
