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

public class PMove_SpreadPower extends PMove
{
    public static final PSkillData DATA = register(PMove_SpreadPower.class, PCLEffectType.Power);

    public PMove_SpreadPower()
    {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_SpreadPower(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_SpreadPower(PCLCardTarget target, PCLPowerHelper... powers)
    {
        this(target, 0, powers);
    }

    public PMove_SpreadPower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        super(DATA, target, amount, powers);
    }

    @Override
    public String getSampleText()
    {
        return PGR.core.tooltips.spread.title;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        List<AbstractCreature> targets = getTargetList(info);
        if (powers.isEmpty())
        {
            for (PCLPowerHelper power : PCLPowerHelper.commonDebuffs())
            {
                spreadPower(info.source, targets, power);
            }
        }
        else if (alt)
        {
            PCLPowerHelper power = GameUtilities.getRandomElement(powers);
            if (power != null)
            {
                spreadPower(info.source, targets, power);
            }
        }
        else
        {
            for (PCLPowerHelper power : powers)
            {
                spreadPower(info.source, targets, power);
            }
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String powerString = powers.isEmpty() ? PGR.core.tooltips.debuff.title : alt ? getPowerOrString() : getPowerString();
        String mainString = amount <= 0 ? TEXT.actions.spread(powerString, getTargetString()) : TEXT.actions.spreadAmount(getAmountRawString(), powerString, getTargetString());
        return alt ? TEXT.subjects.randomly(mainString) : mainString;
    }

    protected void spreadPower(AbstractCreature p, List<AbstractCreature> targets, PCLPowerHelper power)
    {
        for (AbstractCreature t : targets)
        {
            getActions().spreadPower(p, t, power.ID, amount);
        }
        // Handle powers that are equivalent in terms of what the player sees but that have different IDs
        if (power == PCLPowerHelper.Intangible)
        {
            for (AbstractCreature t : targets)
            {
                getActions().spreadPower(p, t, IntangiblePower.POWER_ID, amount);
            }
        }
    }
}
