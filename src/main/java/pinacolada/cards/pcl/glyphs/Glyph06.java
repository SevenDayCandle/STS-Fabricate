package pinacolada.cards.pcl.glyphs;

import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCardData;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

public class Glyph06 extends Glyph {
    public static final PCLCardData DATA = registerInternal(Glyph06.class);

    public Glyph06() {
        super(DATA);
    }

    public void setup(Object input) {
        addGainPower(PTrigger.when(PCond.onOtherCardPlayed(randomAffinity()),
                getSpecialMove(0, this::action, 1).setUpgrade(1)).setAmount(1));
    }

    public void action(PSpecialSkill move, PCLUseInfo info) {
        for (AbstractMonster m1 : GameUtilities.getEnemies(true)) {
            PCLActions.bottom.add(new HealAction(m1, null, move.amount));
        }
    }
}