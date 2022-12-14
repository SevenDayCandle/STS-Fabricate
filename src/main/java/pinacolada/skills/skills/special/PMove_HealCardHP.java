package pinacolada.skills.skills.special;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;

public class PMove_HealCardHP extends PMove
{
    public static final PSkillData DATA = register(PMove_HealCardHP.class, PCLEffectType.General);


    public PMove_HealCardHP(PointerProvider card)
    {
        super(DATA, PCLCardTarget.Self, 0);
        setSource(card, PCLCardValueSource.SecondaryNumber);
    }

    @Override
    public PMove_HealCardHP onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (!card.tags.contains(AbstractCard.CardTags.HEALING))
        {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.heal("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().heal(amount);
    }

    @Override
    public String getSubText()
    {
        return null;
    }
}
