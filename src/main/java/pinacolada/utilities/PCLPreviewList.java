package pinacolada.utilities;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUICardPreview;

import java.util.ArrayList;

public class PCLPreviewList extends RotatingList<EUICardPreview>
{

    public PCLPreviewList() {
        super();
    }

    public AbstractCard getCard(int index) {
        return get(index).defaultPreview;
    }

    public AbstractCard getUpgrade(int index) {
        return get(index).upgradedPreview;
    }

    public void add(AbstractCard card) {
        add(EUICardPreview.generatePreviewCard(card));
    }

    public EUICardPreview set(int index, AbstractCard card) {
        return set(index, EUICardPreview.generatePreviewCard(card));
    }

    public EUICardPreview set(int index, EUICardPreview card) {
        super.set(index, card);
        return card;
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
}
