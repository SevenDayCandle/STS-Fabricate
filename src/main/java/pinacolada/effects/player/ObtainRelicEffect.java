package pinacolada.effects.player;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import pinacolada.effects.PCLEffect;
import pinacolada.utilities.GameUtilities;

public class ObtainRelicEffect extends PCLEffect {
    private final AbstractRelic relic;

    public ObtainRelicEffect(AbstractRelic relic) {
        this.relic = relic;
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        GameUtilities.obtainRelicFromEvent(relic);

        complete();
    }
}