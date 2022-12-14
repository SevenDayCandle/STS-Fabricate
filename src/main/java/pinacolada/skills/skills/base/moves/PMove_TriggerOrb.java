package pinacolada.skills.skills.base.moves;

import extendedui.EUIRM;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

public class PMove_TriggerOrb extends PMove
{
    public static final PSkillData DATA = register(PMove_TriggerOrb.class, PCLEffectType.Orb)
            .setExtra(0, Integer.MAX_VALUE)
            .selfTarget();

    public PMove_TriggerOrb()
    {
        this(1, 1);
    }

    public PMove_TriggerOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_TriggerOrb(int amount, PCLOrbHelper... orb)
    {
        this(amount, 1, orb);
    }

    public PMove_TriggerOrb(int amount, int extra, PCLOrbHelper... orb)
    {
        super(DATA, PCLCardTarget.None, amount, orb);
        setExtra(extra);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.trigger("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().triggerOrbPassive(amount, extra <= 0 ? GameUtilities.getOrbCount() : extra, alt)
                .setFilter(orbs.isEmpty() ? null : getOrbFilter());

        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String orbStr = !orbs.isEmpty() ? getOrbString() : plural(PGR.core.tooltips.orb, EXTRA_CHAR);
        if (alt)
        {
            orbStr = EUIRM.strings.numNoun(getExtraRawString(), TEXT.subjects.randomX(orbStr));
        }
        else
        {
            if (extra > 0)
            {
                orbStr = EUIRM.strings.numNoun(getExtraRawString(), orbStr);
            }
            orbStr = extra <= 0 ? TEXT.subjects.allX(orbStr) : TEXT.subjects.yourFirst(orbStr);
        }
        return amount == 1 ? TEXT.actions.trigger(orbStr) : TEXT.actions.triggerXTimes(orbStr, getAmountRawString());
    }
}
