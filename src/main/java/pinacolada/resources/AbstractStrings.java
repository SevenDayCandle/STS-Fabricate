package pinacolada.resources;

import com.megacrit.cardcrawl.localization.UIStrings;

public class AbstractStrings {
    protected PCLResources<?, ?, ?, ?> resources;

    public AbstractStrings(PCLResources<?, ?, ?, ?> resources) {
        this.resources = resources;
    }

    public String[] getTutorialStrings() {
        return new String[]{};
    }

    protected UIStrings getUIStrings(String id) {
        return resources.getUIStrings(id);
    }
}
