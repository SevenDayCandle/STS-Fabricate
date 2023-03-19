package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.utilities.ColoredString;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModifyAffinity;

import java.util.List;

@VisibleSkill
public class PMove_ModifyAffinity extends PMove_Modify<PField_CardModifyAffinity>
{
    public static final PSkillData<PField_CardModifyAffinity> DATA = PMove_Modify.register(PMove_ModifyAffinity.class, PField_CardModifyAffinity.class)
            .setAmounts(-PCLAffinity.MAX_LEVEL, PCLAffinity.MAX_LEVEL)
            .setExtra(0, DEFAULT_MAX)
            .selfTarget()
            .pclOnly();

    public PMove_ModifyAffinity()
    {
        this(1, 1);
    }

    public PMove_ModifyAffinity(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_ModifyAffinity(PCLAffinity... affinities)
    {
        this(1, affinities);
    }

    public PMove_ModifyAffinity(int amount, PCLAffinity... affinities)
    {
        this(amount, 1, affinities);
    }

    public PMove_ModifyAffinity(int amount, int extra, PCLAffinity... affinities)
    {
        super(DATA, amount, extra);
        fields.setAddAffinity(affinities);
    }

    @Override
    public void cardAction(List<AbstractCard> cards)
    {
        if (fields.or && fields.addAffinities.size() > 1)
        {
            chooseEffect(cards, fields.addAffinities);
        }
        else
        {
            super.cardAction(cards);
        }
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> getActions().modifyAffinityLevel(c, fields.addAffinities, amount, true, fields.forced);
    }

    @Override
    public String getObjectSampleText()
    {
        return TEXT.sui_affinities;
    }

    @Override
    public String getObjectText()
    {
        String base = fields.getAddAffinityChoiceString();
        return extra > 1 ? EUIRM.strings.numNoun(getAmountRawString(), base) : base;
    }

    @Override
    public String getSubText()
    {
        String giveString = getObjectText();
        if (fields.forced)
        {
            return useParent ? TEXT.act_setTheOf(PGR.core.tooltips.affinityGeneral, getInheritedString(), giveString) :
                    fields.hasGroups() ?
                            TEXT.act_setTheOfFrom(PGR.core.tooltips.affinityGeneral, EUIRM.strings.numNoun(amount <= 0 ? TEXT.subjects_all : getExtraRawString(), pluralCard()), fields.getGroupString(), giveString) :
                            TEXT.act_setTheOf(PGR.core.tooltips.affinityGeneral, TEXT.subjects_thisObj, giveString);
        }
        if (extra >= 0)
        {
            return super.getSubText();
        }
        return useParent ? TEXT.act_removeFrom(giveString, getInheritedString()) :
                fields.hasGroups() ?
                        TEXT.act_removeFromPlace(giveString, EUIRM.strings.numNoun(amount <= 0 ? TEXT.subjects_all : getExtraRawString(), pluralCard()), fields.getGroupString()) :
                        TEXT.act_removeFrom(giveString, TEXT.subjects_thisObj);
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
