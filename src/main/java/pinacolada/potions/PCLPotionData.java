package pinacolada.potions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import extendedui.interfaces.delegates.FuncT1;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleRelic;
import pinacolada.misc.PCLGenericData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static extendedui.EUIUtils.array;

public class PCLPotionData extends PCLGenericData<PCLPotion> {
    private static final Map<String, PCLPotionData> STATIC_DATA = new HashMap<>();
    private static final ArrayList<PCLPotionData> TEMPLATES = new ArrayList<>();

    public PotionStrings strings;
    public String imagePath;
    public AbstractCard.CardColor cardColor = AbstractCard.CardColor.COLORLESS;
    public AbstractPotion.PotionEffect effect = AbstractPotion.PotionEffect.NONE;
    public AbstractPotion.PotionRarity rarity = AbstractPotion.PotionRarity.PLACEHOLDER;
    public AbstractPotion.PotionSize size = AbstractPotion.PotionSize.S;
    public Color liquidColor = Color.WHITE;
    public Color hybridColor = Color.WHITE;
    public Color spotsColor = Color.WHITE;
    public Integer[] counter = array(0);
    public Integer[] counterUpgrade = array(0);
    public int maxForms = 1;
    public int maxUpgradeLevel = 0;
    public int branchFactor = 0;

    public PCLPotionData(Class<? extends PCLPotion> invokeClass, PCLResources<?, ?, ?, ?> resources) {
        this(invokeClass, resources, resources.createID(invokeClass.getSimpleName()));
    }

    public PCLPotionData(Class<? extends PCLPotion> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID) {
        this(invokeClass, resources, cardID, PGR.getPotionStrings(cardID));
    }

    public PCLPotionData(Class<? extends PCLPotion> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, PotionStrings strings) {
        super(cardID, invokeClass, resources);
        this.cardColor = resources.cardColor;
        this.strings = strings != null ? strings : new PotionStrings();
        this.initializeImage();
    }

    public static Collection<PCLPotionData> getAllData() {
        return getAllData(false, true, (FuncT1<Boolean, PCLPotionData>) null);
    }

    public static Collection<PCLPotionData> getAllData(boolean showHidden, boolean sort, FuncT1<Boolean, PCLPotionData> filterFunc) {
        Stream<PCLPotionData> stream = STATIC_DATA
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

    public static Collection<PCLPotionData> getAllData(boolean showHidden, boolean sort, AbstractCard.CardColor filterColor) {
        return getAllData(false, true, a -> a.cardColor == filterColor || a.resources.cardColor == filterColor || a.resources == PGR.core);
    }

    public int getCounter(int form) {
        return counter[Math.min(counter.length - 1, form)];
    }

    public int getCounterUpgrade(int form) {
        return counterUpgrade[Math.min(counterUpgrade.length - 1, form)];
    }

    public static PCLPotionData getStaticData(String cardID) {
        return STATIC_DATA.get(cardID);
    }

    public static Collection<PCLPotionData> getTemplates() {
        return TEMPLATES.stream().sorted((a, b) -> StringUtils.compare(a.ID, b.ID)).collect(Collectors.toList());
    }

    public void initializeImage() {
        this.imagePath = PGR.getRelicImage(ID);
    }

    protected static <T extends PCLPotionData> T registerData(T cardData) {
        STATIC_DATA.put(cardData.ID, cardData);
        return cardData;
    }

    protected static <T extends PCLPotionData> T registerTemplate(T cardData) {
        STATIC_DATA.put(cardData.ID, cardData);
        TEMPLATES.add(cardData);
        return cardData;
    }

    public PCLPotionData setBottleColor(Color liquidColor, Color hybridColor, Color spotsColor) {
        this.hybridColor = hybridColor != null ? hybridColor : Color.WHITE;
        this.liquidColor = liquidColor != null ? liquidColor : Color.WHITE;
        this.spotsColor = spotsColor != null ? spotsColor : Color.WHITE;
        return this;
    }

    public PCLPotionData setBranchFactor(int factor) {
        this.branchFactor = factor;

        return this;
    }

    public PCLPotionData setColor(AbstractCard.CardColor color) {
        this.cardColor = color;
        return this;
    }

    public PCLPotionData setCounter(int heal) {
        this.counter[0] = heal;
        return this;
    }

    public PCLPotionData setCounter(int heal, int healUpgrade) {
        this.counter[0] = heal;
        this.counterUpgrade[0] = healUpgrade;
        return this;
    }

    public PCLPotionData setCounter(int thp, Integer[] thpUpgrade) {
        return setCounter(array(thp), thpUpgrade);
    }

    public PCLPotionData setCounter(Integer[] heal, Integer[] healUpgrade) {
        this.counter = heal;
        this.counterUpgrade = healUpgrade;
        return this;
    }

    public PCLPotionData setHybridColor(Color hybridColor) {
        this.hybridColor = hybridColor != null ? hybridColor : Color.WHITE;
        return this;
    }

    public PCLPotionData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLPotionData setLiquidColor(Color liquidColor) {
        this.liquidColor = liquidColor != null ? liquidColor : Color.WHITE;
        return this;
    }

    public PCLPotionData setMaxForms(int maxForms) {
        this.maxForms = maxForms;

        return this;
    }

    public PCLPotionData setMaxUpgrades(int maxUpgradeLevel) {
        this.maxUpgradeLevel = MathUtils.clamp(maxUpgradeLevel, -1, Integer.MAX_VALUE);

        return this;
    }

    public PCLPotionData setEffect(AbstractPotion.PotionEffect effect) {
        this.effect = effect;
        return this;
    }


    public PCLPotionData setSize(AbstractPotion.PotionSize size) {
        this.size = size;
        return this;
    }

    public PCLPotionData setRarity(AbstractPotion.PotionRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public PCLPotionData setProps(AbstractPotion.PotionRarity tier, AbstractPotion.PotionSize sfx) {
        this.rarity = tier;
        this.size = sfx;
        return this;
    }

    public PCLPotionData setSpotsColor(Color spotsColor) {
        this.spotsColor = spotsColor != null ? spotsColor : Color.WHITE;
        return this;
    }
}
