package pinacolada.ui.cardReward;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.cardFilter.CustomCardPoolModule;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardAffinityStatistics;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;

public class CardAffinityPanel extends CustomCardPoolModule
{
    public static final float ICON_SIZE = scale(40);

    private static final PCLCoreImages.AffinityIcons ICONS = PGR.core.images.affinities;

    private final EUIHitbox hb;
    private final ArrayList<CardAffinityCounter> counters = new ArrayList<>();
    private final PCLCardAffinityStatistics statistics;
    private final EUIToggle upgradeToggle;
    private long lastFrame;

    public CardAffinityPanel()
    {
        statistics = new PCLCardAffinityStatistics();
        hb = new DraggableHitbox(screenW(0.025f), screenH(0.65f), scale(140), scale(50), false);

        for (PCLAffinity t : PCLAffinity.all())
        {
            counters.add(new CardAffinityCounter(hb, t));
        }

        upgradeToggle = new EUIToggle(RelativeHitbox.fromPercentages(hb, 1.3f, 1, 0.5f * (1 - (ICON_SIZE / hb.width)), -(0.5f + counters.size())))
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.4f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(this::toggleViewUpgrades);
    }

    public void close()
    {
        setActive(false);
    }

    public void open(ArrayList<AbstractCard> cards)
    {
        open(cards, false, null, false);
    }

    public void open(ArrayList<AbstractCard> cards, boolean showUpgradeToggle, ActionT1<CardAffinityCounter> onClick, boolean force)
    {
        isActive = (force || GameUtilities.isPCLPlayerClass()) && cards != null;

        if (!isActive)
        {
            return;
        }

        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade).setActive(showUpgradeToggle);
        statistics.reset();
        statistics.addCards(cards);
        PCLAffinity[] available = PCLAffinity.getAvailableAffinities();

        for (CardAffinityCounter c : counters)
        {
            c.setOnClick(onClick).setActive(EUIUtils.any(available, a -> c.type == a));
            c.initialize(statistics);
        }

        refresh(showUpgradeToggle && upgradeToggle.toggled);
    }

    public void open(HashMap<PCLAugmentData, Integer> augments, ActionT1<CardAffinityCounter> onClick, boolean force)
    {
        isActive = (force || GameUtilities.isPCLPlayerClass()) && augments != null;

        if (!isActive)
        {
            return;
        }

        upgradeToggle.setActive(false);
        statistics.reset();
        statistics.addAugments(augments);
        PCLAffinity[] available = PCLAffinity.basic();

        for (CardAffinityCounter c : counters)
        {
            c.setOnClick(onClick).setActive(EUIUtils.any(available, a -> c.type == a));
            c.initialize(statistics);
        }

        refresh(false);
    }

    public void refresh(boolean showUpgrades)
    {
        statistics.refreshStatistics(false);

        counters.sort((a, b) -> (int) (1000 * (b.affinityGroup.getPercentage(0) - a.affinityGroup.getPercentage(0))));

        int index = 0;
        for (CardAffinityCounter c : counters)
        {
            if (c.isActive)
            {
                c.setIndex(index);
                index += 1;
            }
        }
    }

    protected void toggleViewUpgrades(boolean value)
    {
        SingleCardViewPopup.isViewingUpgrade = value;
        refresh(value);
    }

    @Override
    public void update(boolean shouldDoStandardUpdate)
    {
        if (shouldDoStandardUpdate)
        {
            hb.update();

            for (CardAffinityCounter c : counters)
            {
                c.tryUpdate();
            }

            if (upgradeToggle.isActive)
            {
                if (upgradeToggle.toggled != SingleCardViewPopup.isViewingUpgrade)
                {
                    upgradeToggle.toggle(SingleCardViewPopup.isViewingUpgrade);
                }

                upgradeToggle.setInteractable(PCLEffects.isEmpty()).updateImpl();
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
        for (CardAffinityCounter c : counters)
        {
            c.tryRender(sb);
        }
        upgradeToggle.tryRender(sb);
        hb.render(sb);
    }
}
