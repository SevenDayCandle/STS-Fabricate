package pinacolada.ui.combat;

import extendedui.EUIUtils;
import pinacolada.resources.PGR;

// TODO add basic matching logic
public class PCLEmptyMeter extends PCLPlayerMeter
{
    public static final String ID = createFullID(PGR.core, PCLEmptyMeter.class);

    public PCLEmptyMeter()
    {
        super(ID, null, 12);
        infoIcon.setActive(false);
        draggablePanel.setActive(false);
        draggableIcon.setActive(false);
    }

    @Override
    public String[] getInfoDescription()
    {
        return EUIUtils.array("");
    }

    @Override
    public String getInfoMainDescrption()
    {
        return "";
    }

    @Override
    public String getInfoTitle()
    {
        return "";
    }
}
