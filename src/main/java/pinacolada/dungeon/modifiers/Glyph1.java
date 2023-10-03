package pinacolada.dungeon.modifiers;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.RitualPower;
import pinacolada.actions.PCLActions;
import pinacolada.resources.AbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public class Glyph1 extends AbstractGlyph {
    public static final String ID = createFullID(Glyph1.class);

    public Glyph1() {
        super(ID, PGR.config.ascensionGlyph1, AbstractPlayerData.ASCENSION_GLYPH1_UNLOCK, AbstractPlayerData.ASCENSION_GLYPH1_LEVEL_STEP, 0, 1);
    }

    @Override
    public void atBattleStart(int counter) {
        super.atBattleStart(counter);

        int potency = getPotency(counter);
        if (potency > 0) {
            for (AbstractMonster mo : GameUtilities.getEnemies(true)) {
                PCLActions.top.applyPower(mo, new RitualPower(mo, potency, false));
            }
        }
    }
}