package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PMove_StabilizePower extends PMove
{
    public static final PSkillData DATA = register(PMove_StabilizePower.class, PCLEffectType.Power);

    public PMove_StabilizePower()
    {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_StabilizePower(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_StabilizePower(PCLCardTarget target, PCLPowerHelper... powers)
    {
        this(target, 0, powers);
    }

    public PMove_StabilizePower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        super(DATA, target, amount, powers);
    }

    @Override
    public String getSampleText()
    {
        return PGR.core.tooltips.stabilize.title;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        List<AbstractCreature> targets = getTargetList(info);
        if (powers.isEmpty())
        {
            for (PCLPowerHelper power : PCLPowerHelper.commonDebuffs())
            {
                stabilizePower(info.source, targets, power);
            }
        }
        else if (alt)
        {
            PCLPowerHelper power = GameUtilities.getRandomElement(powers);
            if (power != null)
            {
                stabilizePower(info.source, targets, power);
            }
        }
        else
        {
            for (PCLPowerHelper power : powers)
            {
                stabilizePower(info.source, targets, power);
            }
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String powerString = powers.isEmpty() ? PGR.core.tooltips.debuff.title : alt ? getPowerOrString() : getPowerString();
        String mainString = TEXT.actions.stabilize(powerString, getTargetString());
        if (amount >= 2)
        {
            mainString = (TEXT.conditions.forTurns(getAmountRawString()) + ", " + mainString);
        }
        return alt ? TEXT.subjects.randomly(mainString) : mainString;
    }

    protected void stabilizePower(AbstractCreature p, List<AbstractCreature> targets, PCLPowerHelper power)
    {
        for (AbstractCreature t : targets)
        {
            getActions().stabilizePower(p, t, power.ID, amount);
        }
        // Handle powers that are equivalent in terms of what the player sees but that have different IDs
        if (power == PCLPowerHelper.Intangible)
        {
            for (AbstractCreature t : targets)
            {
                getActions().stabilizePower(p, t, IntangiblePower.POWER_ID, amount);
            }
        }
    }
}
