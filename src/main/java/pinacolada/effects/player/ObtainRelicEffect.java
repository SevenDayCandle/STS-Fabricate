package pinacolada.effects.player;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import pinacolada.effects.PCLEffect;

public class ObtainRelicEffect extends PCLEffect
{
    private final AbstractRelic relic;

    public ObtainRelicEffect(AbstractRelic relic)
    {
        this.relic = relic;
    }

    @Override
    protected void firstUpdate()
    {
        relic.instantObtain();
        CardCrawlGame.metricData.addRelicObtainData(relic);

        complete();
    }
}