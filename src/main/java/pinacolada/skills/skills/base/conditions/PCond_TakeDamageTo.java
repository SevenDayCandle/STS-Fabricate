package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLAction;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PActiveCond;

@VisibleSkill
public class PCond_TakeDamageTo extends PActiveCond<PField_Not>
{
    public static final PSkillData<PField_Not> DATA = register(PCond_TakeDamageTo.class, PField_Not.class)
            .selfTarget();

    public PCond_TakeDamageTo(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_TakeDamageTo()
    {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PCond_TakeDamageTo(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_takeDamage(TEXT.subjects_x);
    }

    @Override
    public String getSubText()
    {
        return fields.not ? TEXT.act_loseAmount(getAmountRawString(), PGR.core.tooltips.hp.title) : TEXT.act_takeDamage(getAmountRawString());
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource)
    {
        return fields.not ? info.source.currentHealth > amount : info.source.currentHealth + info.source.currentBlock > amount;
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, ActionT0 onComplete, ActionT0 onFail)
    {
        return fields.not ? getActions().loseHP(amount, AbstractGameAction.AttackEffect.NONE).addCallback(c -> {
            onComplete.invoke();
        }) : getActions().takeDamage(amount, AbstractGameAction.AttackEffect.NONE).addCallback(c -> {
            onComplete.invoke();
        });
    }
}
