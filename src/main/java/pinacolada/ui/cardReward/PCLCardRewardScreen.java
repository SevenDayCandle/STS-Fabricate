package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.vfx.cardManip.ExhaustCardEffect;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;
import pinacolada.effects.card.HideCardEffect;
import pinacolada.interfaces.providers.CardRewardActionProvider;
import pinacolada.interfaces.providers.CardRewardBonusProvider;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLCardRewardScreen extends EUIBase {
    protected static final float REWARD_INDEX = AbstractCard.IMG_HEIGHT * 0.515f;
    protected final ArrayList<PCLCardRewardActionButton> buttons = new ArrayList<>();
    protected final ArrayList<PCLCardRewardBundle> bundles = new ArrayList<>();
    private boolean shouldClose; // Needed to prevent comodification errors
    protected CardRewardActionProvider actionProvider;
    protected CardRewardBonusProvider lastProvider;
    protected RewardItem rewardItem;
    protected boolean canReroll;
    public EUIToggle upgradeToggle;

    public PCLCardRewardScreen() {
        upgradeToggle = new EUIToggle(new EUIHitbox(scale(256), scale(48f)))
                .setBackground(EUIRM.images.greySquare.texture(), Color.DARK_GRAY)
                .setPosition(screenW(0.9f), screenH(0.65f))
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setOnToggle(this::toggleViewUpgrades);
    }

    public void action(PCLCardRewardActionButton button) {
        final int cardIndex = button.getIndex();
        final AbstractCard targetCard = button.getCard(false);
        if (targetCard == null || cardIndex > rewardItem.cards.size()) {
            return;
        }

        if (actionProvider.doAction(targetCard, rewardItem, cardIndex)) {
            takeReward();
        }
        canReroll = actionProvider.canAct();
    }

    public void close() {
        EUI.countingPanel.close();
        upgradeToggle.toggle(false);
        buttons.clear();
    }

    public void onCardObtained(AbstractCard hoveredCard) {
        for (PCLCardRewardBundle cardRewardBundle : bundles) {
            if (cardRewardBundle.card == hoveredCard) {
                cardRewardBundle.acquired();
            }
        }
    }

    protected PCLCardRewardActionButton getButton(int index) {
        return (PCLCardRewardActionButton) new PCLCardRewardActionButton(this,
                EUIRM.images.hexagonalButton.texture(), actionProvider.getTitle(), actionProvider.getDescription(), REWARD_INDEX, index, false)
                .setDimensions(AbstractCard.IMG_WIDTH * 0.75f, AbstractCard.IMG_HEIGHT * 0.14f)
                .setColor(Color.TAN)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Settings.GOLD_COLOR);
    }

    public void open(ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
        if (GameUtilities.inBattle(true) || cards == null || rItem == null) {
            close();
            return;
        }

        EUI.countingPanel.open(AbstractDungeon.player.masterDeck.group, AbstractDungeon.player.getCardColor(), false);
        openForBundle(rItem, cards);
        openForReroll(rItem);
        upgradeToggle.toggle(false);
        upgradeToggle.setActive(GameUtilities.isPCLPlayerClass()); // TODO enable this for other characters
    }

    protected void openForBundle(RewardItem rewardItem, ArrayList<AbstractCard> cards) {
        if (this.rewardItem != rewardItem) {
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
            for (CardRewardBonusProvider provider : GameUtilities.getPlayerRelics(CardRewardBonusProvider.class)) {
                if (provider.canActivate(rewardItem)) {
                    lastProvider = provider;
                    for (AbstractCard c : cards) {
                        PCLCardRewardBundle bundle = provider.getBundle(c);
                        if (bundle != null && !EUIUtils.any(bundles, b -> b.card == c)) {
                            bundles.add(bundle);
                        }
                    }
                }
            }
        }
    }

    protected void openForReroll(RewardItem rItem) {
        buttons.clear();
        actionProvider = GameUtilities.getPlayerRelic(CardRewardActionProvider.class);
        if (actionProvider != null && actionProvider.canActivate(rItem)) {
            canReroll = actionProvider.canAct();
            for (int i = 0; i < rItem.cards.size(); i++) {
                buttons.add(getButton(i));
            }
        }
    }

    public void preRender(SpriteBatch sb) {
        EUI.countingPanel.tryRender(sb);
        upgradeToggle.renderImpl(sb);
        if (canReroll) {
            for (PCLCardRewardActionButton banButton : buttons) {
                banButton.tryRender(sb);
            }
        }
    }

    public void renderImpl(SpriteBatch sb) {
        for (PCLCardRewardBundle cardRewardBundle : bundles) {
            cardRewardBundle.render(sb);
        }
    }

    protected void takeReward() {
        AbstractDungeon.combatRewardScreen.rewards.remove(rewardItem);
        AbstractDungeon.combatRewardScreen.positionRewards();
        if (AbstractDungeon.combatRewardScreen.rewards.isEmpty()) {
            AbstractDungeon.combatRewardScreen.hasTakenAll = true;
            AbstractDungeon.overlayMenu.proceedButton.show();
        }
        shouldClose = true;
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
        upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade);
    }

    public void updateImpl() {
        EUI.countingPanel.tryUpdate();
        upgradeToggle.updateImpl();
        for (PCLCardRewardBundle cardRewardBundle : bundles) {
            cardRewardBundle.update();
        }
        if (canReroll) {
            for (PCLCardRewardActionButton banButton : buttons) {
                banButton.tryUpdate();
            }
        }
        if (shouldClose) {
            shouldClose = false;
            AbstractDungeon.closeCurrentScreen();
        }
    }
}