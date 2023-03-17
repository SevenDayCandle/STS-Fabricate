package pinacolada.ui.cardReward;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.cardFilter.CustomCardPoolModule;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.augments.PCLAugmentCategory;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.CountingPanelStats;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;

public class CountingPanel extends CustomCardPoolModule
{
    public static final float ICON_SIZE = scale(40);

    private static final PCLCoreImages.AffinityIcons ICONS = PGR.core.images.affinities;

    private final EUIHitbox hb;
    private ArrayList<? extends CountingPanelCounter<?>> counters;
    private long lastFrame;

    public CountingPanel()
    {
        hb = new DraggableHitbox(screenW(0.025f), screenH(0.65f), scale(140), scale(50), false);
    }

    public void close()
    {
        setActive(false);
    }

    public void open(ArrayList<AbstractCard> cards)
    {
        open(cards, false, null, false);
    }

    public void open(ArrayList<AbstractCard> cards, boolean showUpgradeToggle, ActionT1<CountingPanelCounter<PCLAffinity>> onClick, boolean force)
    {
        isActive = (force || GameUtilities.isPCLPlayerClass()) && cards != null;

        if (!isActive)
        {
            return;
        }

        counters = CountingPanelStats.affinityStats(cards).generateCounters(hb, onClick);
    }

    public void open(HashMap<PCLAugmentData, Integer> augments, ActionT1<CountingPanelCounter<PCLAugmentCategory>> onClick, boolean force)
    {
        isActive = (force || GameUtilities.isPCLPlayerClass()) && augments != null;

        if (!isActive)
        {
            return;
        }

        counters = CountingPanelStats.augmentStats(augments).generateCounters(hb, onClick);
    }

    @Override
    public void update(boolean shouldDoStandardUpdate)
    {
        if (shouldDoStandardUpdate)
        {
            hb.update();

            if (counters != null)
            {
                for (CountingPanelCounter<?> c : counters)
                {
                    c.tryUpdate();
                }
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        long frame = Gdx.graphics.getFrameId();
        if (frame == lastFrame)
        {
            return;
        }

        lastFrame = frame;
        if (counters != null)
        {
            for (CountingPanelCounter<?> c : counters)
            {
                c.tryRender(sb);
            }
        }
        hb.render(sb);
    }
}
