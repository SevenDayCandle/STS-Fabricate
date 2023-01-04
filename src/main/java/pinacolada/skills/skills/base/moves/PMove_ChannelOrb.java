package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class PMove_ChannelOrb extends PMove
{
    public static final PSkillData DATA = register(PMove_ChannelOrb.class, PCLEffectType.Orb)
            .setExtra(-1, DEFAULT_MAX)
            .selfTarget();

    public PMove_ChannelOrb()
    {
        this(1);
    }

    public PMove_ChannelOrb(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ChannelOrb(int amount, PCLOrbHelper... orb)
    {
        super(DATA, PCLCardTarget.None, amount, orb);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.channelX("X", TEXT.cardEditor.orbs);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (!orbs.isEmpty())
        {
            if (alt)
            {
                PCLOrbHelper orb = GameUtilities.getRandomElement(orbs);
                if (orb != null)
                {
                    getActions().channelOrbs(orb, amount).addCallback(this::modifyFocus);
                }
            }
            else
            {
                for (PCLOrbHelper orb : orbs)
                {
                    getActions().channelOrbs(orb, amount).addCallback(this::modifyFocus);
                }
            }
        }
        else
        {
            getActions().channelRandomOrbs(amount).addCallback(this::modifyFocus);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String base = (!orbs.isEmpty() ? alt ? getOrbOrString(getRawString(EFFECT_CHAR)) : getOrbAndString(getRawString(EFFECT_CHAR)) : TEXT.subjects.randomX(plural(PGR.core.tooltips.orb)));
        if (extra > 0)
        {
            base = TEXT.subjects.withX(base, EUIRM.strings.numNoun("+" + getExtraRawString(), PGR.core.tooltips.focus.title));
        }
        return alt ? TEXT.subjects.randomly(TEXT.actions.channelX(getAmountRawString(), base))
                : TEXT.actions.channelX(getAmountRawString(), base);
    }

    protected void modifyFocus(List<AbstractOrb> orbs)
    {
        if (extra > 0)
        {
            for (AbstractOrb o : orbs)
            {
                GameUtilities.modifyOrbBaseFocus(o, extra, true, false);
            }
        }
    }
}
