package pinacolada.ui.cardView;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.effects.screen.ApplyAugmentToCardEffect;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLValueEditor;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Collections;

import static pinacolada.skills.PSkill.COLON_SEPARATOR;

public class PCLSingleCardPopup extends PCLSingleItemPopup<AbstractCard, PCLCard> {
    protected static final float AUGMENT_X = Settings.WIDTH * 0.77f;
    protected static final float AUGMENT_Y = Settings.HEIGHT * 0.8f;
    protected static final float ICON_SIZE = 64f * Settings.scale;
    protected static final String[] TEXT = SingleCardViewPopup.TEXT;

    protected final ArrayList<PCLAugmentViewer> currentAugments = new ArrayList<>();
    protected final EUIToggle upgradeToggle;
    protected final EUIToggle betaArtToggle;
    protected final PCLValueEditor changeVariantEditor;
    protected final EUIButton changeVariant;
    protected final EUIButton toggleAugment;
    protected final EUILabel maxUpgradesLabel;
    protected final EUILabel maxCopiesLabel;
    private PCLCard upgradedCard;
    private CardGroup group;
    private ApplyAugmentToCardEffect effect;
    protected boolean showAugments;
    protected boolean viewBetaArt;
    protected int currentForm;
    public PCLCard displayCard;

    public PCLSingleCardPopup() {
        super(new EUIHitbox(550f * Settings.scale, 770f * Settings.scale));

        this.upgradeToggle = new EUIToggle(new EUIHitbox(250f * Settings.scale, 80f * Settings.scale)).setText(TEXT[6])
                .setTickImage(null, new EUIImage(ImageMaster.TICK, Color.WHITE), 64)
                .setFontColors(Settings.GOLD_COLOR, Settings.BLUE_TEXT_COLOR)
                .setControllerAction(CInputActionSet.proceed)
                .setFont(FontHelper.cardTitleFont, 1)
                .setOnToggle(this::toggleUpgrade);

        this.betaArtToggle = new EUIToggle(new EUIHitbox(250f * Settings.scale, 80f * Settings.scale)).setText(TEXT[14])
                .setTickImage(null, new EUIImage(ImageMaster.TICK, Color.WHITE), 64)
                .setFontColors(Settings.GOLD_COLOR, Settings.BLUE_TEXT_COLOR)
                .setControllerAction(CInputActionSet.proceed)
                .setFont(FontHelper.cardTitleFont, 1)
                .setOnToggle(this::toggleBetaArt);

        this.toggleAugment = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(Settings.WIDTH * 0.85f, Settings.HEIGHT * 0.95f, scale(240), scale(50)))
                .setLabel(FontHelper.buttonLabelFont, 0.75f, showAugments ? PGR.core.strings.scp_viewTooltips : PGR.core.strings.scp_viewAugments)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> toggleAugmentView(!showAugments));

        this.maxUpgradesLabel = new EUILabel(FontHelper.tipHeaderFont,
                new EUIHitbox(screenW(0.008f), screenH(0.88f), screenW(0.21f), EUIBase.scale(32)))
                .setAlignment(0.9f, 0.1f, true);
        this.maxUpgradesLabel.setTooltip(PGR.core.strings.cedit_maxUpgrades, PGR.core.strings.cetut_maxUpgrades);

        this.maxCopiesLabel = new EUILabel(FontHelper.tipHeaderFont,
                new EUIHitbox(screenW(0.008f), screenH(0.855f), screenW(0.21f), EUIBase.scale(32)))
                .setAlignment(0.9f, 0.1f, true);
        this.maxCopiesLabel.setTooltip(PGR.core.strings.cedit_maxCopies, PGR.core.strings.cetut_maxCopies);

        this.changeVariantEditor = new PCLValueEditor(
                new EUIHitbox(screenW(0.1f), screenH(0.8f), ICON_SIZE, ICON_SIZE), PGR.core.strings.scp_variant, this::changePreviewForm);
        this.changeVariantEditor.header.setFont(FontHelper.tipHeaderFont)
                .setAlignment(0.9f, 0.1f, false)
                .setColor(Color.WHITE)
                .setHitbox(new EUIHitbox(screenW(0.008f), screenH(0.76f), screenW(0.21f), EUIBase.scale(32)));
        this.changeVariant = new EUIButton(EUIRM.images.hexagonalButton.texture(), new EUIHitbox(screenW(0.012f), screenH(0.76f), 200f * Settings.scale, 150f * Settings.scale))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setDimensions(screenW(0.18f), screenH(0.07f))
                .setText(PGR.core.strings.scp_changeVariant)
                .setTooltip(PGR.core.strings.scp_changeVariant, PGR.core.strings.scp_changeVariantTooltipAlways)
                .setOnClick(this::changeCardForm)
                .setColor(Color.FIREBRICK);
    }

    protected void actualClose() {
        super.actualClose();
        FontHelper.ClearSCPFontTextures();
    }

    private void applyAugment(PCLAugment augment, int ind) {
        PGR.dungeon.removeAugment(augment.save);
        currentItem.addAugment(augment, ind, true);
        this.displayCard = currentItem.makePopupCopy();
        this.upgradedCard = getUpgradeCard();
        refreshAugments();
    }

    private void changeCardForm() {
        if (currentItem != null && currentItem.auxiliaryData.form != currentForm) {
            currentItem.changeForm(currentForm, currentItem.timesUpgraded);
            //aCard.canBranch = false;
        }
    }

    public void changePreviewForm(int newForm) {
        if (displayCard != null && newForm >= 0 && newForm <= displayCard.maxForms() - 1) {
            this.currentForm = displayCard.changeForm(newForm, displayCard.timesUpgraded);
            upgradedCard = displayCard.makePopupCopy();
            upgradedCard.changeForm(newForm, displayCard.timesUpgraded);
            upgradedCard.upgrade();
            upgradedCard.displayUpgrades();
        }
    }

    public void close() {
        super.close();
        if (AbstractDungeon.player != null) {
            SingleCardViewPopup.isViewingUpgrade = false;
        }

        if (this.displayCard != null) {
            this.displayCard.unloadSingleCardView();
        }

        this.currentItem = null;
        this.displayCard = null;
        this.upgradedCard = null;
        this.currentForm = 0;
    }

    public PCLCard getCard() {
        if (SingleCardViewPopup.isViewingUpgrade) {
            if (upgradedCard == null) {
                upgradedCard = getUpgradeCard();
            }

            return upgradedCard;
        }
        else {
            return displayCard;
        }
    }

    private String getCardCopiesText() {
        if (displayCard == null) {
            return "";
        }
        int currentCopies = (AbstractDungeon.player != null ? EUIUtils.count(AbstractDungeon.player.masterDeck.group, c -> c.cardID.equals(displayCard.cardID)) : -1);
        int maxCopies = displayCard.cardData != null ? displayCard.cardData.maxCopies : -1;

        if (currentCopies >= 0 && maxCopies > 0) {
            return currentCopies + "/" + maxCopies;
        }
        else if (currentCopies >= 0) {
            return String.valueOf(currentCopies);
        }
        else if (maxCopies > 0) {
            return String.valueOf(maxCopies);
        }
        else {
            return PGR.core.strings.subjects_infinite;
        }
    }

    private String getCardUpgradesText() {
        if (displayCard == null) {
            return "";
        }
        int current = displayCard.timesUpgraded;
        int max = displayCard.cardData != null ? displayCard.cardData.maxUpgradeLevel : 1;

        if (current >= 0 && max > 0) {
            return current + "/" + max;
        }
        else if (current >= 0) {
            return String.valueOf(current);
        }
        else if (max >= 0) {
            return String.valueOf(max);
        }
        else {
            return PGR.core.strings.subjects_infinite;
        }
    }

    @Override
    protected String getCredits(PCLCard currentItem) {
        if (currentItem != null) {
            return currentItem.cardData.getAuthorString();
        }
        return null;
    }

    @Override
    protected Iterable<? extends EUITooltip> getTipsToDisplay(PCLCard currentItem) {
        return currentItem != null ? ((TooltipProvider) currentItem).getTipsForRender() : Collections.emptyList();
    }

    private PCLCard getUpgradeCard() {
        upgradedCard = displayCard.makePopupCopy();
        upgradedCard.upgrade();
        upgradedCard.displayUpgrades();
        return upgradedCard;
    }

    private void initializeAugments() {
        currentAugments.clear();
        // Do not show augments for cards not in your deck, or if the card does not have augment slots
        if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck.contains(currentItem) && currentItem.augments.size() > 0) {
            toggleAugment.setActive(true);
            float curY = AUGMENT_Y;
            for (int i = 0; i < currentItem.augments.size(); i++) {
                int finalI = i;
                PCLAugmentViewer viewer = new PCLAugmentViewer(new EUIHitbox(AUGMENT_X, curY, scale(300), scale(360)), currentItem, i)
                        .setOnClick(() -> {
                            if (currentItem.augments.get(finalI) == null) {
                                if (PGR.dungeon.augmentList.size() > 0) {
                                    this.effect = (ApplyAugmentToCardEffect) new ApplyAugmentToCardEffect(currentItem)
                                            .addCallback((augment -> {
                                                if (augment != null) {
                                                    applyAugment(augment, finalI);
                                                }
                                            }));
                                }
                            }
                            else {
                                removeAugment(finalI);
                            }
                        });
                curY += viewer.getHeight();
                currentAugments.add(viewer);
            }
        }
        else {
            toggleAugment.setActive(false);
            toggleAugmentView(false);
        }
    }

    protected void initializeLabels() {
        PCLCardData cardData = currentItem != null ? currentItem.cardData : null;
        if (cardData != null) {
            String author = cardData.getAuthorString();
            changeVariant.setActive(cardData.canToggleFromPopup && (currentItem.auxiliaryData.form == 0 || cardData.canToggleFromAlternateForm) && GameUtilities.inGame());
            changeVariant.tooltip.setDescription(!cardData.canToggleFromAlternateForm ? PGR.core.strings.scp_changeVariantTooltipPermanent : PGR.core.strings.scp_changeVariantTooltipAlways);
            artAuthorLabel.setLabel(author != null ? PGR.core.strings.scp_artAuthor + COLON_SEPARATOR + EUIUtils.modifyString(author, w -> "#y" + w) : "");
        }
        else {
            changeVariant.setActive(false);
            changeVariant.tooltip.setDescription(PGR.core.strings.scp_changeVariantTooltipAlways);
            artAuthorLabel.setLabel("");
        }

        ModInfo info = EUIGameUtils.getModInfo(currentItem);
        whatModLabel.setLabel(info != null ? EUIRM.strings.ui_origins + COLON_SEPARATOR + EUIUtils.modifyString(info.Name, w -> "#y" + w) : "");
    }

    private void initializeToggles() {
        this.betaArtToggle.setActive(false);// (boolean)_canToggleBetaArt.Invoke(CardCrawlGame.cardPopup));
        this.upgradeToggle.setActive(SingleCardViewPopup.enableUpgradeToggle && currentItem.canUpgrade());

        if (betaArtToggle.isActive) {
            this.viewBetaArt = UnlockTracker.betaCardPref.getBoolean(currentItem.cardID, false);

            if (upgradeToggle.isActive) {
                this.betaArtToggle.hb.move(Settings.WIDTH / 2f + 270f * Settings.scale, 70f * Settings.scale);
                this.upgradeToggle.hb.move(Settings.WIDTH / 2f - 180f * Settings.scale, 70f * Settings.scale);
            }
            else {
                this.betaArtToggle.hb.move(Settings.WIDTH / 2f, 70f * Settings.scale);
            }
        }
        else {
            this.upgradeToggle.hb.move(Settings.WIDTH / 2f, 70f * Settings.scale);
        }
    }

    @Override
    protected boolean isHovered() {
        return this.toggleAugment.hb.hovered ||
                (showAugments && EUIUtils.any(currentAugments, augment -> augment.augmentButton.hb.hovered)) ||
                (this.upgradeToggle.hb.hovered) ||
                (this.betaArtToggle.hb.hovered) ||
                (this.scrollBar.hb.hovered) ||
                (this.changeVariant.hb.hovered) ||
                (this.changeVariantEditor.isHovered());
    }

    public void open(PCLCard card, CardGroup group) {
        CardCrawlGame.isPopupOpen = true;

        if (this.currentItem != null) {
            this.currentItem.unloadSingleCardView();
        }

        this.currentItem = card;
        this.displayCard = card.makePopupCopy();
        this.displayCard.loadSingleCardView();
        this.upgradedCard = null;
        this.group = group;
        this.currentForm = card.auxiliaryData.form;
        this.changeVariantEditor.setLimits(0, card.maxForms() - 1);
        super.openImpl(currentItem, group != null ? group.group : null);

        initializeToggles();
        initializeAugments();
    }

    public void openNext(AbstractCard card) {
        boolean tmp = SingleCardViewPopup.isViewingUpgrade;
        this.close();
        CardCrawlGame.cardPopup.open(card, this.group);
        SingleCardViewPopup.isViewingUpgrade = tmp;
        forceUnfade();
    }

    private void refreshAugments() {
        float curY = AUGMENT_Y;
        for (PCLAugmentViewer viewer : currentAugments) {
            viewer.refreshAugment();
            viewer.translate(AUGMENT_X, curY);
            curY += viewer.getHeight();
        }
    }

    private void removeAugment(int index) {
        PCLAugment augment = currentItem.removeAugment(index);
        if (augment != null) {
            this.displayCard = currentItem.makePopupCopy();
            this.upgradedCard = getUpgradeCard();
            PGR.dungeon.addAugment(augment.save);
            refreshAugments();
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        sb.setColor(this.fadeColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        sb.setColor(Color.WHITE);

        if (this.effect != null) {
            this.effect.render(sb);
            return;
        }

        PCLCard card = getCard();

        card.renderInLibrary(sb);

        super.renderImpl(sb);

        FontHelper.cardTitleFont.getData().setScale(1);
        if (upgradeToggle.isActive) {
            sb.draw(ImageMaster.CHECKBOX, upgradeToggle.hb.cX - 100.0F * Settings.scale - 32.0F, upgradeToggle.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
            upgradeToggle.renderImpl(sb);

            if (Settings.isControllerMode) {
                sb.draw(CInputActionSet.proceed.getKeyImg(), this.upgradeToggle.hb.cX - 132f * Settings.scale - 32f, -32f + 67f * Settings.scale, 32f, 32f, 64f, 64f, Settings.scale, Settings.scale, 0f, 0, 0, 64, 64, false, false);
            }
        }
        if (betaArtToggle.isActive) {
            sb.draw(ImageMaster.CHECKBOX, betaArtToggle.hb.cX - 100.0F * Settings.scale - 32.0F, betaArtToggle.hb.cY - 32.0F, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
            betaArtToggle.renderImpl(sb);

            if (Settings.isControllerMode) {
                sb.draw(CInputActionSet.topPanel.getKeyImg(), this.betaArtToggle.hb.cX - 132f * Settings.scale - 32f, -32f + 67f * Settings.scale, 32f, 32f, 64f, 64f, Settings.scale, Settings.scale, 0f, 0, 0, 64, 64, false, false);
            }
        }

        changeVariantEditor.tryRender(sb);
        changeVariant.tryRender(sb);

        if (AbstractDungeon.player != null || card.cardData != null) {
            maxUpgradesLabel.renderImpl(sb);
            maxCopiesLabel.renderImpl(sb);
        }

        this.toggleAugment.tryRender(sb);
    }

    @Override
    protected void renderTips(SpriteBatch sb) {
        if (showAugments) {
            for (PCLAugmentViewer augment : currentAugments) {
                augment.tryRender(sb);
            }
        }
        else {
            super.renderTips(sb);
        }
    }

    private void toggleAugmentView(boolean value) {
        showAugments = value;
        toggleAugment.setText(showAugments ? PGR.core.strings.scp_viewTooltips : PGR.core.strings.scp_viewAugments);
    }

    private void toggleBetaArt(boolean value) {
        this.viewBetaArt = value;
        UnlockTracker.betaCardPref.putBoolean(this.displayCard.cardID, this.viewBetaArt);
        UnlockTracker.betaCardPref.flush();
    }

    private void toggleUpgrade(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    @Override
    public void updateImpl() {
        if (this.effect != null) {
            this.effect.update();
            if (this.effect.isDone) {
                this.effect = null;
            }
            return;
        }
        super.updateImpl();

        this.upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade).tryUpdate();
        this.betaArtToggle.setToggle(viewBetaArt).tryUpdate();

        this.maxUpgradesLabel.setLabel(EUIUtils.format((AbstractDungeon.player != null ? PGR.core.strings.scp_currentUpgrades : PGR.core.strings.scp_maxUpgrades), getCardUpgradesText()));
        this.maxUpgradesLabel.tryUpdate();
        this.maxCopiesLabel.setLabel(EUIUtils.format((AbstractDungeon.player != null ? PGR.core.strings.scp_currentCopies : PGR.core.strings.scp_maxCopies), getCardCopiesText()));
        this.maxCopiesLabel.tryUpdate();
        this.toggleAugment.tryUpdate();

        if (currentItem != null) {
            this.changeVariantEditor.setValue(currentForm, false)
                    .setActive(currentItem.cardData != null && currentItem.maxForms() > 1 && currentItem.cardData.canToggleOnUpgrade && SingleCardViewPopup.isViewingUpgrade)
                    .tryUpdate();
            this.changeVariant.setInteractable(currentItem.auxiliaryData.form != currentForm).tryUpdate();
        }

        if (showAugments) {
            for (PCLAugmentViewer augment : currentAugments) {
                augment.tryUpdate();
            }
        }
        else {
            scrollBar.tryUpdate();
        }
    }

}
