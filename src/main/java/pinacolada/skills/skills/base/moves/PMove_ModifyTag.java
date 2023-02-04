package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardModifyTag;

@VisibleSkill
public class PMove_ModifyTag extends PMove_Modify<PField_CardModifyTag>
{
    public static final PSkillData<PField_CardModifyTag> DATA = PMove_Modify.register(PMove_ModifyTag.class, PField_CardModifyTag.class)
            .setExtra(-PCLAffinity.MAX_LEVEL, PCLAffinity.MAX_LEVEL)
            .selfTarget()
            .pclOnly();

    public PMove_ModifyTag()
    {
        this(1, 1);
    }

    public PMove_ModifyTag(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_ModifyTag(int amount, int extra, PCLCardTag... tags)
    {
        super(DATA, amount, extra);
        fields.setAddTag(tags);
    }

    @Override
    public ActionT1<AbstractCard> getAction()
    {
        return (c) -> {
            for (PCLCardTag tag : fields.addTags)
            {
                getActions().modifyTag(c, tag, extra, extra != 0);
            }
        };
    }

    @Override
    public String getObjectSampleText()
    {
        return TEXT.cedit_tags;
    }

    @Override
    public String getObjectText()
    {
        String base = fields.getAddTagChoiceString();
        return extra > 1 ? EUIRM.strings.numNoun(getExtraRawString(), base) : extra < 0 ? EUIRM.strings.numNoun(TEXT.subjects_infinite, base) : base;
    }

    @Override
    public String getSubText()
    {
        if (extra != 0)
        {
            return super.getSubText();
        }
        String giveString = getObjectText();
        return useParent ? TEXT.act_removeFrom(giveString, getInheritedString()) :
                fields.hasGroups() ?
                        TEXT.act_removeFromPlace(giveString, EUIRM.strings.numNoun(getAmountRawString(), pluralCard()), fields.getGroupString()) :
                        TEXT.act_removeFrom(giveString, TEXT.subjects_thisObj);
    }
}
