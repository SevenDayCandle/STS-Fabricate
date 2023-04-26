package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.relics.AbstractRelic;

public interface OnRelicObtainedListener {
    void onRelicObtained(AbstractRelic relic, Trigger trigger);

    enum Trigger {
        Equip,
        Obtain,
        BossChest,
        MetricData
    }
}
