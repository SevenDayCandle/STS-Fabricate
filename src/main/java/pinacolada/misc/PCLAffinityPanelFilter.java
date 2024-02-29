package pinacolada.misc;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CountingPanelFilter;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.ui.cardFilter.CountingPanelCounter;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLAffinityPanelFilter implements CountingPanelFilter<AbstractCard> {

    @Override
    public ArrayList<? extends CountingPanelCounter<?, AbstractCard>> generateCounters(ArrayList<? extends AbstractCard> cards, Hitbox hb, ActionT1<CountingPanelCounter<? extends CountingPanelItem<AbstractCard>, AbstractCard>> onClick) {
        return GameUtilities.affinityStats(cards).generateCounters(hb, onClick);
    }

    @Override
    public String getTitle() {
        return PGR.core.tooltips.affinityGeneral.title;
    }
}
