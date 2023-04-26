package pinacolada.cards.base.modifiers;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

// Modifier for displaying tags if they are applied to a card. Does NOT actually apply any tags
public class TagDisplayModifier extends AbstractCardModifier
{
    public static TagDisplayModifier get(AbstractCard c)
    {
        for (AbstractCardModifier mod : CardModifierManager.modifiers(c))
        {
            if (mod instanceof TagDisplayModifier)
            {
                return (TagDisplayModifier) mod;
            }
        }
        return null;
    }

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
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (PGR.config.displayCardTagDescription.get())
        {
            String tagString = GameUtilities.getTagTipString(card);
            if (!tagString.isEmpty())
            {
                return rawDescription + EUIUtils.DOUBLE_SPLIT_LINE + tagString;
            }
        }
        return rawDescription;
    }

    @Override
    public void onRender(AbstractCard card, SpriteBatch sb) {
        PCLCardTag.renderTagsOnCard(sb, card, card.transparency);
    }
}
