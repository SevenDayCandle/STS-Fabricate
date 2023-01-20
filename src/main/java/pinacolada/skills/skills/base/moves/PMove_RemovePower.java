package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Power;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMove_RemovePower extends PMove<PField_Power>
{
    public static final PSkillData<PField_Power> DATA = register(PMove_RemovePower.class, PField_Power.class);

    public PMove_RemovePower()
    {
        this(PCLCardTarget.Self);
    }

    public PMove_RemovePower(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_RemovePower(PCLCardTarget target, PCLPowerHelper... powers)
    {
        super(DATA, target, 1);
        fields.setPower(powers);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.remove(TEXT.cardEditor.powers);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        List<AbstractCreature> targets = getTargetList(info);
        if (fields.powers.isEmpty())
        {
            for (PCLPowerHelper power : PCLPowerHelper.commonDebuffs())
            {
                for (AbstractCreature t : targets)
                {
                    getActions().removePower(t, t, power.ID);
                }
            }
        }
        else if (fields.random)
        {
            PCLPowerHelper power = GameUtilities.getRandomElement(fields.powers);
            if (power != null)
            {
                removePower(targets, power);
            }
        }
        else
        {
            for (PCLPowerHelper power : fields.powers)
            {
                removePower(targets, power);
            }
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String powerString = fields.getPowerSubjectString();
        powerString = target == PCLCardTarget.Self ? TEXT.actions.remove(TEXT.subjects.onYou(powerString)) : TEXT.actions.removeFrom(powerString, getTargetString());
        return fields.random ? TEXT.subjects.randomly(powerString) : powerString;
    }

    protected void removePower(List<AbstractCreature> targets, PCLPowerHelper power)
    {
        for (AbstractCreature t : targets)
        {
            getActions().removePower(t, t, power.ID);
        }
        // Handle powers that are equivalent in terms of what the player sees but that have different IDs
        if (power == PCLPowerHelper.Intangible)
        {
            for (AbstractCreature t : targets)
            {
                getActions().removePower(t, t, IntangiblePower.POWER_ID);
            }
        }
    }
}
