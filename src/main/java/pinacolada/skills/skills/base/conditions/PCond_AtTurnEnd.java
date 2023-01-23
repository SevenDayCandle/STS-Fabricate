package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnEndOfTurnFirstSubscriber;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PCond_AtTurnEnd extends PCond<PField_Empty> implements OnEndOfTurnFirstSubscriber
{

    public static final PSkillData<PField_Empty> DATA = register(PCond_AtTurnEnd.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PCond_AtTurnEnd()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_AtTurnEnd(PSkillData<PField_Empty> data, PSkillSaveData content)
    {
        super(DATA, content);
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
    public void onEndOfTurnFirst(boolean isPlayer)
    {
        useFromTrigger(makeInfo(null));
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
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fromTrigger;
    }
}
