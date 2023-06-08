package pinacolada.relics;

import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PCLResources;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TemplateRelicData extends PCLRelicData {
    private static final HashMap<String, TemplateRelicData> TEMPLATES = new HashMap<>();
    public String originalID;

    public TemplateRelicData(Class<? extends PCLRelic> type, PCLResources<?, ?, ?, ?> resources, String sourceID) {
        super(type, resources);
        this.originalID = sourceID;
        TEMPLATES.put(sourceID, this);
    }

    public static TemplateRelicData getTemplate(String original) {
        return TEMPLATES.get(original);
    }

    public static Collection<TemplateRelicData> getTemplates() {
        return TEMPLATES.values().stream().sorted((a, b) -> StringUtils.compare(a.ID, b.ID)).collect(Collectors.toList());
    }
}
