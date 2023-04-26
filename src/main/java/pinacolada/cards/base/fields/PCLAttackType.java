package pinacolada.cards.base.fields;

import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;

import java.util.Collections;
import java.util.List;

public enum PCLAttackType implements TooltipProvider {
    Normal(false, false, false, false),
    Brutal(false, false, false, false),
    Immaterial(false, true, false, true),
    Piercing(true, true, false, false),
    Ranged(false, true, true, false);

    public final boolean bypassThorns;
    public final boolean bypassBlock;
    public final boolean bypassFlight;
    public final boolean useFocus;

    PCLAttackType(boolean bypassBlock, boolean bypassThorns, boolean bypassFlight, boolean useFocus) {
        this.bypassThorns = bypassThorns;
        this.bypassBlock = bypassBlock;
        this.bypassFlight = bypassFlight;
        this.useFocus = useFocus;
    }

    // These strings cannot be put in as an enum variable because cards are initialized before these strings are
    public final EUITooltip getTooltip() {
        switch (this) {
            case Brutal:
                return PGR.core.tooltips.brutal;
            case Immaterial:
                return PGR.core.tooltips.immaterialDamage;
            case Piercing:
                return PGR.core.tooltips.piercing;
            case Ranged:
                return PGR.core.tooltips.ranged;
        }
        return PGR.core.tooltips.normalDamage;
    }

    @Override
    public List<EUITooltip> getTips() {
        return Collections.singletonList(getTooltip());
    }
}
