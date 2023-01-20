package pinacolada.cards.pcl.glyphs;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.cards.base.PCLCardData;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;

public class Glyph02 extends Glyph
{
    public static final PCLCardData DATA = registerInternal(Glyph02.class);

    public Glyph02()
    {
        super(DATA);
    }

    public void action(PSpecialSkill move, PCLUseInfo info)
    {
        for (AbstractMonster mo : GameUtilities.getEnemies(true))
        {
            mo.increaseMaxHp(Math.max(1, mo.maxHealth * move.amount / 100), true);
        }
    }

    public void setup(Object input)
    {
        addUseMove(getSpecialMove(ef -> PGR.core.strings.actions.gainAmount(ef.getAmountRawString(), PGR.core.tooltips.maxHP), this::action, 1).setUpgrade(1));
    }
}