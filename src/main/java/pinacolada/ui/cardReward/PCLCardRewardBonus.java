package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.ui.EUIBase;
import pinacolada.interfaces.providers.CardRewardBonusProvider;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLCardRewardBonus extends EUIBase {
    protected CardRewardBonusProvider provider;
    protected final ArrayList<CardRewardBundle> bundles = new ArrayList<>();
    protected RewardItem rewardItem;

    public PCLCardRewardBonus() {
        this(null);
    }

    public PCLCardRewardBonus(RewardItem rewardItem) {
        this.rewardItem = rewardItem;
    }

    // Called when rewards get rerolled through dice
    public void addManual(AbstractCard card) {
         if (provider != null && provider.canActivate(rewardItem)) {
             CardRewardBundle bundle = provider.getBundle(card);
             if (bundle != null) {
                 bundles.add(bundle);
             }
         }
    }

    // TODO rework to prevent "rerolling" rewards
    public void close() {
        rewardItem = null;
        bundles.clear();
    }

    public void onCardObtained(AbstractCard hoveredCard) {
        for (CardRewardBundle cardRewardBundle : bundles) {
            if (cardRewardBundle.card == hoveredCard) {
                cardRewardBundle.acquired();
            }
        }
    }

    public void open(RewardItem rewardItem, ArrayList<AbstractCard> cards) {
        this.rewardItem = rewardItem;
        this.bundles.clear();



        final ArrayList<AbstractCard> toRemove = new ArrayList<>();
        for (AbstractCard card : cards) {
            if (PGR.dungeon.tryCancelCardReward(card)) {
                toRemove.add(card);
            }
        }

        for (AbstractCard card : toRemove) {
            final AbstractCard replacement = PGR.dungeon.getRandomRewardReplacementCard(card.rarity, cards, AbstractDungeon.cardRng, true);
            if (replacement != null) {
                GameUtilities.copyVisualProperties(replacement, card);
                cards.remove(card);
                cards.add(replacement);
                if (rewardItem.cards != cards) {
                    rewardItem.cards.remove(card);
                    rewardItem.cards.add(replacement);
                }
            }
        }

        // TODO allow card reward bonus provider to be cards/blights as well
        provider = GameUtilities.getPlayerRelic(CardRewardBonusProvider.class);
        if (provider != null && provider.canActivate(rewardItem)) {
            for (AbstractCard c : cards) {
                CardRewardBundle bundle = provider.getBundle(c);
                if (bundle != null) {
                    bundles.add(bundle);
                }
            }
        }
    }

    public void remove(AbstractCard card) {
        for (int i = 0; i < bundles.size(); i++) {
            if (bundles.get(i).card == card) {
                bundles.remove(i);
                return;
            }
        }
    }

    public void renderImpl(SpriteBatch sb) {
        for (CardRewardBundle cardRewardBundle : bundles) {
            cardRewardBundle.render(sb);
        }
    }

    public void updateImpl() {
        for (CardRewardBundle cardRewardBundle : bundles) {
            cardRewardBundle.update();
        }
    }
}
