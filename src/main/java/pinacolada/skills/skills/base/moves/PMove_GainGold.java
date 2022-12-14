package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_GainGold extends PMove_Gain
{
    public static final PSkillData DATA = register(PMove_GainGold.class, PCLEffectType.General);

    public PMove_GainGold()
    {
        this(1);
    }

    public PMove_GainGold(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_GainGold(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String gainText()
    {
        return PGR.core.tooltips.gold.getTitleOrIcon();
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.gainAmount("X", PGR.core.tooltips.gold.title);
    }

    @Override
    public PMove_GainGold onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (!card.tags.contains(AbstractCard.CardTags.HEALING))
        {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().gainGold(amount);
        super.use(info);
    }
}
