package pinacolada.blights.common;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisibleBlight;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisibleBlight
public class GlyphBlight2 extends AbstractGlyphBlight {
    public static final String ID = createFullID(GlyphBlight2.class);

    public GlyphBlight2() {
        super(ID, PGR.config.ascensionGlyph2, AbstractPlayerData.ASCENSION_GLYPH1_UNLOCK, AbstractPlayerData.ASCENSION_GLYPH1_LEVEL_STEP, 10, 5);
    }

    @Override
    public void atBattleStart() {
        super.atBattleStart();

        for (AbstractMonster mo : GameUtilities.getEnemies(true)) {
            mo.increaseMaxHp(Math.max(1, mo.maxHealth * getPotency() / 100), true);
        }
    }

}