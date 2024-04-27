package pinacolada.cards.base;

import org.apache.commons.lang3.StringUtils;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;

import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TemplateCardData extends PCLCardData {
    private static final HashMap<String, TemplateCardData> TEMPLATES = new HashMap<>();
    public String originalID;

    public TemplateCardData(Class<? extends PCLCard> type, PCLResources<?, ?, ?, ?> resources, String sourceID) {
        super(type, resources, resources.createID(type.getSimpleName()), PGR.getCardStrings(sourceID));
        this.originalID = sourceID;
        TEMPLATES.put(sourceID, this);
    }

    public static TemplateCardData getTemplate(String original) {
        return TEMPLATES.get(original);
    }

    public static Collection<TemplateCardData> getTemplates() {
        return TEMPLATES.values().stream().sorted((a, b) -> StringUtils.compare(a.ID, b.ID)).collect(Collectors.toList());
    }
}
