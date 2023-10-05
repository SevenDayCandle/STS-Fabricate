package pinacolada.dungeon.modifiers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class Glyph0 extends AbstractGlyph {
    public static final String ID = createFullID(Glyph0.class);

    public Glyph0() {
        super(ID, PGR.config.ascensionGlyph0, AbstractPlayerData.ASCENSION_GLYPH1_UNLOCK, AbstractPlayerData.ASCENSION_GLYPH1_LEVEL_STEP, 10, 5);
    }

    @Override
    public void atBattleStart(int counter) {
        super.atBattleStart(counter);

        for (AbstractMonster mo : GameUtilities.getEnemies(true)) {
            mo.increaseMaxHp(Math.max(1, mo.maxHealth * getPotency(counter) / 100), true);
        }
    }

}