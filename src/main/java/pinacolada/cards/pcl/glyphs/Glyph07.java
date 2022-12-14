package pinacolada.cards.pcl.glyphs;

import pinacolada.cards.base.PCLCardData;
import pinacolada.skills.PCond;
import pinacolada.skills.PMove;
import pinacolada.skills.PTrigger;

public class Glyph07 extends Glyph
{
    public static final PCLCardData DATA = registerInternal(Glyph07.class);

    public Glyph07()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addGainPower((PTrigger) PTrigger.when(PCond.onDraw().setAffinity(randomAffinity()),
                PMove.cycle(1).useParent(true)).setAmount(1, 1).setAlt(true));
    }
}