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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static extendedui.EUIUtils.array;

public class PCLRelicData extends PCLGenericData<PCLRelic> {
    private static final Map<String, PCLRelicData> STATIC_DATA = new HashMap<>();

    public RelicStrings strings;
    public String imagePath;
    public AbstractCard.CardColor cardColor = AbstractCard.CardColor.COLORLESS;
    public AbstractRelic.RelicTier tier = AbstractRelic.RelicTier.DEPRECATED;
    public AbstractRelic.LandingSound sfx = AbstractRelic.LandingSound.CLINK;
    public Integer[] counter = array(0);
    public Integer[] counterUpgrade = array(0);
    public int maxForms = 1;
    public int maxUpgradeLevel = 0;
    public int branchFactor = 0;

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
        this.imagePath = PGR.getRelicImage(ID);
    }

    public static Collection<PCLRelicData> getAllData() {
        return getAllData(false, true, (FuncT1<Boolean, PCLRelicData>) null);
    }

    public static Collection<PCLRelicData> getAllData(boolean showHidden, boolean sort, FuncT1<Boolean, PCLRelicData> filterFunc) {
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

    public static Collection<PCLRelicData> getAllData(boolean showHidden, boolean sort, AbstractCard.CardColor filterColor) {
        return getAllData(false, true, a -> a.cardColor == filterColor || a.resources.cardColor == filterColor || a.resources == PGR.core);
    }

    public int getCounter(int form) {
        return counter[Math.min(counter.length - 1, form)];
    }

    public int getCounterUpgrade(int form) {
        return counterUpgrade[Math.min(counterUpgrade.length - 1, form)];
    }

    protected static <T extends PCLRelicData> T registerData(T cardData) {
        STATIC_DATA.put(cardData.ID, cardData);
        return cardData;
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

    public PCLRelicData setMaxForms(int maxForms) {
        this.maxForms = maxForms;

        return this;
    }

    public PCLRelicData setMaxUpgrades(int maxUpgradeLevel) {
        this.maxUpgradeLevel = MathUtils.clamp(maxUpgradeLevel, -1, Integer.MAX_VALUE);

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

    public PCLRelicData setProps(AbstractRelic.RelicTier tier, AbstractRelic.LandingSound sfx) {
        this.tier = tier;
        this.sfx = sfx;
        return this;
    }
}
