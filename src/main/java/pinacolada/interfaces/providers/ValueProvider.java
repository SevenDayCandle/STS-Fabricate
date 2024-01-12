package pinacolada.interfaces.providers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public interface ValueProvider {

    default AbstractCreature getSourceCreature() {
        return AbstractDungeon.player;
    }
    default int getXValue() {return 1;}
    default int timesUpgraded() {
        return 0;
    }
}
