package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnAttackSubscriber;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Random;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.skills.skills.base.primary.PTrigger_When;

@VisibleSkill
public class PCond_HaveTakenDamage extends PPassiveCond<PField_Random> implements OnAttackSubscriber
{
    public static final PSkillData<PField_Random> DATA = register(PCond_HaveTakenDamage.class, PField_Random.class);

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
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        int count = fields.random ? GameActionManager.damageReceivedThisCombat : GameActionManager.damageReceivedThisTurn;
        return amount == 0 ? count == 0 : fields.not ^ count >= amount;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_whenSingle(TEXT.act_takeDamage(TEXT.subjects_x)) : TEXT.cond_ifX(TEXT.act_takeDamage(TEXT.subjects_x));
    }

    @Override
    public String getSubText()
    {
        if (isWhenClause())
        {
            return getWheneverAreString(PGR.core.tooltips.attack.past());
        }
        String base = TEXT.cond_ifTargetTook(TEXT.subjects_you, EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_damage));
        return fields.random ? TEXT.subjects_thisCombat(base) : TEXT.subjects_thisTurn(base);
    }

    // When the owner receives damages, triggers the effect onto the attacker
    @Override
    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature t)
    {
        if (info.type == DamageInfo.DamageType.NORMAL && target.getTargets(getOwnerCreature(), t).contains(t))
        {
            useFromTrigger(makeInfo(info.owner));
        }
    }

    @Override
    public String wrapAmount(int input)
    {
        return input == 0 ? String.valueOf(input) : (fields.not ? (input + "-") : (input + "+"));
    }
}
