package pinacolada.blights;

import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.BlightStrings;
import extendedui.interfaces.delegates.FuncT1;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleRelic;
import pinacolada.misc.PCLGenericData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static extendedui.EUIUtils.array;

public class PCLBlightData extends PCLGenericData<PCLBlight> {
    private static final Map<String, PCLBlightData> STATIC_DATA = new HashMap<>();

    public BlightStrings strings;
    public String imagePath;
    public Integer[] counter = array(0);
    public Integer[] counterUpgrade = array(0);
    public boolean unique = false;
    public int maxForms = 1;
    public int maxUpgradeLevel = 0;
    public int branchFactor = 0;

    public PCLBlightData(Class<? extends PCLBlight> invokeClass, PCLResources<?, ?, ?, ?> resources) {
        this(invokeClass, resources, resources.createID(invokeClass.getSimpleName()));
    }

    public PCLBlightData(Class<? extends PCLBlight> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID) {
        this(invokeClass, resources, cardID, PGR.getBlightStrings(cardID));
    }

    public PCLBlightData(Class<? extends PCLBlight> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, BlightStrings strings) {
        super(cardID, invokeClass, resources);
        this.strings = strings != null ? strings : new BlightStrings();
        this.initializeImage();
    }

    public static List<PCLBlightData> getAllData() {
        return getAllData(false, true, (FuncT1<Boolean, PCLBlightData>) null);
    }

    public static List<PCLBlightData> getAllData(boolean showHidden, boolean sort, FuncT1<Boolean, PCLBlightData> filterFunc) {
        Stream<PCLBlightData> stream = STATIC_DATA
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

    public static List<PCLBlightData> getAllData(boolean showHidden, boolean sort, AbstractCard.CardColor filterColor) {
        return getAllData(false, true, a -> a.resources.cardColor == filterColor || a.resources == PGR.core);
    }

    public static PCLBlightData getStaticData(String cardID) {
        return STATIC_DATA.get(cardID);
    }

    protected static <T extends PCLBlightData> T registerData(T cardData) {
        STATIC_DATA.put(cardData.ID, cardData);
        return cardData;
    }

    public int getCounter(int form) {
        return counter[Math.min(counter.length - 1, form)];
    }

    public int getCounterUpgrade(int form) {
        return counterUpgrade[Math.min(counterUpgrade.length - 1, form)];
    }

    public void initializeImage() {
        this.imagePath = PGR.getBlightImage(ID);
    }

    public PCLBlightData setBranchFactor(int factor) {
        this.branchFactor = factor;

        return this;
    }

    public PCLBlightData setCounter(int heal) {
        this.counter[0] = heal;
        return this;
    }

    public PCLBlightData setCounter(int heal, int healUpgrade) {
        this.counter[0] = heal;
        this.counterUpgrade[0] = healUpgrade;
        return this;
    }

    public PCLBlightData setCounter(int thp, Integer[] thpUpgrade) {
        return setCounter(array(thp), thpUpgrade);
    }

    public PCLBlightData setCounter(Integer[] heal, Integer[] healUpgrade) {
        this.counter = heal;
        this.counterUpgrade = healUpgrade;
        return this;
    }

    public PCLBlightData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLBlightData setMaxForms(int maxForms) {
        this.maxForms = maxForms;

        return this;
    }

    public PCLBlightData setMaxUpgrades(int maxUpgradeLevel) {
        this.maxUpgradeLevel = MathUtils.clamp(maxUpgradeLevel, -1, Integer.MAX_VALUE);

        return this;
    }

    public PCLBlightData setMaxUpgrades(int maxUpgradeLevel, boolean levelOnStack) {
        setMaxUpgrades(maxUpgradeLevel);
        return setUnique(levelOnStack);
    }

    public PCLBlightData setUnique(boolean unique) {
        this.unique = unique;

        return this;
    }
}
