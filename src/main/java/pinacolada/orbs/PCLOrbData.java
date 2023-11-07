package pinacolada.orbs;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.PCLSFX;
import pinacolada.misc.PCLGenericData;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.utilities.GameUtilities;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static extendedui.EUIUtils.array;

public class PCLOrbData extends PCLGenericData<AbstractOrb> implements KeywordProvider {
    private static final Map<String, PCLOrbData> STATIC_DATA = new HashMap<>();

    public DelayTiming timing;
    public Color flareColor1 = Color.WHITE;
    public Color flareColor2 = Color.WHITE;
    public EUIKeywordTooltip tooltip;
    public OrbStrings strings;
    public String sfx = PCLSFX.ORB_LIGHTNING_CHANNEL;
    public boolean applyFocusToEvoke = true;
    public boolean applyFocusToPassive = true;
    public int baseEvokeValue = 1;
    public int basePassiveValue = 1;
    public float rotationSpeed;

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources) {
        this(invokeClass, resources, resources.createID(invokeClass.getSimpleName()));
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, EUIKeywordTooltip tip) {
        this(invokeClass, resources);
        this.tooltip = tip;
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID) {
        this(invokeClass, resources, cardID, PGR.getOrbStrings(cardID));
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, EUIKeywordTooltip tip) {
        this(invokeClass, resources, cardID);
        this.tooltip = tip;
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, OrbStrings strings) {
        super(cardID, invokeClass, resources);
        this.strings = strings != null ? strings : new OrbStrings();
        //this.initializeImage();
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, OrbStrings strings, EUIKeywordTooltip tip) {
        this(invokeClass, resources, cardID, strings);
        this.tooltip = tip;
    }

    public static Collection<PCLOrbData> getAllData() {
        return getAllData(true, null);
    }

    public static List<PCLOrbData> getAllData(boolean sort, FuncT1<Boolean, PCLOrbData> filterFunc) {
        Stream<PCLOrbData> stream = STATIC_DATA
                .values()
                .stream();
        if (filterFunc != null) {
            stream = stream.filter(filterFunc::invoke);
        }
        if (sort) {
            stream = stream.sorted((a, b) -> StringUtils.compare(a.strings.NAME, b.strings.NAME));
        }
        return stream
                .distinct()
                .collect(Collectors.toList());
    }

    public static PCLOrbData getRandom() {
        return getRandom(null);
    }

    public static PCLOrbData getRandom(FuncT1<Boolean, PCLOrbData> filterFunc) {
        List<PCLOrbData> powers = getAllData(false, filterFunc);
        return GameUtilities.getRandomElement(powers);
    }

    public static PCLOrbData getStaticData(String cardID) {
        return STATIC_DATA.get(cardID);
    }

    public static PCLOrbData getStaticDataOrCustom(String key) {
        PCLOrbData data = getStaticData(key);
        if (data != null) {
            return data;
        }

        return null;
    }

    public static void loadIconsIntoKeywords() {
        for (PCLOrbData data : STATIC_DATA.values()) {
            data.loadImageIntoTooltip();
        }
    }

    // Should only be used by equivalents
    private static <T extends PCLOrbData> T registerData(String id, T cardData) {
        STATIC_DATA.put(id, cardData);
        return cardData;
    }

    protected static <T extends PCLOrbData> T registerData(T cardData) {
        STATIC_DATA.put(cardData.ID, cardData);
        return cardData;
    }

    protected static <T extends PCLOrbData> T registerPCLData(T cardData) {
        registerData(cardData);
        cardData.initializeImage();
        return cardData;
    }

    public String getName() {
        return strings.NAME;
    }

    public String getText() {
        return tooltip != null ? tooltip.description : strings.DESCRIPTION.length > 0 ? strings.DESCRIPTION[0] : EUIUtils.EMPTY_STRING;
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return Collections.singletonList(tooltip);
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        return tooltip;
    }

    public void initializeImage() {
        this.imagePath = PGR.getPowerImage(ID); // TODO fix
    }

    public void loadImageIntoTooltip() {
        if (tooltip != null && tooltip.icon == null) {
            tooltip.setIconFromPath(imagePath);
        }
    }

    public PCLOrbData setApplyFocusToEvoke(boolean val) {
        this.applyFocusToEvoke = val;
        return this;
    }

    public PCLOrbData setApplyFocusToPassive(boolean val) {
        this.applyFocusToPassive = val;
        return this;
    }

    public PCLOrbData setBaseEvokeValue(int val) {
        this.baseEvokeValue = val;
        return this;
    }

    public PCLOrbData setBasePassiveValue(int val) {
        this.basePassiveValue = val;
        return this;
    }

    public PCLOrbData setFlareColor1(Color val) {
        this.flareColor1 = val != null ? val : Color.WHITE;
        return this;
    }

    public PCLOrbData setFlareColor2(Color val) {
        this.flareColor2 = val != null ? val : Color.WHITE;
        return this;
    }

    public PCLOrbData setRotationSpeed(float val) {
        this.rotationSpeed = val;
        return this;
    }

    public PCLOrbData setSfx(String val) {
        this.sfx = val;
        return this;
    }

    public PCLOrbData setTiming(DelayTiming val) {
        this.timing = val;
        return this;
    }

    public PCLOrbData setTooltip(EUIKeywordTooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public static class PCLOrbDataAdapter extends TypeAdapter<PCLOrbData> {
        @Override
        public PCLOrbData read(JsonReader in) throws IOException {
            String key = in.nextString();
            PCLOrbData data = getStaticDataOrCustom(key);
            if (data != null) {
                return data;
            }
            EUIUtils.logError(PCLOrbData.PCLOrbDataAdapter.class, "Failed to read orb " + key);
            return null; // TODO fix
        }

        @Override
        public void write(JsonWriter writer, PCLOrbData value) throws IOException {
            writer.value(value.ID);
        }
    }
}
