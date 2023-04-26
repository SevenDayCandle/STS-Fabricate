package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnAttackSubscriber;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.base.primary.PTrigger_When;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_IsAttacking extends PPassiveCond<PField_Not> implements OnAttackSubscriber
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
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        if (target == PCLCardTarget.Single)
        {
            return fields.not ^ (GameUtilities.isAttacking(info.target));
        }
        return fields.not ^ EUIUtils.any(GameUtilities.getIntents(), i -> fields.not ^ i.isAttacking());
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_whenSingle(PGR.core.tooltips.attack.present()) : TEXT.cond_objIs(TEXT.subjects_target, PGR.core.tooltips.attack.progressive());
    }

    @Override
    public String getSubText()
    {
        if (isWhenClause())
        {
            return getWheneverString(PGR.core.tooltips.attack.present());
        }
        return getTargetIsString(fields.not ? TEXT.cond_not(PGR.core.tooltips.attack.progressive()) : PGR.core.tooltips.attack.progressive());
    }

    // When the owner attacks, triggers the effect on the target
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature t)
    {
        if (info.type == DamageInfo.DamageType.NORMAL && target.getTargets(getOwnerCreature(), t).contains(info.owner))
        {
            useFromTrigger(makeInfo(t));
        }
    }
}
