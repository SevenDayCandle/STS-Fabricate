package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.PTrigger;
import pinacolada.skills.fields.PField;
import pinacolada.skills.fields.PField_Tag;

public class PTrait_Tag extends PTrait<PField_Tag>
{
    public static final PSkillData<PField_Tag> DATA = register(PTrait_Tag.class, PField_Tag.class);

    public PTrait_Tag()
    {
        this((PCLCardTag) null);
    }

    public PTrait_Tag(PSkillSaveData content)
    {
        super(content);
    }

    public PTrait_Tag(PCLCardTag... tags)
    {
        this(1, tags);
    }

    public PTrait_Tag(int amount, PCLCardTag... tags)
    {
        super(DATA, amount);
        fields.setTag(tags);
    }

    @Override
    public String getSubText()
    {
        String tagAmount = amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(), getSubDescText()) : amount < 0 ? EUIRM.strings.numNoun(TEXT.subjects.infinite, getSubDescText()) : getSubDescText();
        return hasParentType(PTrigger.class) ? tagAmount :
                fields.random ? TEXT.actions.remove(tagAmount) : TEXT.actions.has(tagAmount);
    }

    @Override
    public void applyToCard(AbstractCard c, boolean conditionMet)
    {
        for (PCLCardTag tag : fields.tags)
        {
            tag.set(sourceCard, (fields.random ^ conditionMet ? 1 : 0) * amount);
        }
    }

    @Override
    public String getSubDescText()
    {
        return PField.getTagAndString(fields.tags);
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.cardEditor.tags;
    }
}
