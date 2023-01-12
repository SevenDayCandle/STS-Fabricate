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
import pinacolada.skills.fields.PField_CardModifyAffinity;

import java.util.List;

public class PMove_ModifyAffinity extends PMove_Modify<PField_CardModifyAffinity>
{
    public static final PSkillData<PField_CardModifyAffinity> DATA = PMove_Modify.register(PMove_ModifyAffinity.class, PField_CardModifyAffinity.class)
            .setExtra(-PCLAffinity.MAX_LEVEL, PCLAffinity.MAX_LEVEL)
            .selfTarget()
            .pclOnly();

    public PMove_ModifyAffinity()
    {
        this(1, 1);
    }

    public PMove_ModifyAffinity(PSkillSaveData content)
    {
        super(content);
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
        super(DATA, amount, level);
        fields.setAddAffinity(affinities);
    }

    @Override
    public void cardAction(List<AbstractCard> cards)
    {
        if (fields.random && fields.affinities.size() > 1)
        {
            chooseEffect(cards, fields.affinities);
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
            getActions().modifyAffinityLevel(c, fields.affinities, extra, true, fields.forced);
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
        String base = fields.getAddAffinityChoiceString();
        return extra > 1 ? EUIRM.strings.numNoun(getExtraRawString(), base) : base;
    }

    @Override
    public String getSubText()
    {
        String giveString = getObjectText();
        if (fields.forced)
        {
            return useParent ? TEXT.actions.setTheOf(PGR.core.tooltips.affinityGeneral, getInheritedString(), giveString) :
                    fields.hasGroups() ?
                            TEXT.actions.setTheOfFrom(PGR.core.tooltips.affinityGeneral, EUIRM.strings.numNoun(amount <= 0 ? TEXT.subjects.all : getAmountRawString(), pluralCard()), fields.getGroupString(), giveString) :
                            TEXT.actions.setTheOf(PGR.core.tooltips.affinityGeneral, TEXT.subjects.thisObj, giveString);
        }
        if (extra >= 0)
        {
            return super.getSubText();
        }
        return useParent ? TEXT.actions.removeFrom(giveString, getInheritedString()) :
                fields.hasGroups() ?
                        TEXT.actions.removeFromPlace(giveString, EUIRM.strings.numNoun(amount <= 0 ? TEXT.subjects.all : getAmountRawString(), pluralCard()), fields.getGroupString()) :
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
