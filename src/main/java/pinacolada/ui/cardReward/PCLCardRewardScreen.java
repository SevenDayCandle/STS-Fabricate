package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.ui.EUIBase;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLCardRewardScreen extends EUIBase
{
    public static final PCLCardRewardScreen Instance = new PCLCardRewardScreen();

    public final PCLCardRewardBonus rewardBundle = new PCLCardRewardBonus();
    public final PCLCardRewardInfo rewardInfo = new PCLCardRewardInfo();
    public final PCLCardRewardRerollAction purgingStoneUI = new PCLCardRewardRerollAction(rewardBundle::add, rewardBundle::remove);
    public final PCLCardRewardBreakAction breakUI = new PCLCardRewardBreakAction(rewardBundle::add, rewardBundle::remove);

    public void close()
    {
        PGR.cardAffinities.close();
        rewardInfo.close();
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

        PGR.cardAffinities.open(AbstractDungeon.player.masterDeck.group);
        rewardBundle.open(rItem, cards);
        breakUI.open(rItem, cards, true);
        purgingStoneUI.open(rItem, cards, !breakUI.isActive);
        rewardInfo.open();
    }

    public void preRender(SpriteBatch sb)
    {
        PGR.cardAffinities.tryRender(sb);
        rewardInfo.tryRender(sb);
        purgingStoneUI.tryRender(sb);
        breakUI.tryRender(sb);
    }

    public void updateImpl()
    {
        PGR.cardAffinities.tryUpdate(true);
        purgingStoneUI.tryUpdate();
        rewardBundle.tryUpdate();
        breakUI.tryUpdate();
        rewardInfo.tryUpdate();
    }

    public void renderImpl(SpriteBatch sb)
    {
        rewardBundle.tryRender(sb);
    }
}