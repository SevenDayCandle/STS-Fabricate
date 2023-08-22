package pinacolada.blights.common;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisibleBlight;
import pinacolada.blights.PCLBlightData;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisibleBlight
public class GlyphBlight2 extends AbstractGlyphBlight {
    public static final PCLBlightData DATA = register(GlyphBlight2.class)
            .setUnique(true);

    public GlyphBlight2() {
        super(DATA, PGR.config.ascensionGlyph2, AbstractPlayerData.ASCENSION_GLYPH1_UNLOCK, AbstractPlayerData.ASCENSION_GLYPH1_LEVEL_STEP, 10, 5);
    }

    @Override
    public void atBattleStart() {
        super.atBattleStart();

        for (AbstractMonster mo : GameUtilities.getEnemies(true)) {
            mo.increaseMaxHp(Math.max(1, mo.maxHealth * getPotency() / 100), true);
        }
    }

}