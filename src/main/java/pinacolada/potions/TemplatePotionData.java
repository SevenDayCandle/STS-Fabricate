package pinacolada.potions;

import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PCLResources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class TemplatePotionData extends PCLPotionData {
    private static final ArrayList<TemplatePotionData> TEMPLATES = new ArrayList<>();
    public String originalID;

    public TemplatePotionData(Class<? extends PCLPotion> type, PCLResources<?, ?, ?, ?> resources, String sourceID) {
        super(type, resources);
        this.originalID = sourceID;
        TEMPLATES.add(this);
    }

    public static Collection<TemplatePotionData> getTemplates() {
        return TEMPLATES.stream().sorted((a, b) -> StringUtils.compare(a.ID, b.ID)).collect(Collectors.toList());
    }
}
