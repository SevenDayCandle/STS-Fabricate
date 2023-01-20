package pinacolada.relics.pcl;

import pinacolada.annotations.VisibleRelic;

@VisibleRelic
public class VeryUsefulBox extends AbstractBox
{
    public static final String ID = createFullID(VeryUsefulBox.class);

    public VeryUsefulBox()
    {
        super(ID, RelicTier.SPECIAL, LandingSound.SOLID);
    }
}