package pinacolada.interfaces.subscribers;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public interface OnRelicObtainedSubscriber
{
    enum Trigger
    {
        Equip,
        Obtain,
        BossChest,
        MetricData
    }

    void onRelicObtained(AbstractRelic relic, Trigger trigger);
}
