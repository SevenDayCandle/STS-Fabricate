package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PMove_TriggerAlly extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_TriggerAlly.class, PField_Empty.class)
            .setTargets(PCLCardTarget.AllAlly, PCLCardTarget.RandomAlly, PCLCardTarget.SingleAlly)
            .pclOnly();

    public PMove_TriggerAlly()
    {
        this(1);
    }

    public PMove_TriggerAlly(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_TriggerAlly(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_TriggerAlly(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.trigger(PGR.core.tooltips.summon.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (AbstractCreature t : getTargetList(info))
        {
            if (t instanceof PCLCardAlly)
            {
                getActions().triggerAlly((PCLCardAlly) t, amount);
            }
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return amount == 1 ? TEXT.actions.trigger(getTargetString()) : TEXT.actions.triggerXTimes(getTargetString(), getAmountRawString());
    }
}
