package pinacolada.orbs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.PCLSFX;
import pinacolada.misc.PCLGenericData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.skills.delay.DelayTiming;
import pinacolada.utilities.GameUtilities;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static extendedui.EUIUtils.array;

@JsonAdapter(PCLOrbData.PCLOrbDataAdapter.class)
public class PCLOrbData extends PCLGenericData<AbstractOrb> implements KeywordProvider {
    private static final Map<String, PCLOrbData> STATIC_DATA = new HashMap<>();

    public static final PCLOrbData Dark = registerBase(com.megacrit.cardcrawl.orbs.Dark.class, com.megacrit.cardcrawl.orbs.Dark.ORB_ID, PGR.core.tooltips.dark).setTiming(DelayTiming.EndOfTurnFirst);
    public static final PCLOrbData Frost = registerBase(com.megacrit.cardcrawl.orbs.Frost.class, com.megacrit.cardcrawl.orbs.Frost.ORB_ID, PGR.core.tooltips.frost).setTiming(DelayTiming.EndOfTurnFirst);
    public static final PCLOrbData Lightning = registerBase(com.megacrit.cardcrawl.orbs.Lightning.class, com.megacrit.cardcrawl.orbs.Lightning.ORB_ID, PGR.core.tooltips.lightning).setTiming(DelayTiming.EndOfTurnFirst);
    public static final PCLOrbData Plasma = registerBase(com.megacrit.cardcrawl.orbs.Plasma.class, com.megacrit.cardcrawl.orbs.Plasma.ORB_ID, PGR.core.tooltips.plasma).setTiming(DelayTiming.StartOfTurnFirst);

    public DelayTiming timing = DelayTiming.EndOfTurnFirst;
    public Color flareColor1 = Color.WHITE;
    public Color flareColor2 = Color.WHITE;
    public EUIKeywordTooltip tooltip;
    public OrbStrings strings;
    public String sfx = PCLSFX.ORB_LIGHTNING_CHANNEL;
    public boolean applyFocusToEvoke = true;
    public boolean applyFocusToPassive = true;
    public Integer[] baseEvokeValue = array(1);
    public Integer[] baseEvokeValueUpgrade = array(0);
    public Integer[] basePassiveValue = array(1);
    public Integer[] basePassiveValueUpgrade = array(0);
    public int maxForms = 0;
    public int maxUpgradeLevel = 0;
    public float rotationSpeed = 45f;

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
        this.initializeImage();
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
        PCLCustomOrbSlot slot = PCLCustomOrbSlot.get(key);
        if (slot != null) {
            return slot.getBuilder(0);
        }
        return null;
    }

    public static void loadIconsIntoKeywords() {
        for (PCLOrbData data : STATIC_DATA.values()) {
            data.loadImageIntoTooltip();
        }
    }

    public static PCLOrbData registerBase(Class<? extends AbstractOrb> powerClass, String id, EUIKeywordTooltip tip) {
        return registerData(new PCLOrbData(powerClass, PGR.core, id, tip))
                .setImagePath(PGR.getOrbImage(PGR.core.createID(id)));
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

    public int getBaseEvoke(int form) {
        return baseEvokeValue[Math.min(baseEvokeValue.length - 1, form)];
    }

    public int getBaseEvokeUpgrade(int form) {
        return baseEvokeValueUpgrade[Math.min(baseEvokeValueUpgrade.length - 1, form)];
    }

    public int getBasePassive(int form) {
        return basePassiveValue[Math.min(basePassiveValue.length - 1, form)];
    }

    public int getBasePassiveUpgrade(int form) {
        return basePassiveValueUpgrade[Math.min(basePassiveValueUpgrade.length - 1, form)];
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
        this.imagePath = PGR.getOrbImage(ID);
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

    public PCLOrbData setBaseEvoke(int heal) {
        this.baseEvokeValue[0] = heal;
        return this;
    }

    public PCLOrbData setBaseEvoke(int heal, int healUpgrade) {
        this.baseEvokeValue[0] = heal;
        this.baseEvokeValueUpgrade[0] = healUpgrade;
        return this;
    }

    public PCLOrbData setBaseEvoke(int thp, Integer[] thpUpgrade) {
        return setBaseEvoke(array(thp), thpUpgrade);
    }

    public PCLOrbData setBaseEvoke(Integer[] heal, Integer[] healUpgrade) {
        this.baseEvokeValue = heal;
        this.baseEvokeValueUpgrade = healUpgrade;
        return this;
    }

    public PCLOrbData setBaseEvokeForForm(int form, int targetSize, int val, int upgrade) {
        if (form >= this.baseEvokeValue.length) {
            this.baseEvokeValue = expandArray(this.baseEvokeValue, targetSize);
        }
        if (form >= this.baseEvokeValueUpgrade.length) {
            this.baseEvokeValueUpgrade = expandArray(this.baseEvokeValueUpgrade, targetSize);
        }
        this.baseEvokeValue[form] = val;
        this.baseEvokeValueUpgrade[form] = upgrade;
        this.maxForms = EUIUtils.max(this.maxForms, this.baseEvokeValue.length, this.baseEvokeValueUpgrade.length);
        return this;
    }

    public PCLOrbData setBasePassive(int heal) {
        this.basePassiveValue[0] = heal;
        return this;
    }

    public PCLOrbData setBasePassive(int heal, int healUpgrade) {
        this.basePassiveValue[0] = heal;
        this.basePassiveValueUpgrade[0] = healUpgrade;
        return this;
    }

    public PCLOrbData setBasePassive(int thp, Integer[] thpUpgrade) {
        return setBasePassive(array(thp), thpUpgrade);
    }

    public PCLOrbData setBasePassive(Integer[] heal, Integer[] healUpgrade) {
        this.basePassiveValue = heal;
        this.basePassiveValueUpgrade = healUpgrade;
        return this;
    }

    public PCLOrbData setBasePassiveForForm(int form, int targetSize, int val, int upgrade) {
        if (form >= this.basePassiveValue.length) {
            this.basePassiveValue = expandArray(this.basePassiveValue, targetSize);
        }
        if (form >= this.basePassiveValueUpgrade.length) {
            this.basePassiveValueUpgrade = expandArray(this.basePassiveValueUpgrade, targetSize);
        }
        this.basePassiveValue[form] = val;
        this.basePassiveValueUpgrade[form] = upgrade;
        this.maxForms = EUIUtils.max(this.maxForms, this.basePassiveValue.length, this.basePassiveValueUpgrade.length);
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

    public PCLOrbData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLOrbData setMaxForms(int maxForms) {
        this.maxForms = maxForms;

        return this;
    }

    public PCLOrbData setMaxUpgrades(int maxUpgradeLevel) {
        this.maxUpgradeLevel = MathUtils.clamp(maxUpgradeLevel, -1, Integer.MAX_VALUE);

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
