package pinacolada.relics.pcl;

import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleRelic;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;

import java.util.ArrayList;

@VisibleRelic
public class VeryUsefulBox extends PCLRelic {
    public static final PCLRelicData DATA = register(VeryUsefulBox.class).setTier(RelicTier.SPECIAL, LandingSound.SOLID);

    public VeryUsefulBox() {
        super(DATA);
    }

    @Override
    public void obtain() {
        ArrayList<AbstractRelic> relics = player.relics;
        for (int i = 0; i < relics.size(); i++) {
            UsefulBox relic = EUIUtils.safeCast(relics.get(i), UsefulBox.class);
            if (relic != null) {
                instantObtain(player, i, true);
                setCounter(relic.counter);
                return;
            }
        }

        super.obtain();
    }
}