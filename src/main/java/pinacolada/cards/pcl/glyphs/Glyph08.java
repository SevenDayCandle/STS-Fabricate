package pinacolada.cards.pcl.glyphs;

import pinacolada.cards.base.PCLCardData;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PMove;

public class Glyph08 extends Glyph {
    public static final PCLCardData DATA = registerInternal(Glyph08.class);

    public Glyph08() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.applyToEnemies(3, PCLPowerData.Thorns, PCLPowerData.Artifact).setUpgrade(1));
    }
}