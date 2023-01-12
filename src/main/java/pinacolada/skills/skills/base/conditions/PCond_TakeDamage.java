package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_TakeDamage extends PCond
{
    public static final PSkillData DATA = register(PCond_TakeDamage.class, PField_Empty.class)
            .selfTarget();

    public PCond_TakeDamage(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_TakeDamage()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_TakeDamage(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.takeDamage("X");
    }

    @Override
    public String getSubText()
    {
        if (isTrigger())
        {
            return getWheneverString(TEXT.conditions.takeDamage(target.ordinal()));
        }
        return TEXT.actions.takeDamage(getAmountRawString());
    }

    @Override
    public int triggerOnAttacked(DamageInfo info, int damageAmount)
    {
        if (info.type == DamageInfo.DamageType.NORMAL && childEffect != null)
        {
            childEffect.use(makeInfo(null));
        }
        return damageAmount;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (info.source.currentHealth + info.source.currentBlock <= amount)
        {
            return false;
        }
        if (isUsing)
        {
            getActions().takeDamage(amount, AbstractGameAction.AttackEffect.NONE);
        }
        return true;
    }
}
