package pinacolada.relics;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.interfaces.delegates.FuncT1;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleRelic;
import pinacolada.misc.PCLGenericData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static extendedui.EUIUtils.array;

public class PCLRelicData extends PCLGenericData<PCLRelic> {
    private static final Map<String, PCLRelicData> STATIC_DATA = new HashMap<>();
    private static final ArrayList<PCLRelicData> TEMPLATES = new ArrayList<>();

    public AbstractCard.CardColor cardColor = AbstractCard.CardColor.COLORLESS;
    public AbstractRelic.LandingSound sfx = AbstractRelic.LandingSound.CLINK;
    public AbstractRelic.RelicTier tier = AbstractRelic.RelicTier.DEPRECATED;
    public Integer[] counter = array(0);
    public Integer[] counterUpgrade = array(0);
    public PCLLoadout loadout;
    public RelicStrings strings;
    public String[] replacementIDs;
    public boolean unique = false;
    public int maxForms = 1;
    public int maxUpgradeLevel = 0;
    public int branchFactor = 0;
    public Integer loadoutValue;

    public PCLRelicData(Class<? extends PCLRelic> invokeClass, PCLResources<?, ?, ?, ?> resources) {
        this(invokeClass, resources, resources.createID(invokeClass.getSimpleName()));
    }

    public PCLRelicData(Class<? extends PCLRelic> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID) {
        this(invokeClass, resources, cardID, PGR.getRelicStrings(cardID));
    }

    public PCLRelicData(Class<? extends PCLRelic> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, RelicStrings strings) {
        super(cardID, invokeClass, resources);
        this.cardColor = resources.cardColor;
        this.strings = strings != null ? strings : new RelicStrings();
        this.initializeImage();
    }

    public static List<PCLRelicData> getAllData() {
        return getAllData(false, true, (FuncT1<Boolean, PCLRelicData>) null);
    }

    public static List<PCLRelicData> getAllData(boolean showHidden, boolean sort, FuncT1<Boolean, PCLRelicData> filterFunc) {
        Stream<PCLRelicData> stream = STATIC_DATA
                .values()
                .stream();
        if (!showHidden) {
            stream = stream.filter(a -> a.invokeClass.isAnnotationPresent(VisibleRelic.class));
        }
        if (filterFunc != null) {
            stream = stream.filter(filterFunc::invoke);
        }
        if (sort) {
            stream = stream.sorted((a, b) -> StringUtils.compare(a.strings.NAME, b.strings.NAME));
        }
        return stream.collect(Collectors.toList());
    }

    public static List<PCLRelicData> getAllData(boolean showHidden, boolean sort, AbstractCard.CardColor filterColor) {
        return getAllData(false, true, a -> a.cardColor == filterColor || a.resources.cardColor == filterColor || a.resources == PGR.core);
    }

    public static PCLRelicData getStaticData(String cardID) {
        return STATIC_DATA.get(cardID);
    }

    public static List<PCLRelicData> getTemplates() {
        return TEMPLATES.stream().sorted((a, b) -> StringUtils.compare(a.ID, b.ID)).collect(Collectors.toList());
    }

    public static int getValueForRarity(AbstractRelic.RelicTier rarity) {
        switch (rarity) {
            case STARTER:
                return 6;
            case COMMON:
            case SHOP:
                return 14;
            case UNCOMMON:
                return 21;
            case RARE:
                return 28;
        }
        return 35;
    }

    protected static <T extends PCLRelicData> T registerData(T cardData) {
        STATIC_DATA.put(cardData.ID, cardData);
        return cardData;
    }

    protected static <T extends PCLRelicData> T registerTemplate(T cardData) {
        STATIC_DATA.put(cardData.ID, cardData);
        TEMPLATES.add(cardData);
        return cardData;
    }

    public int getCounter(int form) {
        return counter[Math.min(counter.length - 1, form)];
    }

    public int getCounterUpgrade(int form) {
        return counterUpgrade[Math.min(counterUpgrade.length - 1, form)];
    }

    public int getLoadoutValue() {
        if (loadoutValue != null) {
            return loadoutValue;
        }
        return getValueForRarity(tier);
    }

    public void initializeImage() {
        this.imagePath = PGR.getRelicImage(ID);
    }

    public PCLRelicData setBranchFactor(int factor) {
        this.branchFactor = factor;

        return this;
    }

    public PCLRelicData setColor(AbstractCard.CardColor color) {
        this.cardColor = color;
        return this;
    }

    public PCLRelicData setCounter(int heal) {
        this.counter[0] = heal;
        return this;
    }

    public PCLRelicData setCounter(int heal, int healUpgrade) {
        this.counter[0] = heal;
        this.counterUpgrade[0] = healUpgrade;
        return this;
    }

    public PCLRelicData setCounter(int thp, Integer[] thpUpgrade) {
        return setCounter(array(thp), thpUpgrade);
    }

    public PCLRelicData setCounter(Integer[] heal, Integer[] healUpgrade) {
        this.counter = heal;
        this.counterUpgrade = healUpgrade;
        return this;
    }

    public PCLRelicData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLRelicData setLoadout(PCLLoadout loadout) {
        this.loadout = loadout;

        if (this.loadout != null) {
            this.loadout.relics.add(this);
        }

        return this;
    }

    public PCLRelicData setLoadoutValue(int value) {
        this.loadoutValue = value;
        return this;
    }

    public PCLRelicData setMaxForms(int maxForms) {
        this.maxForms = maxForms;

        return this;
    }

    public PCLRelicData setMaxUpgrades(int maxUpgradeLevel) {
        this.maxUpgradeLevel = MathUtils.clamp(maxUpgradeLevel, -1, Integer.MAX_VALUE);

        return this;
    }

    public PCLRelicData setMaxUpgrades(int maxUpgradeLevel, boolean levelOnStack) {
        setMaxUpgrades(maxUpgradeLevel);
        return setUnique(levelOnStack);
    }

    public PCLRelicData setProps(AbstractRelic.RelicTier tier, AbstractRelic.LandingSound sfx) {
        this.tier = tier;
        this.sfx = sfx;
        return this;
    }

    public PCLRelicData setReplacementIDs(String... ids) {
        this.replacementIDs = ids;
        return this;
    }

    public PCLRelicData setSfx(AbstractRelic.LandingSound sfx) {
        this.sfx = sfx;
        return this;
    }

    public PCLRelicData setTier(AbstractRelic.RelicTier tier) {
        this.tier = tier;
        return this;
    }

    public PCLRelicData setUnique(boolean unique) {
        this.unique = unique;

        return this;
    }
}
