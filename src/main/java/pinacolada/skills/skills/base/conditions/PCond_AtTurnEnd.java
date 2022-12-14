package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_AtTurnEnd extends PCond
{

    public static final PSkillData DATA = register(PCond_AtTurnEnd.class, PCLEffectType.General, 1, 1)
            .selfTarget();

    public PCond_AtTurnEnd()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_AtTurnEnd(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_AtTurnEnd(PSkill effect)
    {
        this();
        setChild(effect);
    }

    public PCond_AtTurnEnd(PSkill... effect)
    {
        this();
        setChild(effect);
    }

    // This should not activate the child effect when played normally

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.atEndOfTurn();
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.atEndOfTurn();
    }

    @Override
    public void use(PCLUseInfo info)
    {
    }

    @Override
    public void use(PCLUseInfo info, int index)
    {
    }

    @Override
    public boolean canPlay(AbstractCard card, AbstractMonster m)
    {
        return true;
    }

    @Override
    public boolean triggerOnEndOfTurn(boolean isUsing)
    {
        if (this.childEffect != null && isUsing)
        {
            this.childEffect.use(makeInfo(null));
        }
        return true;
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fromTrigger;
    }
}
