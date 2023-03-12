package pinacolada.skills.skills.base.conditions;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.subscribers.OnStartOfTurnPostDrawSubscriber;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PPassiveNonCheckCond;

@VisibleSkill
public class PCond_AtTurnStart extends PPassiveNonCheckCond<PField_Empty> implements OnStartOfTurnPostDrawSubscriber
{

    public static final PSkillData<PField_Empty> DATA = register(PCond_AtTurnStart.class, PField_Empty.class, 1, 1)
            .selfTarget();

    public PCond_AtTurnStart()
    {
        super(DATA, PCLCardTarget.None, 0);
    }

    public PCond_AtTurnStart(PSkillData<PField_Empty> data, PSkillSaveData content)
    {
        super(DATA, content);
    }

    // This should not activate the child effect when played normally

    @Override
    public String getSampleText()
    {
        return TEXT.cond_atStartOfTurn();
    }

    @Override
    public String getSubText()
    {
        return isWhenClause() ? TEXT.cond_atStartOfTurn() : TEXT.cond_onGeneric(PGR.core.tooltips.retain.title);
    }

    @Override
    public void onStartOfTurnPostDraw()
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
