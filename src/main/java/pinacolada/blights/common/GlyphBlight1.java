package pinacolada.blights.common;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.RitualPower;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameUtilities;

public class GlyphBlight1 extends AbstractGlyphBlight
{
    public static final String ID = createFullID(GlyphBlight1.class);

    public GlyphBlight1()
    {
        super(ID, PGR.core.config.ascensionGlyph1, PCLAbstractPlayerData.ASCENSION_GLYPH1_UNLOCK, PCLAbstractPlayerData.ASCENSION_GLYPH1_LEVEL_STEP, 0, 1);
    }

    @Override
    public void atBattleStart()
    {
        super.atBattleStart();

        int potency = getPotency();
        if (potency > 0)
        {
            for (AbstractMonster mo : GameUtilities.getEnemies(true))
            {
                GameActions.top.applyPower(mo, new RitualPower(mo, potency, false));
            }
        }
    }

}