package pinacolada.relics.pcl;

public class UsefulBox extends AbstractBox
{
    public static final String ID = createFullID(UsefulBox.class);

    public UsefulBox()
    {
        super(ID, RelicTier.SPECIAL, LandingSound.SOLID);
    }
}