package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMove_StabilizePower extends PMove<PField_Power>
{
    public static final PSkillData<PField_Power> DATA = register(PMove_StabilizePower.class, PField_Power.class);

    public PMove_StabilizePower()
    {
        this(PCLCardTarget.Self, 1);
    }

    public PMove_StabilizePower(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_StabilizePower(PCLCardTarget target, PCLPowerHelper... powers)
    {
        this(target, 1, powers);
    }

    public PMove_StabilizePower(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        super(DATA, target, amount);
        fields.setPower(powers);
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
        if (fields.powers.isEmpty())
        {
            for (PCLPowerHelper power : PCLPowerHelper.commonDebuffs())
            {
                stabilizePower(info.source, targets, power);
            }
        }
        else if (fields.random)
        {
            PCLPowerHelper power = GameUtilities.getRandomElement(fields.powers);
            if (power != null)
            {
                stabilizePower(info.source, targets, power);
            }
        }
        else
        {
            for (PCLPowerHelper power : fields.powers)
            {
                stabilizePower(info.source, targets, power);
            }
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String powerString = fields.getPowerSubjectString();
        String mainString = TEXT.act_stabilize(powerString, getTargetString());
        if (amount != 1)
        {
            mainString = (TEXT.cond_forTurns(getAmountRawString()) + ", " + mainString);
        }
        return fields.random ? TEXT.subjects_randomly(mainString) : mainString;
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
