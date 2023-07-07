package pinacolada.ui.cardView;

import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
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
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIClassUtils;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.effects.screen.ApplyAugmentToCardEffect;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

import static pinacolada.skills.PSkill.COLON_SEPARATOR;

public class PCLSingleCardPopup extends PCLSingleItemPopup<AbstractCard, PCLCard> {
    protected static final float AUGMENT_X = Settings.WIDTH * 0.77f;
    protected static final float AUGMENT_Y = Settings.HEIGHT * 0.8f;
    protected static final float ICON_SIZE = 64f * Settings.scale;
    protected static final String[] TEXT = SingleCardViewPopup.TEXT;

    protected final ArrayList<PCLAugmentViewer> currentAugments = new ArrayList<>();
    protected final EUIToggle upgradeToggle;
    protected final EUIToggle betaArtToggle;
    protected final EUIButton changeVariant;
    protected final EUIButton changeVariantNext;
    protected final EUIButton changeVariantPrev;
    protected final EUIButton toggleAugment;
    protected final EUITextBox changeVariantNumber;
    protected final EUILabel changeVariantLabel;
    protected final EUILabel changeVariantDescription;
    protected final EUILabel maxCopiesLabel;
    protected final EUILabel maxCopiesDescription;
    private PCLCard upgradedCard;
    private CardGroup group;
    private ApplyAugmentToCardEffect effect;
    protected boolean showAugments;
    protected boolean viewBetaArt;
    protected boolean viewVariants;
    protected boolean viewChangeVariants;
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

        this.changeVariant = new EUIButton(EUIRM.images.hexagonalButton.texture(), new EUIHitbox(200f * Settings.scale, 150f * Settings.scale))
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setClickDelay(0.3f)
                .setDimensions(screenW(0.18f), screenH(0.07f))
                .setText(PGR.core.strings.scp_changeVariant)
                .setOnClick(this::changeCardForm)
                .setColor(Color.FIREBRICK);
        this.changeVariant.hb.move(Settings.WIDTH / 2f - 700f * Settings.scale, Settings.HEIGHT / 2f + 170 * Settings.scale);

        this.changeVariantNext = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(changeVariant.hb, ICON_SIZE, ICON_SIZE, changeVariant.hb.width / 2 + ICON_SIZE * 3.5f, changeVariant.hb.height * 0.8f))
                .setOnClick(() -> changePreviewForm(currentForm + 1));

        this.changeVariantPrev = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(changeVariant.hb, ICON_SIZE, ICON_SIZE, changeVariant.hb.width / 2 + ICON_SIZE * 1.5f, changeVariant.hb.height * 0.8f))
                .setOnClick(() -> changePreviewForm(currentForm - 1));

        this.toggleAugment = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(Settings.WIDTH * 0.85f, Settings.HEIGHT * 0.95f, scale(240), scale(50)))
                .setLabel(EUIFontHelper.buttonFont, 0.9f, PGR.core.strings.scp_viewTooltips)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> toggleAugmentView(!showAugments));

        this.changeVariantNumber = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new RelativeHitbox(changeVariant.hb, ICON_SIZE, ICON_SIZE, changeVariant.hb.width / 2 + ICON_SIZE * 2.5f, changeVariant.hb.height * 0.8f))
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        this.changeVariantLabel = new EUILabel(EUIFontHelper.cardDescriptionFontLarge,
                new RelativeHitbox(changeVariant.hb, ICON_SIZE, ICON_SIZE, changeVariant.hb.width / 2 - ICON_SIZE * 2, changeVariant.hb.height * 1.6f))
                .setAlignment(0.5f, 0.5f) // 0.1f
                .setLabel(PGR.core.strings.scp_variant + ":");

        final float offX = changeVariant.hb.width / 2 - ICON_SIZE * 0.75f;

        this.changeVariantDescription = new EUILabel(EUIFontHelper.cardTooltipFont,
                new RelativeHitbox(changeVariant.hb, screenW(0.21f), screenH(0.07f), changeVariant.hb.width / 2, -changeVariant.hb.height * 0.6f))
                .setAlignment(0.9f, 0.1f, true)
                .setLabel(PGR.core.strings.scp_changeVariantTooltipAlways);

        this.maxCopiesLabel = new EUILabel(EUIFontHelper.cardTooltipTitleFontNormal,
                new RelativeHitbox(changeVariant.hb, screenW(0.21f), screenH(0.07f), offX, changeVariant.hb.height * 3.7f))
                .setAlignment(0.9f, 0.1f, true);

        this.maxCopiesDescription = new EUILabel(EUIFontHelper.cardTooltipFont,
                new RelativeHitbox(changeVariant.hb, screenW(0.21f), screenH(0.07f), offX, changeVariant.hb.height * 3.1f))
                .setAlignment(0.9f, 0.1f, true)
                .setLabel(PGR.core.strings.cetut_maxCopies);
    }

    private void applyAugment(PCLAugment augment) {
        PGR.dungeon.addAugment(augment.ID, -1);
        currentItem.addAugment(augment);
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
        if (displayCard != null && newForm >= 0 && newForm <= displayCard.getMaxForms() - 1) {
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
        int maxCopies = displayCard.cardData != null ? displayCard.cardData.maxCopies : 0;

        if (currentCopies >= 0 && maxCopies > 0) {
            return currentCopies + "/" + maxCopies;
        }
        else if (currentCopies >= 0) {
            return String.valueOf(currentCopies);
        }
        else {
            return String.valueOf(maxCopies);
        }
    }

    private PCLCard getUpgradeCard() {
        upgradedCard = displayCard.makePopupCopy();
        upgradedCard.upgrade();
        upgradedCard.displayUpgrades();
        return upgradedCard;
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
        super.openImpl(currentItem, group != null ? group.group : null);

        initializeToggles();
        initializeAugments();
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

    protected void initializeLabels() {
        PCLCardData cardData = currentItem != null ? currentItem.cardData : null;
        if (cardData != null) {
            String author = cardData.getAuthorString();
            viewChangeVariants = cardData.canToggleFromPopup && (currentItem.auxiliaryData.form == 0 || cardData.canToggleFromAlternateForm) && GameUtilities.inGame();
            changeVariantDescription.setLabel(!cardData.canToggleFromAlternateForm ? PGR.core.strings.scp_changeVariantTooltipPermanent : PGR.core.strings.scp_changeVariantTooltipAlways);
            artAuthorLabel.setLabel(author != null ? PGR.core.strings.scp_artAuthor + COLON_SEPARATOR + EUIUtils.modifyString(author, w -> "#y" + w) : "");
        }
        else {
            viewChangeVariants = false;
            changeVariantDescription.setLabel(PGR.core.strings.scp_changeVariantTooltipAlways);
            artAuthorLabel.setLabel("");
        }

        ModInfo info = EUIGameUtils.getModInfo(currentItem);
        whatModLabel.setLabel(info != null ? EUIRM.strings.ui_origins + COLON_SEPARATOR + EUIUtils.modifyString(info.Name, w -> "#y" + w) : "");
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
                                if (PGR.dungeon.augments.size() > 0) {
                                    this.effect = (ApplyAugmentToCardEffect) new ApplyAugmentToCardEffect(currentItem)
                                            .addCallback((augment -> {
                                                if (augment != null) {
                                                    applyAugment(augment);
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
            PGR.dungeon.addAugment(augment.ID, 1);
            refreshAugments();
        }
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

        if (viewVariants) {
            changeVariant.setInteractable(currentItem.auxiliaryData.form != currentForm);
            changeVariantNumber.renderImpl(sb);
            changeVariantLabel.renderImpl(sb);
            if (currentForm > 0) {
                changeVariantPrev.renderImpl(sb);
            }
            if (currentForm < card.getMaxForms() - 1) {
                changeVariantNext.renderImpl(sb);
            }
            if (viewChangeVariants) {
                changeVariant.renderImpl(sb);
                changeVariantDescription.renderImpl(sb);
            }
        }

        if (AbstractDungeon.player != null || card.cardData != null) {
            maxCopiesLabel.renderImpl(sb);
            maxCopiesDescription.renderImpl(sb);
        }

        this.toggleAugment.tryRender(sb);
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

        this.changeVariantNumber.setLabel(currentForm);
        this.changeVariantNumber.tryUpdate();
        this.changeVariantPrev.tryUpdate();
        this.changeVariantNext.tryUpdate();
        this.changeVariant.tryUpdate();
        this.changeVariantLabel.tryUpdate();
        this.changeVariantDescription.tryUpdate();
        this.maxCopiesLabel.setLabel((AbstractDungeon.player != null ? PGR.core.strings.scp_currentCopies : PGR.core.strings.scp_maxCopies) + COLON_SEPARATOR + PCLCoreStrings.colorString("b", getCardCopiesText()));
        this.maxCopiesLabel.tryUpdate();
        this.maxCopiesDescription.tryUpdate();
        this.toggleAugment.tryUpdate();

        this.viewVariants = viewChangeVariants || currentItem != null && currentItem.cardData != null && currentItem.getMaxForms() > 1;

        if (showAugments) {
            for (PCLAugmentViewer augment : currentAugments) {
                augment.tryUpdate();
            }
        }
        else {
            scrollBar.tryUpdate();
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
    protected Iterable<? extends EUITooltip> getTipsForRender(PCLCard currentItem) {
        return currentItem != null ? ((TooltipProvider) currentItem).getTipsForRender() : new ArrayList<>();
    }

    @Override
    protected String getCredits(PCLCard currentItem) {
        if (currentItem != null) {
            return ((PCLCard) currentItem).cardData.getAuthorString();
        }
        return null;
    }

    @Override
    protected boolean isHovered() {
        return this.toggleAugment.hb.hovered ||
                (showAugments && EUIUtils.any(currentAugments, augment -> augment.augmentButton.hb.hovered)) ||
                (this.upgradeToggle.hb.hovered) ||
                (this.betaArtToggle.hb.hovered) ||
                (this.scrollBar.hb.hovered) ||
                (this.viewVariants && (this.changeVariant.hb.hovered || this.changeVariantNext.hb.hovered || this.changeVariantPrev.hb.hovered || this.changeVariantNumber.hb.hovered));
    }

}
