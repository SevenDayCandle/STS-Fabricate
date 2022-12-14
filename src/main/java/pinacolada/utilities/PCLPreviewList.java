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
        return getInnerList().get(index).defaultPreview;
    }

    public AbstractCard getUpgrade(int index) {
        return getInnerList().get(index).upgradedPreview;
    }

    public void add(AbstractCard card) {
        add(EUICardPreview.generatePreviewCard(card));
    }

    public void set(int index, AbstractCard card) {
        set(index, EUICardPreview.generatePreviewCard(card));
    }

    public void set(int index, EUICardPreview card) {
        getInnerList().set(index, card);
    }

    public ArrayList<AbstractCard> getCards() {
        return EUIUtils.mapAsNonnull(getInnerList(), p -> p.defaultPreview);
    }

    public int getMatchingIndex(AbstractCard card) {
        if (card != null) {
            for (int i = 0; i < count(); i++) {
                if (getCard(i).uuid.equals(card.uuid)) {
                    return i;
                }
            }
        }
        return -1;
    }
}
