package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public interface OnRelicObtainedListener
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
