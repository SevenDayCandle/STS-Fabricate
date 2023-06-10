package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.EUI;
import extendedui.ui.EUIBase;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLCardRewardScreen extends EUIBase {
    public static final PCLCardRewardScreen Instance = new PCLCardRewardScreen();

    public final PCLCardRewardBonus rewardBundle = new PCLCardRewardBonus();
    public final PCLCardRewardInfo rewardInfo = new PCLCardRewardInfo();
    public final PCLCardRewardRerollAction rerollUI = new PCLCardRewardRerollAction(rewardBundle::addManual, rewardBundle::remove);

    public void close() {
        EUI.countingPanel.close();
        rewardInfo.close();
        rewardBundle.close();
        rerollUI.close();
    }

    public void onCardObtained(AbstractCard hoveredCard) {
        rewardBundle.onCardObtained(hoveredCard);
    }

    public void open(ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
        if (GameUtilities.inBattle(true) || cards == null || rItem == null) {
            close();
            return;
        }

        EUI.countingPanel.open(AbstractDungeon.player.masterDeck.group, AbstractDungeon.player.getCardColor(), false);
        rewardBundle.open(rItem, cards);
        rerollUI.open(rItem, cards);
        rewardInfo.open();
    }

    public void preRender(SpriteBatch sb) {
        EUI.countingPanel.tryRender(sb);
        rewardInfo.tryRender(sb);
        rerollUI.tryRender(sb);
    }

    public void renderImpl(SpriteBatch sb) {
        rewardBundle.tryRender(sb);
    }

    public void updateImpl() {
        EUI.countingPanel.tryUpdate();
        rerollUI.tryUpdate();
        rewardBundle.tryUpdate();
        rewardInfo.tryUpdate();
    }
}