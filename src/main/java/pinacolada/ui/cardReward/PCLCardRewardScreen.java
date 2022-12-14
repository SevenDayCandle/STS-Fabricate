package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.ui.EUIBase;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLCardRewardScreen extends EUIBase
{
    public static final PCLCardRewardScreen Instance = new PCLCardRewardScreen();

    public final PCLCardRewardBonus rewardBundle = new PCLCardRewardBonus();
    public final PCLCardRewardInfo cardBadgeLegend = new PCLCardRewardInfo();
    public final PCLCardRewardRerollAction purgingStoneUI = new PCLCardRewardRerollAction(rewardBundle::add, rewardBundle::remove);
    public final PCLCardRewardBreakAction breakUI = new PCLCardRewardBreakAction(rewardBundle::add, rewardBundle::remove);

    public void close()
    {
        PGR.core.cardAffinities.close();
        cardBadgeLegend.close();
        rewardBundle.close();
        purgingStoneUI.close();
        breakUI.close();
    }

    public void onCardObtained(AbstractCard hoveredCard)
    {
        rewardBundle.onCardObtained(hoveredCard);
    }

    public void open(ArrayList<AbstractCard> cards, RewardItem rItem, String header)
    {
        if (GameUtilities.inBattle(true) || cards == null || rItem == null)
        {
            close();
            return;
        }

        PGR.core.cardAffinities.open(AbstractDungeon.player.masterDeck.group);
        rewardBundle.open(rItem, cards);
        breakUI.open(rItem, cards, true);
        purgingStoneUI.open(rItem, cards, !breakUI.isActive);
        cardBadgeLegend.open();
    }

    public void preRender(SpriteBatch sb)
    {
        PGR.core.cardAffinities.tryRender(sb);
        cardBadgeLegend.tryRender(sb);
        purgingStoneUI.tryRender(sb);
        breakUI.tryRender(sb);
    }

    public void updateImpl()
    {
        PGR.core.cardAffinities.tryUpdate(true);
        purgingStoneUI.tryUpdate();
        rewardBundle.tryUpdate();
        breakUI.tryUpdate();
        cardBadgeLegend.tryUpdate();
    }

    public void renderImpl(SpriteBatch sb)
    {
        rewardBundle.tryRender(sb);
    }
}