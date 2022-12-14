package pinacolada.cards.base;

import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;

public enum PCLAttackType
{
    Normal(false, false, false, false),
    Brutal(false, false, false, false),
    Magical(false, true, false, true),
    Piercing(true, true, false, false),
    Ranged(false, true, true, false);

    public final boolean bypassThorns;
    public final boolean bypassBlock;
    public final boolean bypassFlight;
    public final boolean useFocus;

    PCLAttackType(boolean bypassBlock, boolean bypassThorns, boolean bypassFlight, boolean useFocus)
    {
        this.bypassThorns = bypassThorns;
        this.bypassBlock = bypassBlock;
        this.bypassFlight = bypassFlight;
        this.useFocus = useFocus;
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    public final EUITooltip getTooltip()
    {
        switch (this)
        {
            case Brutal:
                return PGR.core.tooltips.brutal;
            case Magical:
                return PGR.core.tooltips.magicDamage;
            case Piercing:
                return PGR.core.tooltips.piercing;
            case Ranged:
                return PGR.core.tooltips.ranged;
        }
        return PGR.core.tooltips.damage;
    }
}
