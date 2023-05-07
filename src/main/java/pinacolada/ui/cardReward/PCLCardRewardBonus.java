package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.ui.EUIBase;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.card.PermanentUpgradeEffect;
import pinacolada.interfaces.listeners.OnAddingToCardRewardListener;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
// TODO Rework
public class PCLCardRewardBonus extends EUIBase {
    public static final float BASE_CHANCE = 0.3f;
    public static final int BASE_GOLD = 10;
    public static final int BASE_HP = 1;
    private final ArrayList<CardRewardBundle> bundles = new ArrayList<>();
    private RewardItem rewardItem;

    public PCLCardRewardBonus() {
        this(null);
    }

    public PCLCardRewardBonus(RewardItem rewardItem) {
        this.rewardItem = rewardItem;
    }

    public void add(AbstractCard card) {
        //CardRewardBundle cardRewardBundle = getBundle(card);
        //if (cardRewardBundle != null)
        //{
        //    bundles.add(cardRewardBundle);
        //}
    }

    // TODO rework to prevent "rerolling" rewards
    public void close() {
        rewardItem = null;
        bundles.clear();
    }

    private CardRewardBundle getBundle(AbstractCard card) {
        float chance = BASE_CHANCE / (1 + GameUtilities.getMasterDeckCopies(card.cardID).size());
        if (GameUtilities.getRNG().randomBoolean(chance)) {
            int value = GameUtilities.getRNG().random(0, 9);
            switch (value) {
                case 1:
                case 2:
                case 3:
                    return getMaxHPBundle(card, BASE_HP);
                default:
                    return getGoldBundle(card, BASE_GOLD);
            }
        }

        return null;
    }

    private CardRewardBundle getGoldBundle(AbstractCard card, int gold) {
        return new CardRewardBundle(card, this::receiveGold).setAmount(gold)
                .setIcon(ImageMaster.UI_GOLD, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.545f)
                .setText(PGR.core.strings.rewards_goldBonus(gold), Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
    }

    private CardRewardBundle getMaxHPBundle(AbstractCard card, int maxHP) {
        return new CardRewardBundle(card, this::receiveMaxHP).setAmount(maxHP)
                .setIcon(ImageMaster.TP_HP, -AbstractCard.RAW_W * 0.45f, -AbstractCard.RAW_H * 0.545f)
                .setText(PGR.core.strings.rewards_maxHPBonus(maxHP), Color.WHITE, -AbstractCard.RAW_W * 0.165f, -AbstractCard.RAW_H * 0.54f);
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
            if (card instanceof OnAddingToCardRewardListener && ((OnAddingToCardRewardListener) card).shouldCancel()) {
                toRemove.add(card);
            }

            // TODO reenable this when card rewards are ready to be added in the game
            //add(card);
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

                //add(replacement);
            }
        }
    }

    private void receiveGold(CardRewardBundle bundle) {
        PCLSFX.play(PCLSFX.GOLD_GAIN);
        AbstractDungeon.player.gainGold(bundle.amount);
    }

    private void receiveMaxHP(CardRewardBundle bundle) {
        AbstractDungeon.player.increaseMaxHp(bundle.amount, true);
    }

    private void receiveUpgrade(CardRewardBundle bundle) {
        PCLEffects.TopLevelQueue.add(new PermanentUpgradeEffect());
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
