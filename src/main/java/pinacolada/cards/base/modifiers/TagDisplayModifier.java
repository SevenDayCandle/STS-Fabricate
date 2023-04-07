package pinacolada.cards.base.modifiers;

import basemod.abstracts.AbstractCardModifier;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.tags.PCLCardTag;

// Modifier for displaying tags if they are applied to a card
public class TagDisplayModifier extends AbstractCardModifier
{
    @Override
    public AbstractCardModifier makeCopy()
    {
        return new TagDisplayModifier();
    }

    @Override
    public boolean shouldApply(AbstractCard card) {
        return !(card instanceof PCLCard);
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        PCLCardTag.renderTagsOnCard(sb, card, card.transparency);
    }
}
