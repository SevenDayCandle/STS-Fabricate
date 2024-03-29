package pinacolada.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;

import java.util.ArrayList;

public class CardPreviewList extends ArrayList<EUICardPreview> {

    public CardPreviewList() {
        super();
    }

    public void add(AbstractCard card) {
        add(new EUICardPreview(card));
    }

    public AbstractCard getCard(int index) {
        return get(index).defaultPreview;
    }

    public ArrayList<AbstractCard> getCards() {
        return EUIUtils.mapAsNonnull(this, p -> p.defaultPreview);
    }

    public int getMatchingIndex(AbstractCard card) {
        if (card != null) {
            for (int i = 0; i < size(); i++) {
                if (getCard(i).uuid.equals(card.uuid)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public AbstractCard getUpgrade(int index) {
        return get(index).upgradedPreview;
    }

    public EUICardPreview set(int index, AbstractCard card) {
        return set(index, new EUICardPreview(card));
    }

    public EUICardPreview set(int index, EUICardPreview card) {
        super.set(index, card);
        return card;
    }
}
