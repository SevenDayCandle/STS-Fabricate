package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;
import java.util.List;

public class PMove_ModifyAffinity extends PMove_Modify
{
    public static final PSkillData DATA = PMove_Modify.register(PMove_ModifyAffinity.class, PCLEffectType.CardGroupAffinity)
            .pclOnly();

    public PMove_ModifyAffinity()
    {
        this(1, 1, new ArrayList<>());
    }

    public PMove_ModifyAffinity(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ModifyAffinity(int amount, int level, ArrayList<AbstractCard> cards, PCLAffinity... affinities)
    {
        super(DATA, amount, cards, affinities);
        setExtra(level);
    }

    public PMove_ModifyAffinity(PCLAffinity... affinities)
    {
        this(1, affinities);
    }

    public PMove_ModifyAffinity(int level, PCLAffinity... affinities)
    {
        this(1, level, affinities);
    }

    public PMove_ModifyAffinity(int amount, int level, PCLAffinity... affinities)
    {
        super(DATA, amount, new ArrayList<>(), affinities);
        setExtra(level);
    }

    @Override
    public void cardAction(List<AbstractCard> cards)
    {
        if (alt && affinities.size() > 1)
        {
            chooseEffect(cards, affinities);
        }
        else
        {
            super.cardAction(cards);
        }
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> {
            getActions().modifyAffinityLevel(c, affinities, extra, true, alt2);
        };
    }

    @Override
    public String getObjectSampleText()
    {
        return TEXT.seriesUI.affinities;
    }

    @Override
    public String getObjectText()
    {
        String base = alt ? getAffinityOrString() : getAffinityAndString();
        return extra > 1 ? EUIRM.strings.numNoun(getExtraRawString(), base) : base;
    }

    @Override
    public String getSubText()
    {
        String giveString = getObjectText();
        if (alt2)
        {
            return useParent || (cards != null && !cards.isEmpty()) ? TEXT.actions.setTheOf(PGR.core.tooltips.affinityGeneral, getInheritedString(), giveString) :
                    groupTypes != null && !groupTypes.isEmpty() ?
                            TEXT.actions.setTheOfFrom(PGR.core.tooltips.affinityGeneral, EUIRM.strings.numNoun(amount <= 0 ? TEXT.subjects.all : getAmountRawString(), pluralCard()), getGroupString(), giveString) :
                            TEXT.actions.setTheOf(PGR.core.tooltips.affinityGeneral, TEXT.subjects.thisObj, giveString);
        }
        if (extra >= 0)
        {
            return super.getSubText();
        }
        return useParent || (cards != null && !cards.isEmpty()) ? TEXT.actions.removeFrom(giveString, getInheritedString()) :
                groupTypes != null && !groupTypes.isEmpty() ?
                        TEXT.actions.removeFromPlace(giveString, EUIRM.strings.numNoun(amount <= 0 ? TEXT.subjects.all : getAmountRawString(), pluralCard()), getGroupString()) :
                        TEXT.actions.removeFrom(giveString, TEXT.subjects.thisObj);
    }

    public void chooseEffect(List<AbstractCard> cards, List<PCLAffinity> choices)
    {
        getActions().tryChooseAffinitySkill(getName(), amount, getSourceCreature(), null, EUIUtils.map(choices, a -> PMove.modifyAffinity(amount, a)));
    }

    @Override
    public ColoredString getColoredExtraString()
    {
        return getColoredValueString(Math.abs(baseExtra), Math.abs(extra));
    }
}
