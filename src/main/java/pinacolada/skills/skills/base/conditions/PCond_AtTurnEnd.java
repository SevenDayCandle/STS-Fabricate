package pinacolada.skills.skills.base.conditions;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnEndOfTurnFirstSubscriber;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveNonCheckCond;
import pinacolada.skills.skills.base.primary.PTrigger_When;

@VisibleSkill
public class PCond_AtTurnEnd extends PPassiveNonCheckCond<PField_Empty> implements OnEndOfTurnFirstSubscriber
{

    public static final PSkillData<PField_Empty> DATA = register(PCond_AtTurnEnd.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PCond_AtTurnEnd()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_AtTurnEnd(PSkillSaveData content)
    {
        super(DATA, content);
    }

    // This should not activate the child effect when played normally

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return callingSkill instanceof PTrigger_When ? TEXT.cond_atEndOfTurn() : TEXT.cond_inXAtTurnEnd(TEXT.cpile_hand);
    }

    @Override
    public String getSubText()
    {
        return isWhenClause() ? TEXT.cond_atEndOfTurn() : TEXT.cond_inXAtTurnEnd(TEXT.cpile_hand);
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
    public boolean canPlay(PCLUseInfo info)
    {
        return true;
    }
}
