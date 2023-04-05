package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnLoseHPSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_LoseHP extends PPassiveCond<PField_Empty> implements OnLoseHPSubscriber
{
    public static final PSkillData<PField_Empty> DATA = register(PCond_LoseHP.class, PField_Empty.class)
            .selfTarget();

    public PCond_LoseHP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_LoseHP()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_LoseHP(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_loseAmount(TEXT.subjects_x, PGR.core.tooltips.hp.title);
    }

    @Override
    public String getSubText()
    {
        if (isWhenClause())
        {
            return getWheneverString(TEXT.act_lose(PGR.core.tooltips.hp.title));
        }
        return TEXT.act_loseAmount(getAmountRawString(), PGR.core.tooltips.hp.title);
    }

    @Override
    public int onLoseHP(AbstractPlayer p, DamageInfo info, int amount)
    {
        if (amount > 0 && info.type == DamageInfo.DamageType.HP_LOSS)
        {
            useFromTrigger(makeInfo(info.owner));
        }
        return amount;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        if (info.source.currentHealth <= amount)
        {
            return false;
        }
        if (isUsing && !isWhenClause())
        {
            getActions().loseHP(amount, AbstractGameAction.AttackEffect.NONE);
        }
        return true;
    }
}
