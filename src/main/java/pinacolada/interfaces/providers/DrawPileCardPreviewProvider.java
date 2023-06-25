package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.ui.combat.DrawPileCardPreview;

public interface DrawPileCardPreviewProvider {
    default void refreshCard() {
        DrawPileCardPreview.refreshCard(this);
    }

    default DrawPileCardPreview subscribe() {
        return DrawPileCardPreview.subscribe(this);
    }

    default void unsubscribe() {
        DrawPileCardPreview.unsubscribe(this);
    }

    AbstractCard findCard();

    void onClick(AbstractCard highlighted);
}
