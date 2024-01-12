package pinacolada.ui.cardReward;

import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.CacheableCard;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.providers.CardRewardActionProvider;
import pinacolada.interfaces.providers.CardRewardBonusProvider;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// Copied and modified from STS-AnimatorMod
public class PCLCardRewardScreen extends EUIBase {
    protected static final float REWARD_INDEX = AbstractCard.IMG_HEIGHT * 0.515f;
    public static final HashSet<String> seenCards = new HashSet<>();
    protected final ArrayList<PCLCardRewardActionButton> buttons = new ArrayList<>();
    protected final ArrayList<PCLCardRewardBundle> bundles = new ArrayList<>();
    protected final HashMap<AbstractCard, AbstractCard> upgrades = new HashMap<>();
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
                .setFont(FontHelper.cardDescFont_L, 0.5f)
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

    public void close(boolean clearBundles) {
        EUI.countingPanel.close();
        upgradeToggle.toggleForce(false);
        buttons.clear();
        if (clearBundles) {
            bundles.clear();
            PCLCardRewardScreen.seenCards.clear();
        }
        upgrades.clear();
    }

    public AbstractCard getActualCardToRender(AbstractCard c) {
        if (SingleCardViewPopup.isViewingUpgrade && c.canUpgrade()) {
            AbstractCard upgrade = upgrades.get(c);
            if (upgrade == null) {
                if (c instanceof CacheableCard) {
                    upgrade = ((CacheableCard) c).getCachedUpgrade();
                }
                else {
                    try {
                        upgrade = c.makeSameInstanceOf();
                        upgrade.upgrade();
                        upgrade.displayUpgrades();
                    }
                    catch (Exception e) {
                        EUIUtils.logError(this, "Why is your card crashing on upgrade :( " + c);
                        upgrade = c;
                    }
                }
                upgrades.put(c, upgrade);
            }
            return upgrade;
        }
        return c;
    }

    protected PCLCardRewardActionButton getButton(int index) {
        return (PCLCardRewardActionButton) new PCLCardRewardActionButton(this,
                EUIRM.images.hexagonalButton.texture(), actionProvider.getTitle(), actionProvider.getDescription(), REWARD_INDEX, index, false)
                .setDimensions(AbstractCard.IMG_WIDTH * 0.75f, AbstractCard.IMG_HEIGHT * 0.14f)
                .setColor(Color.TAN)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Settings.GOLD_COLOR);
    }

    public void onCardObtained(AbstractCard hoveredCard) {
        for (PCLCardRewardBundle cardRewardBundle : bundles) {
            if (cardRewardBundle.card == hoveredCard) {
                cardRewardBundle.acquired();
            }
        }
    }

    public void open(ArrayList<AbstractCard> cards, RewardItem rItem, String header) {
        if (CombatManager.inBattleForceRefresh() || cards == null || rItem == null) {
            close(true);
            return;
        }

        EUI.countingPanel.open(AbstractDungeon.player.masterDeck.group, AbstractDungeon.player.getCardColor(), EUIGameUtils.canReceiveAnyColorCard(), false);
        openForBundle(rItem, cards);
        openForReroll(rItem);
        upgradeToggle.toggleForce(false);
        upgradeToggle.setActive(GameUtilities.isPCLPlayerClass() || PGR.config.showUpgradeOnCardRewards.get());
        upgrades.clear();
    }

    protected void openForBundle(RewardItem rewardItem, ArrayList<AbstractCard> cards) {
        if (this.rewardItem != rewardItem) {
            this.rewardItem = rewardItem;
            this.bundles.clear();
            PCLCardRewardScreen.seenCards.clear();

            final ArrayList<AbstractCard> toRemove = new ArrayList<>();
            for (AbstractCard card : cards) {
                if (PGR.dungeon.tryCancelCardReward(card)) {
                    toRemove.add(card);
                }
            }

            for (AbstractCard card : toRemove) {
                final AbstractCard replacement = PGR.dungeon.getRandomRewardReplacementCard(card.rarity, c -> !(EUIUtils.any(cards, i -> i.cardID.equals(c.cardID))), AbstractDungeon.cardRng, true);
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

    public void renderCardReward(SpriteBatch sb, AbstractCard c) {
        AbstractCard toRender = getActualCardToRender(c);
        if (toRender != c) {
            GameUtilities.copyVisualProperties(toRender, c);
        }
        toRender.render(sb);
    }

    public boolean renderCardRewardTip(SpriteBatch sb, AbstractCard c) {
        AbstractCard toRender = getActualCardToRender(c);
        if (toRender != c) {
            ReflectionHacks.setPrivate(toRender, AbstractCard.class, "renderTip", ReflectionHacks.getPrivate(c, AbstractCard.class, "renderTip"));
            toRender.renderCardTip(sb);
            return false;
        }
        return true;
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