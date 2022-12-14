package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.PTrigger;

public class PTrait_Tag extends PTrait
{

    public static final PSkillData DATA = register(PTrait_Tag.class, PCLEffectType.General);

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
        super(DATA, tags);
    }

    public PTrait_Tag(int amount, PCLCardTag... tags)
    {
        super(DATA, amount, tags);
    }

    @Override
    public String getSubText()
    {
        String tagAmount = amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(), getSubDescText()) : amount < 0 ? EUIRM.strings.numNoun(TEXT.subjects.infinite, getSubDescText()) : getSubDescText();
        return hasParentType(PTrigger.class) ? tagAmount :
                alt ? TEXT.actions.remove(tagAmount) : TEXT.actions.has(tagAmount);
    }

    @Override
    public void applyToCard(AbstractCard c, boolean conditionMet)
    {
        for (PCLCardTag tag : tags)
        {
            tag.set(sourceCard, (alt ^ conditionMet ? 1 : 0) * amount);
        }
    }

    @Override
    public String getSubDescText()
    {
        return getTagAndString();
    }

    @Override
    public String getSubSampleText()
    {
        return TEXT.cardEditor.tags;
    }
}
