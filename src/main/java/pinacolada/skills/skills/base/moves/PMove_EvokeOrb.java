package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

public class PMove_EvokeOrb extends PMove
{
    public static final PSkillData DATA = register(PMove_EvokeOrb.class, PCLEffectType.Orb)
            .setExtra(0, Integer.MAX_VALUE)
            .selfTarget();

    public PMove_EvokeOrb()
    {
        this(1, 1);
    }

    public PMove_EvokeOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_EvokeOrb(int amount, PCLOrbHelper... orb)
    {
        this(amount, 1, orb);
    }

    public PMove_EvokeOrb(int amount, int orbs, PCLOrbHelper... orb)
    {
        super(DATA, PCLCardTarget.None, amount, orb);
        setExtra(orbs);
    }

    @Override
    public PMove_EvokeOrb onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        card.showEvokeValue = amount > 0;
        card.showEvokeOrbCount = amount;
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.evoke("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().evokeOrb(amount, extra <= 0 ? GameUtilities.getOrbCount() : extra, alt).setFilter(orbs.isEmpty() ? null : getOrbFilter());
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
        return amount == 1 ? TEXT.actions.evoke(orbStr) : TEXT.actions.evokeXTimes(orbStr, getAmountRawString());
    }
}
