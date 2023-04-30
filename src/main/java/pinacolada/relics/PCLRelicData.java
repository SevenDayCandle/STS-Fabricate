package pinacolada.relics;

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

public class PCLRelicData extends PCLGenericData<PCLRelic> {
    private static final Map<String, PCLRelicData> STATIC_DATA = new HashMap<>();

    public AbstractCard.CardColor cardColor = AbstractCard.CardColor.COLORLESS;
    public RelicStrings strings;
    public String imagePath;
    public AbstractRelic.RelicTier tier = AbstractRelic.RelicTier.DEPRECATED;
    public AbstractRelic.LandingSound sfx = AbstractRelic.LandingSound.CLINK;

    protected static <T extends PCLRelicData> T registerData(T cardData) {
        STATIC_DATA.put(cardData.ID, cardData);
        return cardData;
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

    public PCLRelicData setImagePath(String imagePath) {
        this.imagePath = imagePath;

        return this;
    }

    public PCLRelicData setSfx(AbstractRelic.LandingSound sfx)
    {
        this.sfx = sfx;
        return this;
    }

    public PCLRelicData setTier(AbstractRelic.RelicTier tier)
    {
        this.tier = tier;
        return this;
    }

    public PCLRelicData setTier(AbstractRelic.RelicTier tier, AbstractRelic.LandingSound sfx)
    {
        this.tier = tier;
        this.sfx = sfx;
        return this;
    }
}
