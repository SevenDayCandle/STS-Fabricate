package pinacolada.misc;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.interfaces.markers.CountingPanelCardFilter;
import extendedui.ui.cardFilter.CountingPanelCounter;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLAffinityPanelFilter implements CountingPanelCardFilter {
    @Override
    public ArrayList<? extends CountingPanelCounter<?>> generateCounters(ArrayList<AbstractCard> cards, Hitbox hb) {
        return GameUtilities.affinityStats(cards).generateCounters(hb, panel -> cards.sort(panel.type));
    }
}
