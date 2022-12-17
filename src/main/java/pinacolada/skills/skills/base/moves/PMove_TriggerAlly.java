package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public class PMove_TriggerAlly extends PMove
{
    public static final PSkillData DATA = register(PMove_TriggerAlly.class, PCLEffectType.General)
            .setTargets(PCLCardTarget.AllAlly, PCLCardTarget.RandomAlly, PCLCardTarget.SingleAlly)
            .pclOnly();

    public PMove_TriggerAlly()
    {
        this(1);
    }

    public PMove_TriggerAlly(PSkillSaveData content)
    {
        super(content);
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
        List<AbstractCreature> targets = getTargetList(info);
        for (AbstractCreature t : targets)
        {
            if (t instanceof PCLCardAlly)
            {
                getActions().triggerAlly((PCLCardAlly) t);
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
