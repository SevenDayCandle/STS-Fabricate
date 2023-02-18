package pinacolada.cards.base.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.resources.PGR;
import pinacolada.utilities.ListSelection;

public enum PCLCardSelection
{
    Manual,
    Top,
    Bottom,
    Random;

    public ListSelection<AbstractCard> toSelection()
    {
        switch (this)
        {
            case Top:
                return ListSelection.last(0);
            case Bottom:
                return ListSelection.first(0);
            case Random:
                return ListSelection.random(null);
            default:
                return null;
        }
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    public final String getTitle()
    {
        switch (this)
        {
            case Manual:
                return PGR.core.strings.cpile_manual;
            case Top:
                return PGR.core.strings.cpile_top;
            case Bottom:
                return PGR.core.strings.cpile_bottom;
            case Random:
                return PGR.core.strings.cpile_random;
        }
        return "";
    }
}
