package pinacolada.orbs;

import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.misc.PCLGenericData;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PCLOrbData extends PCLGenericData<AbstractOrb> implements KeywordProvider {
    private static final Map<String, PCLOrbData> STATIC_DATA = new HashMap<>();

    public EUIKeywordTooltip tooltip;
    public PowerStrings strings;

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources) {
        this(invokeClass, resources, resources.createID(invokeClass.getSimpleName()));
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, EUIKeywordTooltip tip) {
        this(invokeClass, resources);
        this.tooltip = tip;
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID) {
        this(invokeClass, resources, cardID, PGR.getPowerStrings(cardID));
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, EUIKeywordTooltip tip) {
        this(invokeClass, resources, cardID);
        this.tooltip = tip;
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, PowerStrings strings) {
        super(cardID, invokeClass, resources);
        this.strings = strings != null ? strings : new PowerStrings();
        //this.initializeImage();
    }

    public PCLOrbData(Class<? extends AbstractOrb> invokeClass, PCLResources<?, ?, ?, ?> resources, String cardID, PowerStrings strings, EUIKeywordTooltip tip) {
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

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return Collections.singletonList(tooltip);
    }
}
