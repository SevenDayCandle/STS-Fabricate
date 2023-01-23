package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnLoseHPSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PCond_TakeDamage extends PCond<PField_Empty> implements OnLoseHPSubscriber
{
    public static final PSkillData<PField_Empty> DATA = register(PCond_TakeDamage.class, PField_Empty.class)
            .selfTarget();

    public PCond_TakeDamage(PSkillSaveData content)
    {
        super(DATA, content);
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
        return TEXT.actions.takeDamage(TEXT.subjects.x);
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
    public int onLoseHP(AbstractPlayer p, DamageInfo info, int amount)
    {
        if (info.type == DamageInfo.DamageType.NORMAL && childEffect != null)
        {
            useFromTrigger(makeInfo(info.owner));
        }
        return amount;
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
