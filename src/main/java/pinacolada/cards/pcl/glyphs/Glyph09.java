package pinacolada.cards.pcl.glyphs;

import pinacolada.cards.base.PCLCardData;
import pinacolada.powers.PCLPowerData;
import pinacolada.skills.PMove;

public class Glyph09 extends Glyph {
    public static final PCLCardData DATA = registerInternal(Glyph09.class);

    public Glyph09() {
        super(DATA);
    }

    public void setup(Object input) {
        addUseMove(PMove.applyToEnemies(3, PCLPowerData.PlatedArmor).setUpgrade(1));
    }
}