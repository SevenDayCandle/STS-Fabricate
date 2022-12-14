package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;

public class PMove_ModifyTag extends PMove_Modify
{
    public static final PSkillData DATA = PMove_Modify.register(PMove_ModifyTag.class, PCLEffectType.Tag)
            .selfTarget();

    public PMove_ModifyTag()
    {
        this(new ArrayList<>());
    }

    public PMove_ModifyTag(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_ModifyTag(ArrayList<AbstractCard> cards, PCLCardTag... tags)
    {
        super(DATA, 1, cards, tags);
    }

    public PMove_ModifyTag(int amount, ArrayList<AbstractCard> cards, PCLCardTag... tags)
    {
        super(DATA, amount, cards, tags);
    }

    public PMove_ModifyTag(int amount, PCLCardTag... tags)
    {
        super(DATA, amount, new ArrayList<>(), tags);
        setExtra(1);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> {
            for (PCLCardTag tag : tags)
            {
                getActions().modifyTag(c, tag, extra, extra != 0);
            }
        };
    }

    @Override
    public String getObjectSampleText()
    {
        return TEXT.cardEditor.tags;
    }

    @Override
    public String getObjectText()
    {
        String base = EUIUtils.joinStrings(" ", EUIUtils.map(tags, PCLCardTag::getTip));
        return extra > 1 ? EUIRM.strings.numNoun(getExtraRawString(), base) : extra < 0 ? EUIRM.strings.numNoun(TEXT.subjects.infinite, base) : base;
    }

    @Override
    public String getSubText()
    {
        if (extra != 0)
        {
            return super.getSubText();
        }
        String giveString = getObjectText();
        return useParent || (cards != null && !cards.isEmpty()) ? TEXT.actions.removeFrom(giveString, getInheritedString()) :
                groupTypes != null && !groupTypes.isEmpty() ?
                        TEXT.actions.removeFromPlace(giveString, EUIRM.strings.numNoun(getAmountRawString(), pluralCard()), getGroupString()) :
                        TEXT.actions.removeFrom(giveString, TEXT.subjects.thisObj);
    }
}
