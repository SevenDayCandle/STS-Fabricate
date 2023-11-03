package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisibleRelic;
import pinacolada.powers.special.MacroscopePower;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

@VisibleRelic
public class Macroscope extends PCLRelic {
    public static final PCLRelicData DATA = register(Macroscope.class)
            .setTier(RelicTier.SPECIAL)
            .setLoadoutValue(0)
            .setUnique(true);
    public static final int MULTIPLIER = 10;

    public Macroscope() {
        super(DATA);
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
    public String getDescriptionImpl() {
        return formatDescription(0, MULTIPLIER);
    }

    @Override
    public void onSpawnMonster(AbstractMonster monster) {
        GameUtilities.applyPowerInstantly(monster, new MacroscopePower(monster), 1);
    }

    @Override
    public void onVictory() {
        super.onVictory();
        player.maxHealth = Math.max(1, AbstractDungeon.player.maxHealth / Macroscope.MULTIPLIER);
        player.currentHealth = Math.max(1, AbstractDungeon.player.currentHealth / Macroscope.MULTIPLIER);
        player.healthBarUpdatedEvent();
        PGR.dungeon.setDivisor(1);
    }
}