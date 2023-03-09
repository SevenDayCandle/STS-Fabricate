package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.ui.combat.DrawPileCardPreview;

public interface DrawPileCardPreviewProvider
{
    public abstract AbstractCard findCard();
    public void onClick(AbstractCard highlighted);
    default DrawPileCardPreview subscribe()
    {
        return DrawPileCardPreview.subscribe(this);
    }
    default void unsubscribe()
    {
        DrawPileCardPreview.unsubscribe(this);
    }
    default void refreshCard()
    {
        DrawPileCardPreview.refreshCard(this);
    }
}
