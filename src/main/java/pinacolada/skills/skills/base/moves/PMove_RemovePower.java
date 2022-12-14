package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.IntangiblePower;
import extendedui.EUIRM;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PMove_RemovePower extends PMove
{
    public static final PSkillData DATA = register(PMove_RemovePower.class, PCLEffectType.Power);

    public PMove_RemovePower()
    {
        this(PCLCardTarget.Self);
    }

    public PMove_RemovePower(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_RemovePower(PCLCardTarget target, PCLPowerHelper... powers)
    {
        super(DATA, target, 1, powers);
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
        if (powers.isEmpty())
        {
            for (PCLPowerHelper power : PCLPowerHelper.commonDebuffs())
            {
                for (AbstractCreature t : targets)
                {
                    getActions().removePower(t, t, power.ID);
                }
            }
        }
        else if (alt)
        {
            PCLPowerHelper power = GameUtilities.getRandomElement(powers);
            if (power != null)
            {
                removePower(targets, power);
            }
        }
        else
        {
            for (PCLPowerHelper power : powers)
            {
                removePower(targets, power);
            }
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String powerString = powers.isEmpty() ? EUIRM.strings.numNoun(1, PGR.core.tooltips.debuff.title) : alt ? getPowerOrString() : getPowerString();
        powerString = target == PCLCardTarget.Self ? TEXT.actions.remove(TEXT.subjects.onYou(powerString)) : TEXT.actions.removeFrom(powerString, getTargetString());
        return alt ? TEXT.subjects.randomly(powerString) : powerString;
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
