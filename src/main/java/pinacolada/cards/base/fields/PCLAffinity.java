package pinacolada.cards.base.fields;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CountingPanelItem;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.TextureCache;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.dungeon.CombatManager;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.io.IOException;
import java.util.*;

@JsonAdapter(PCLAffinity.PCLAffinityAdapter.class)
public class PCLAffinity implements KeywordProvider, Comparable<PCLAffinity>, CountingPanelItem {
    private static final HashMap<AbstractCard.CardColor, Set<PCLAffinity>> REGISTERED_TYPES = new HashMap<>();
    private static final HashMap<AbstractCard.CardColor, TextureCache> REGISTERED_BORDERS = new HashMap<>();
    private static final HashMap<String, PCLAffinity> BY_SYMBOL = new HashMap<>();
    public static final int ID_UNKNOWN = -3;
    public static final int ID_GENERAL = -2;
    public static final int ID_STAR = -1;
    public static final int ID_RED = 0;
    public static final int ID_BLUE = 1;
    public static final int ID_GREEN = 2;
    public static final int ID_ORANGE = 3;
    public static final int ID_YELLOW = 4;
    public static final int ID_PURPLE = 5;
    public static final int ID_SILVER = 6;
    public static final String SYM_RED = "R";
    public static final String SYM_BLUE = "B";
    public static final String SYM_GREEN = "G";
    public static final String SYM_ORANGE = "O";
    public static final String SYM_YELLOW = "Y";
    public static final String SYM_PURPLE = "P";
    public static final String SYM_SILVER = "S";
    public static final String SYM_STAR = "A";
    public static final String SYM_GENERAL = "W";
    public static final String SYM_UNKNOWN = "U";
    private static PCLAffinity[] AFFINITIES = new PCLAffinity[]{};
    // Affinities with special purposes, these are deliberately not registered
    public static final PCLAffinity Unknown = new PCLAffinity(ID_UNKNOWN, SYM_UNKNOWN);
    public static final PCLAffinity General = new PCLAffinity(ID_GENERAL, SYM_GENERAL);
    public static final PCLAffinity Star = new PCLAffinity(ID_STAR, SYM_STAR);
    public static final PCLAffinity Red = registerAffinityAt(ID_RED, SYM_RED).setColor(new Color(0.8f, 0.5f, 0.5f, 1f));
    public static final PCLAffinity Blue = registerAffinityAt(ID_BLUE, SYM_BLUE).setColor(new Color(0.45f, 0.55f, 0.7f, 1f));
    public static final PCLAffinity Green = registerAffinityAt(ID_GREEN, SYM_GREEN).setColor(new Color(0.45f, 0.7f, 0.55f, 1f));
    public static final PCLAffinity Orange = registerAffinityAt(ID_ORANGE, SYM_ORANGE).setColor(new Color(0.7f, 0.6f, 0.5f, 1f));
    public static final PCLAffinity Yellow = registerAffinityAt(ID_YELLOW, SYM_YELLOW).setColor(new Color(0.8f, 0.8f, 0.3f, 1f));
    public static final PCLAffinity Purple = registerAffinityAt(ID_PURPLE, SYM_PURPLE).setColor(new Color(0.55f, 0.1f, 0.85f, 1));
    public static final PCLAffinity Silver = registerAffinityAt(ID_SILVER, SYM_SILVER).setColor(new Color(0.5f, 0.5f, 0.5f, 1f));

    public final int ID;
    public final String symbol;
    public final EUIKeywordTooltip tooltip;
    protected Color color;

    PCLAffinity(int ID, String powerSymbol) {
        this(ID, powerSymbol, EUIKeywordTooltip.findByID(getAffinitySymbol(powerSymbol)).forceIcon(true));
    }

    PCLAffinity(int ID, String powerSymbol, EUIKeywordTooltip tip) {
        this.ID = ID;
        this.symbol = powerSymbol;
        this.tooltip = tip;

        if (BY_SYMBOL.containsKey(powerSymbol)) {
            throw new RuntimeException("Duplicate power symbol: " + powerSymbol);
        }
        BY_SYMBOL.put(powerSymbol, this);
    }

    // TODO find way to make array iterator without making new copy and preserving privacy
    public static List<PCLAffinity> basic() {
        return Arrays.asList(AFFINITIES);
    }

    public static PCLAffinity get(int id) {
        switch (id) {
            case ID_UNKNOWN:
                return Unknown;
            case ID_GENERAL:
                return General;
            case ID_STAR:
                return Star;
        }
        return AFFINITIES[id];
    }

    public static PCLAffinity get(String symbol) {
        return BY_SYMBOL.get(symbol);
    }

    public static String getAffinitySymbol(String symbol) {
        return EUIUtils.format("A-{0}", symbol);
    }

    public static Collection<PCLAffinity> getAvailableAffinities() {
        if (GameUtilities.inGame()) {
            return CombatManager.showAffinities();
        }
        return getAvailableAffinities(GameUtilities.getActingColor());
    }

    public static Collection<PCLAffinity> getAvailableAffinities(AbstractCard.CardColor pc) {
        return getAvailableAffinities(pc, true);
    }

    public static Collection<PCLAffinity> getAvailableAffinities(AbstractCard.CardColor pc, boolean allowColorless) {
        if (GameUtilities.isColorlessCardColor(pc)) {
            return allowColorless ? basic() : Collections.emptySet();
        }
        else {
            return REGISTERED_TYPES.getOrDefault(pc, Collections.emptySet());
        }
    }

    public static int getCount() {
        return AFFINITIES.length;
    }

    public static PCLAffinity getRandomAvailableAffinity() {
        PCLAffinity affinity = GameUtilities.getRandomElement(new ArrayList<>(getAvailableAffinities()));
        return affinity != null ? affinity : General;
    }

    public static void loadIconsIntoKeywords() {
        Star.tooltip.setIcon(Star.getDefaultIcon());
        General.tooltip.setIcon(General.getDefaultIcon());
        Unknown.tooltip.setIcon(Unknown.getDefaultIcon());
        for (PCLAffinity affinity : AFFINITIES) {
            affinity.tooltip.setIconFunc(affinity::getTextureRegion);
        }
    }

    public static PCLAffinity registerAffinity(String symbol) {
        PCLAffinity affinity = new PCLAffinity(AFFINITIES.length, symbol);
        AFFINITIES = EUIUtils.arrayAppend(AFFINITIES, affinity);
        return affinity;
    }

    private static PCLAffinity registerAffinityAt(int index, String symbol) {
        PCLAffinity affinity = new PCLAffinity(index, symbol);
        if (index >= AFFINITIES.length) {
            AFFINITIES = Arrays.copyOf(AFFINITIES, index + 1);
        }
        AFFINITIES[index] = affinity;
        return affinity;
    }

    public static void registerAffinityBorder(AbstractCard.CardColor pc, TextureCache cache) {
        REGISTERED_BORDERS.putIfAbsent(pc, cache);
    }

    public static void registerAvailableAffinities(AbstractCard.CardColor pc, PCLAffinity... affinities) {
        REGISTERED_TYPES.putIfAbsent(pc, new TreeSet<PCLAffinity>(Arrays.asList(affinities)));
    }

    @Override
    public int compareTo(PCLAffinity o) {
        return this.ID - o.ID;
    }

    public String getAffinitySymbol() {
        return getAffinitySymbol(symbol);
    }

    public Color getAlternateColor(float lerp) {
        return Color.WHITE.cpy().lerp(getAlternateColor(), lerp);
    }

    public Color getAlternateColor() {
        return color != null ? color : Color.WHITE;
    }

    public Texture getBackground(int level) {
        return (level) > 1 ? PCLCoreImages.Core.borderFG.texture() : null;
    }

    public Texture getBorder(int level) {
        AbstractCard.CardColor color = GameUtilities.getActingColor();
        TextureCache cache = REGISTERED_BORDERS.get(color);
        if (cache != null) {
            return cache.texture();
        }
        return (level > 1 ? PCLCoreImages.Core.borderWeak : PCLCoreImages.Core.borderNormal).texture();
    }

    public Texture getDefaultIcon() {
        Texture tex = PGR.core.images.getAffinityTexture(this);
        return tex != null ? tex : PCLCoreImages.CardAffinity.unknown.texture();
    }

    public Texture getForeground(int level) {
        return /*this == Star ? null : */(level > 1 ? PCLCoreImages.Core.borderFG.texture() : null);
    }

    public String getFormattedAffinitySymbol() {
        return EUIUtils.format("[{0}]", getAffinitySymbol());
    }

    public String getFormattedPowerSymbol() {
        return EUIUtils.format("[{0}]", getPowerSymbol());
    }

    public String getFormattedSymbol(AbstractCard.CardColor pc) {
        return getTooltip().getTitleOrIcon();
    }

    public String getFormattedSymbolForced(AbstractCard.CardColor pc) {
        return getTooltip().getTitleOrIconForced();
    }

    public Texture getIcon() {
        AbstractCard.CardColor color = GameUtilities.getActingColor();
        PCLResources<?, ?, ?, ?> resources = PGR.getResources(color);
        Texture tex = resources.images.getAffinityTexture(this);
        return tex != null ? tex : getDefaultIcon();
    }

    public String getPowerSymbol() {
        return EUIUtils.format("P-{0}", symbol);
    }

    @Override
    public int getRank(AbstractCard c) {
        return GameUtilities.getPCLCardAffinityLevel(c, this, false);
    }

    public TextureRegion getTextureRegion() {
        return EUIRenderHelpers.getCroppedRegion(getIcon(), 10);
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return Collections.singletonList(getTooltip());
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        return tooltip;
    }

    public PCLCardDataAffinity make() {
        return make(1);
    }

    public PCLCardDataAffinity make(int level) {
        return new PCLCardDataAffinity(this, level);
    }

    public PCLCardDataAffinity make(int level, int upgrade) {
        return new PCLCardDataAffinity(this, level, upgrade);
    }

    public PCLCardDataAffinity make(int level, Integer[] upgrade) {
        return new PCLCardDataAffinity(this, level, upgrade);
    }

    public PCLCardDataAffinity make(Integer[] level, Integer[] upgrade) {
        return new PCLCardDataAffinity(this, level, upgrade);
    }

    public PCLAffinity setColor(Color color) {
        this.color = color;
        return this;
    }

    public static class PCLAffinityAdapter extends TypeAdapter<PCLAffinity> {
        @Override
        public PCLAffinity read(JsonReader in) throws IOException {
            String key = in.nextString();
            PCLAffinity data = get(key);
            if (data != null) {
                return data;
            }
            EUIUtils.logError(PCLPowerData.PCLPowerDataAdapter.class, "Failed to read affinity " + key);
            // Legacy migration
            // TODO remove this after a few patches
            switch (key) {
                case "Blue":
                    return Blue;
                case "Green":
                    return Green;
                case "Orange":
                    return Orange;
                case "Purple":
                    return Purple;
                case "Silver":
                    return Silver;
                case "Star":
                    return Star;
                case "Yellow":
                    return Yellow;
            }
            return Red;
        }

        @Override
        public void write(JsonWriter writer, PCLAffinity value) throws IOException {
            writer.value(value.symbol);
        }
    }
}