package pinacolada.ui.combat;

public class PCLEmptyMeter extends PCLPlayerMeter
{
    public PCLEmptyMeter()
    {
        super(null, 12);
    }

    @Override
    public String[] getInfoDescription()
    {
        return new String[0];
    }

    @Override
    public String getInfoMainDescrption()
    {
        return null;
    }

    @Override
    public String getInfoTitle()
    {
        return null;
    }
}
