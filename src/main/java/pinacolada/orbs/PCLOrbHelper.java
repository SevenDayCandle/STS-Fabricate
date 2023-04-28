package pinacolada.orbs;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.WeightedList;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@JsonAdapter(PCLOrbHelper.PCLOrbHelperAdapter.class)
public class PCLOrbHelper implements TooltipProvider {
    private static final Map<String, PCLOrbHelper> ALL = new HashMap<>();
    private static final WeightedList<PCLOrbHelper> WEIGHTED = new WeightedList<>();

    public static final int COMMON_THRESHOLD = 11;
    public static final PCLOrbHelper Dark = new PCLOrbHelper(com.megacrit.cardcrawl.orbs.Dark.ORB_ID, PGR.core.tooltips.dark, PCLAffinity.Purple, com.megacrit.cardcrawl.orbs.Dark::new, COMMON_THRESHOLD);
    public static final PCLOrbHelper Frost = new PCLOrbHelper(com.megacrit.cardcrawl.orbs.Frost.ORB_ID, PGR.core.tooltips.frost, PCLAffinity.Blue, com.megacrit.cardcrawl.orbs.Frost::new, COMMON_THRESHOLD);
    public static final PCLOrbHelper Lightning = new PCLOrbHelper(com.megacrit.cardcrawl.orbs.Lightning.ORB_ID, PGR.core.tooltips.lightning, PCLAffinity.Yellow, com.megacrit.cardcrawl.orbs.Lightning::new, COMMON_THRESHOLD);
    public static final PCLOrbHelper Plasma = new PCLOrbHelper(com.megacrit.cardcrawl.orbs.Plasma.ORB_ID, PGR.core.tooltips.plasma, PCLAffinity.Yellow, com.megacrit.cardcrawl.orbs.Plasma::new, 2);
    public final EUITooltip tooltip;
    public final PCLAffinity affinity;
    public final String ID;
    public final int weight;
    public final AbstractCard.CardColor[] allowedColors;
    protected final FuncT0<AbstractOrb> constructor;

    public PCLOrbHelper(String powerID, EUITooltip tooltip, PCLAffinity affinity, FuncT0<AbstractOrb> constructor, int weight, AbstractCard.CardColor... allowedColors) {
        this.ID = powerID;
        this.tooltip = tooltip;
        this.affinity = affinity;
        this.constructor = constructor;
        this.weight = weight;
        this.allowedColors = allowedColors;

        ALL.putIfAbsent(powerID, this);
        if (weight > 0) {
            WEIGHTED.add(this, weight);
        }
    }

    public static PCLOrbHelper get(String orbID) {
        return ALL.get(orbID);
    }

    public static PCLOrbHelper randomCommonHelper() {
        return GameUtilities.getRandomElement(EUIUtils.filter(WEIGHTED.getInnerList(), PCLOrbHelper::isCommon));
    }

    public final boolean isCommon() {
        return weight >= COMMON_THRESHOLD;
    }

    public static PCLOrbHelper randomHelper() {
        return randomHelper(true);
    }

    public static PCLOrbHelper randomHelper(boolean weighted) {
        return weighted ? WEIGHTED.retrieve(GameUtilities.getRNG(), false) : GameUtilities.getRandomElement(WEIGHTED.getInnerList());
    }

    public static AbstractOrb randomOrb() {
        return randomOrb(true);
    }

    public static AbstractOrb randomOrb(boolean weighted) {
        return randomHelper(weighted).create();
    }

    public AbstractOrb create() {
        if (constructor != null) {
            return constructor.invoke();
        }
        else {
            throw new RuntimeException("Do not create a PowerHelper with a null constructor.");
        }
    }

    public static Collection<PCLOrbHelper> values() {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.tooltip.title, b.tooltip.title)).collect(Collectors.toList());
    }

    public static Collection<PCLOrbHelper> visibleValues() {
        return WEIGHTED.getInnerList().stream().sorted((a, b) -> StringUtils.compare(a.tooltip.title, b.tooltip.title)).collect(Collectors.toList());
    }

    @Override
    public List<EUITooltip> getTips() {
        return Collections.singletonList(tooltip);
    }

    public static class PCLOrbHelperAdapter extends TypeAdapter<PCLOrbHelper> {
        @Override
        public void write(JsonWriter writer, PCLOrbHelper value) throws IOException {
            writer.value(value.ID);
        }

        @Override
        public PCLOrbHelper read(JsonReader in) throws IOException {
            return get(in.nextString());
        }
    }
}
