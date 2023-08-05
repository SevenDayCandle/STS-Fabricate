package pinacolada.effects.player;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import pinacolada.effects.PCLEffectWithCallback;

public class RemoveRelicEffect extends PCLEffectWithCallback<AbstractRelic> {
    private final AbstractRelic relic;

    public RemoveRelicEffect(AbstractRelic relic) {
        this.relic = relic;
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        relic.onUnequip();
        AbstractDungeon.player.relics.remove(relic);
        AbstractDungeon.player.reorganizeRelics();

        complete(relic);
    }
}