package pinacolada.resources;

import com.megacrit.cardcrawl.localization.UIStrings;

public class PCLStrings
{
    protected PCLResources<?,?,?,?> resources;
    public PCLStrings(PCLResources<?,?,?,?> resources)
    {
        this.resources = resources;
    }

    public String[] getTutorialStrings()
    {
        return new String[]{};
    }

    protected UIStrings getUIStrings(String id)
    {
        return resources.getUIStrings(id);
    }
}
