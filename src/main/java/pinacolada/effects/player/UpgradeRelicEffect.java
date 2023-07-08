package pinacolada.effects.player;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import pinacolada.effects.PCLEffect;
import pinacolada.relics.PCLRelic;

public class UpgradeRelicEffect extends PCLEffect {
    private final AbstractRelic relic;
    private final int amount;

    public UpgradeRelicEffect(AbstractRelic relic) {
        this(relic, 1);
    }

    public UpgradeRelicEffect(AbstractRelic relic, int amount) {
        this.relic = relic;
        this.amount = amount;
    }

    @Override
    protected void firstUpdate() {
        if (relic instanceof PCLRelic) {
            for (int i = 0; i < amount; i++) {
                ((PCLRelic) relic).upgrade();
            }
            relic.flash();
        }

        complete();
    }
}