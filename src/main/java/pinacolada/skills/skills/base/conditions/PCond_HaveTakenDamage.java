package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnLoseHPSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Random;
import pinacolada.skills.skills.PPassiveCond;

@VisibleSkill
public class PCond_HaveTakenDamage extends PPassiveCond<PField_Random> implements OnLoseHPSubscriber
{
    public static final PSkillData<PField_Random> DATA = register(PCond_HaveTakenDamage.class, PField_Random.class)
            .selfTarget();

    public PCond_HaveTakenDamage()
    {
        this(1);
    }

    public PCond_HaveTakenDamage(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_HaveTakenDamage(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        int count = fields.random ? GameActionManager.damageReceivedThisCombat : GameActionManager.damageReceivedThisTurn;
        return amount == 0 ? count == 0 : fields.not ^ count >= amount;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.cond_ifX(TEXT.act_takeDamage(TEXT.subjects_x));
    }

    @Override
    public String getSubText()
    {
        if (isWhenClause())
        {
            return getWheneverString(TEXT.cond_takeDamage(target.ordinal()));
        }
        return fields.random ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(PGR.core.tooltips.pay), EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_damage)) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(PGR.core.tooltips.pay), EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_damage));
    }

    @Override
    public int onLoseHP(AbstractPlayer p, DamageInfo info, int amount)
    {
        if (amount > 0 && (info.type == DamageInfo.DamageType.NORMAL || info.type == DamageInfo.DamageType.THORNS))
        {
            useFromTrigger(makeInfo(info.owner));
        }
        return amount;
    }

    @Override
    public String wrapAmount(int input)
    {
        return input == 0 ? String.valueOf(input) : (fields.not ? (input + "-") : (input + "+"));
    }
}
