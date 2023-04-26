package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisibleRelic;
import pinacolada.powers.special.MacroscopePower;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisibleRelic
public class Macroscope extends PCLRelic {
    public static final String ID = createFullID(Macroscope.class);
    public static final int MULTIPLIER = 10;

    public Macroscope() {
        super(ID, RelicTier.SPECIAL, LandingSound.CLINK);
    }

    @Override
    public String getUpdatedDescription() {
        return formatDescription(0, MULTIPLIER);
    }

    @Override
    public void atPreBattle() {
        super.atPreBattle();

        for (AbstractCreature m : GameUtilities.getAllCharacters(true)) {
            GameUtilities.applyPowerInstantly(m, new MacroscopePower(m), 1);
            m.applyTurnPowers();
        }
        AbstractDungeon.onModifyPower();
    }

    @Override
    public void onVictory() {
        super.onVictory();
        player.maxHealth = Math.max(1, AbstractDungeon.player.maxHealth / Macroscope.MULTIPLIER);
        player.currentHealth = Math.max(1, AbstractDungeon.player.currentHealth / Macroscope.MULTIPLIER);
        player.healthBarUpdatedEvent();
        PGR.dungeon.setDivisor(1);
    }

    @Override
    public void onSpawnMonster(AbstractMonster monster) {
        GameUtilities.applyPowerInstantly(monster, new MacroscopePower(monster), 1);
    }
}