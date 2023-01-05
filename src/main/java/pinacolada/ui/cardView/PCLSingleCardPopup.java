package pinacolada.ui.cardView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.evacipated.cardcrawl.mod.stslib.patches.FlavorText;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputAction;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.*;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class PCLSingleCardPopup extends EUIBase
{
    protected static final float AUGMENT_X = Settings.WIDTH * 0.77f;
    protected static final float AUGMENT_Y = Settings.HEIGHT * 0.8f;
    protected static final float ICON_SIZE = 64f * Settings.scale;
    protected static final String[] TEXT = SingleCardViewPopup.TEXT;

    private final ArrayList<PCLAugmentViewer> currentAugments = new ArrayList<>();
    private final EUIToggle upgradeToggle;
    private final EUIToggle betaArtToggle;
    private final EUIButton changeVariant;
    private final EUIButton changeVariantNext;
    private final EUIButton changeVariantPrev;
    private final EUIButton toggleAugment;
    private final EUITextBox changeVariantNumber;
    private final EUILabel changeVariantLabel;
    private final EUILabel changeVariantDescription;
    private final EUILabel maxCopiesLabel;
    private final EUILabel maxCopiesCount;
    private final EUILabel maxCopiesDescription;
    private final EUILabel artAuthorLabel;

    private final EUIHitbox nextHb;
    private final EUIHitbox prevHb;
    private final EUIHitbox cardHb;
    private final EUIHitbox authorHb;
    private final EUIHitbox upgradeHb;
    private final EUIHitbox betaArtHb;
    private final EUIHitbox changeVariantHb;
    private final EUIHitbox changeVariantNextHb;
    private final EUIHitbox changeVariantPrevHb;
    private final EUIHitbox changeVariantValueHb;
    private final PCLCoreStrings.SingleCardPopupButtons buttonStrings = PGR.core.strings.singleCardPopupButtons;
    private final Color fadeColor;
    private PCLCard baseCard;
    private PCLCard card;
    private PCLCard upgradedCard;
    private CardGroup group;
    private AbstractCard prevCard;
    private AbstractCard nextCard;
    private boolean showAugments = true;
    private boolean viewBetaArt;
    private boolean viewVariants;
    private boolean viewChangeVariants;
    private float fadeTimer;
    private int currentForm;
    private PCLAugmentSelectionEffect effect;

    public PCLSingleCardPopup()
    {
        this.fadeColor = Color.BLACK.cpy();
        this.upgradeHb = new EUIHitbox(250f * Settings.scale, 80f * Settings.scale);
        this.betaArtHb = new EUIHitbox(250f * Settings.scale, 80f * Settings.scale);
        this.prevHb = new EUIHitbox(160f * Settings.scale, 160f * Settings.scale);
        this.nextHb = new EUIHitbox(160f * Settings.scale, 160f * Settings.scale);
        this.cardHb = new EUIHitbox(550f * Settings.scale, 770f * Settings.scale);
        this.authorHb = new EUIHitbox(160f * Settings.scale, 110f * Settings.scale);
        this.changeVariantHb = new EUIHitbox(200f * Settings.scale, 150f * Settings.scale);
        this.changeVariantNextHb = new RelativeHitbox(changeVariantHb, ICON_SIZE, ICON_SIZE, changeVariantHb.width / 2 + ICON_SIZE * 3.5f, changeVariantHb.height * 0.8f);
        this.changeVariantPrevHb = new RelativeHitbox(changeVariantHb, ICON_SIZE, ICON_SIZE, changeVariantHb.width / 2 + ICON_SIZE * 1.5f, changeVariantHb.height * 0.8f);
        this.changeVariantValueHb = new RelativeHitbox(changeVariantHb, ICON_SIZE, ICON_SIZE, changeVariantHb.width / 2 + ICON_SIZE * 2.5f, changeVariantHb.height * 0.8f);
        this.viewBetaArt = false;
        this.isActive = false;
        this.currentForm = 0;

        this.upgradeToggle = new EUIToggle(upgradeHb).setText(TEXT[6])
                .setBackground(new EUIImage(ImageMaster.CHECKBOX, Color.WHITE))
                .setTickImage(null, new EUIImage(ImageMaster.TICK, Color.WHITE), 64)
                .setFontColors(Settings.GOLD_COLOR, Settings.BLUE_TEXT_COLOR)
                .setControllerAction(CInputActionSet.proceed)
                .setFont(FontHelper.cardTitleFont, 1)
                .setOnToggle(this::toggleUpgrade);

        this.betaArtToggle = new EUIToggle(betaArtHb).setText(TEXT[14])
                .setBackground(new EUIImage(ImageMaster.CHECKBOX, Color.WHITE))
                .setTickImage(null, new EUIImage(ImageMaster.TICK, Color.WHITE), 64)
                .setFontColors(Settings.GOLD_COLOR, Settings.BLUE_TEXT_COLOR)
                .setControllerAction(CInputActionSet.proceed)
                .setFont(FontHelper.cardTitleFont, 1)
                .setOnToggle(this::toggleBetaArt);

        this.changeVariant = new EUIButton(EUIRM.images.hexagonalButton.texture(), changeVariantHb)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.WHITE)
                .setClickDelay(0.3f)
                .setDimensions(screenW(0.18f), screenH(0.07f))
                .setText(buttonStrings.changeVariant)
                .setOnClick(this::changeCardForm)
                .setColor(Color.FIREBRICK);

        this.changeVariantNext = new EUIButton(ImageMaster.CF_RIGHT_ARROW, changeVariantNextHb)
                .setOnClick(() -> changePreviewForm(currentForm + 1))
                .setText(null);

        this.changeVariantPrev = new EUIButton(ImageMaster.CF_LEFT_ARROW, changeVariantPrevHb)
                .setOnClick(() -> changePreviewForm(currentForm - 1))
                .setText(null);

        this.toggleAugment = new EUIButton(EUIRM.images.hexagonalButton.texture(),
                new EUIHitbox(Settings.WIDTH * 0.85f, Settings.HEIGHT * 0.95f, scale(240), scale(50)))
                .setFont(EUIFontHelper.buttonFont, 0.9f)
                .setColor(Color.GRAY)
                .setBorder(EUIRM.images.hexagonalButtonBorder.texture(), Color.GRAY)
                .setOnClick(() -> toggleAugmentView(!showAugments))
                .setText(PGR.core.strings.singleCardPopupButtons.viewTooltips);

        this.changeVariantNumber = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), changeVariantValueHb)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);

        this.changeVariantLabel = new EUILabel(EUIFontHelper.carddescriptionfontLarge,
                new RelativeHitbox(changeVariantHb, ICON_SIZE, ICON_SIZE, changeVariantHb.width / 2 - ICON_SIZE * 2, changeVariantHb.height * 1.6f))
                .setAlignment(0.5f, 0.5f) // 0.1f
                .setLabel(buttonStrings.variant + ":");

        this.changeVariantDescription = new EUILabel(EUIFontHelper.cardTooltipFont,
                new RelativeHitbox(changeVariantHb, screenW(0.21f), screenH(0.07f), changeVariantHb.width / 2, -changeVariantHb.height * 0.6f))
                .setAlignment(0.9f, 0.1f, true)
                .setLabel(buttonStrings.changeVariantTooltipAlways);

        this.maxCopiesLabel = new EUILabel(EUIFontHelper.carddescriptionfontLarge,
                new RelativeHitbox(changeVariantHb, ICON_SIZE, ICON_SIZE, changeVariantHb.width / 2 - ICON_SIZE * 1.5f, changeVariantHb.height * 4.3f))
                .setAlignment(0.5f, 0.5f);

        this.maxCopiesCount = new EUILabel(EUIFontHelper.cardtitlefontLarge,
                new RelativeHitbox(changeVariantHb, ICON_SIZE, ICON_SIZE, changeVariantHb.width / 2 + ICON_SIZE * 1.5f, changeVariantHb.height * 4.3f))
                .setColor(new Color(0.7f, 0.9f, 1f, 1f))
                .setAlignment(0.5f, 0.5f);

        this.maxCopiesDescription = new EUILabel(EUIFontHelper.cardTooltipFont,
                new RelativeHitbox(changeVariantHb, screenW(0.21f), screenH(0.07f), changeVariantHb.width / 2, changeVariantHb.height * 3.4f))
                .setAlignment(0.9f, 0.1f, true)
                .setLabel(buttonStrings.maxCopiesTooltip);

        this.artAuthorLabel = new EUILabel(EUIFontHelper.cardTooltipFont,
                new RelativeHitbox(changeVariantHb, screenW(0.21f), screenH(0.07f), changeVariantHb.width / 2 - ICON_SIZE * 0.75f, changeVariantHb.height * 4.52f))
                .setAlignment(0.9f, 0.1f, true)
                .setLabel(buttonStrings.artAuthor);
    }

    private void applyAugment(PCLAugment augment)
    {
        PGR.core.dungeon.addAugment(augment.ID, -1);
        baseCard.addAugment(augment);
        this.card = baseCard.makePopupCopy();
        this.upgradedCard = getUpgradeCard();
        refreshAugments();
    }

    private void changeCardForm()
    {
        if (baseCard != null && baseCard.auxiliaryData.form != currentForm)
        {
            baseCard.changeForm(currentForm, baseCard.timesUpgraded);
            //aCard.canBranch = false;
        }
    }

    public void changePreviewForm(int newForm)
    {
        if (card != null && newForm >= 0 && newForm <= card.getMaxForms() - 1)
        {
            this.currentForm = card.changeForm(newForm, card.timesUpgraded);
            upgradedCard = card.makePopupCopy();
            upgradedCard.changeForm(newForm, card.timesUpgraded);
            upgradedCard.upgrade();
            upgradedCard.displayUpgrades();
        }

    }

    public void close()
    {
        if (AbstractDungeon.player != null)
        {
            SingleCardViewPopup.isViewingUpgrade = false;
        }

        InputHelper.justReleasedClickLeft = false;
        CardCrawlGame.isPopupOpen = false;
        this.isActive = false;
        this.baseCard = null;
        this.card = null;
        this.upgradedCard = null;
        this.currentForm = 0;
    }

    public PCLCard getCard()
    {
        if (SingleCardViewPopup.isViewingUpgrade)
        {
            if (upgradedCard == null)
            {
                upgradedCard = getUpgradeCard();
            }

            return upgradedCard;
        }
        else
        {
            return card;
        }
    }

    private String getCardCopiesText()
    {
        if (card == null)
        {
            return "";
        }
        int currentCopies = (AbstractDungeon.player != null ? EUIUtils.count(AbstractDungeon.player.masterDeck.group, c -> c.cardID.equals(card.cardID)) : -1);
        int maxCopies = card.cardData != null ? card.cardData.maxCopies : 0;

        if (currentCopies >= 0 && maxCopies > 0)
        {
            return currentCopies + "/" + maxCopies;
        }
        else if (currentCopies >= 0)
        {
            return String.valueOf(currentCopies);
        }
        else
        {
            return String.valueOf(maxCopies);
        }
    }

    private PCLCard getUpgradeCard()
    {
        upgradedCard = card.makePopupCopy();
        upgradedCard.upgrade();
        upgradedCard.displayUpgrades();
        return upgradedCard;
    }

    public void open(PCLCard card, CardGroup group)
    {
        CardCrawlGame.isPopupOpen = true;

        this.baseCard = card;
        this.card = card.makePopupCopy();
        this.upgradedCard = null;
        this.isActive = true;
        this.prevCard = null;
        this.nextCard = null;
        this.group = group;
        this.currentForm = card.auxiliaryData.form;

        if (group != null)
        {
            for (int i = 0; i < group.size(); ++i)
            {
                if (group.group.get(i) == card)
                {
                    if (i != 0)
                    {
                        this.prevCard = group.group.get(i - 1);
                    }

                    if (i != group.size() - 1)
                    {
                        this.nextCard = group.group.get(i + 1);
                    }
                    break;
                }
            }

            this.prevHb.move((float) Settings.WIDTH / 2f - 400f * Settings.scale, (float) Settings.HEIGHT / 2f);
            this.nextHb.move((float) Settings.WIDTH / 2f + 400f * Settings.scale, (float) Settings.HEIGHT / 2f);
        }

        this.cardHb.move((float) Settings.WIDTH / 2f, (float) Settings.HEIGHT / 2f);

        this.fadeTimer = 0.25f;
        this.fadeColor.a = 0f;

        this.betaArtToggle.setActive(false);// (boolean)_canToggleBetaArt.Invoke(CardCrawlGame.cardPopup));
        this.upgradeToggle.setActive(SingleCardViewPopup.enableUpgradeToggle && card.canUpgrade());

        if (betaArtToggle.isActive)
        {
            this.viewBetaArt = UnlockTracker.betaCardPref.getBoolean(card.cardID, false);

            if (upgradeToggle.isActive)
            {
                this.betaArtHb.move((float) Settings.WIDTH / 2f + 270f * Settings.scale, 70f * Settings.scale);
                this.upgradeHb.move((float) Settings.WIDTH / 2f - 180f * Settings.scale, 70f * Settings.scale);
            }
            else
            {
                this.betaArtHb.move((float) Settings.WIDTH / 2f, 70f * Settings.scale);
            }
        }
        else
        {
            this.upgradeHb.move((float) Settings.WIDTH / 2f, 70f * Settings.scale);
        }

        this.changeVariantHb.move((float) Settings.WIDTH / 2f - 700f * Settings.scale, Settings.HEIGHT / 2f + 170 * Settings.scale);


        PCLCardData cardData = baseCard != null ? baseCard.cardData : null;
        if (cardData != null)
        {
            String author = FlavorText.CardStringsFlavorField.flavor.get(cardData.strings);
            viewChangeVariants = cardData.canToggleFromPopup && (baseCard.upgraded || cardData.unUpgradedCanToggleForms) && (baseCard.auxiliaryData.form == 0 || cardData.canToggleFromAlternateForm) && GameUtilities.inGame();
            changeVariantDescription.setLabel(!cardData.canToggleFromAlternateForm ? buttonStrings.changeVariantTooltipPermanent : buttonStrings.changeVariantTooltipAlways);
            artAuthorLabel.setLabel(author != null ? buttonStrings.artAuthor + EUIUtils.modifyString(author, w -> "#y" + w) : "");
        }
        else
        {
            viewChangeVariants = false;
            changeVariantDescription.setLabel(buttonStrings.changeVariantTooltipAlways);
            artAuthorLabel.setLabel("");
        }

        currentAugments.clear();
        // Do not show augments for cards not in your deck
        if (AbstractDungeon.player != null && AbstractDungeon.player.masterDeck.contains(baseCard))
        {
            toggleAugment.setActive(true);
            float curY = AUGMENT_Y;
            for (int i = 0; i < baseCard.augments.size(); i++)
            {
                int finalI = i;
                PCLAugmentViewer viewer = new PCLAugmentViewer(new EUIHitbox(AUGMENT_X, curY, scale(300), scale(360)), baseCard, i)
                        .setOnClick(() -> {
                            if (baseCard.augments.get(finalI) == null)
                            {
                                this.effect = (PCLAugmentSelectionEffect) new PCLAugmentSelectionEffect(baseCard)
                                        .addCallback((augment -> {
                                            if (augment != null)
                                            {
                                                applyAugment(augment);
                                            }
                                        }));
                            }
                            else
                            {
                                removeAugment(finalI);
                            }
                        });
                curY += viewer.getHeight();
                currentAugments.add(viewer);
            }
        }
        else
        {
            toggleAugment.setActive(false);
            toggleAugmentView(false);
        }


    }

    private void openNext(AbstractCard card)
    {
        boolean tmp = SingleCardViewPopup.isViewingUpgrade;
        this.close();
        CardCrawlGame.cardPopup.open(card, this.group);
        SingleCardViewPopup.isViewingUpgrade = tmp;
        this.fadeTimer = 0f;
        this.fadeColor.a = 0.9f;
    }

    private void refreshAugments()
    {
        float curY = AUGMENT_Y;
        for (PCLAugmentViewer viewer : currentAugments)
        {
            viewer.refreshAugment();
            viewer.translate(AUGMENT_X, curY);
            curY += viewer.getHeight();
        }
    }

    private void removeAugment(int index)
    {
        PCLAugment augment = baseCard.removeAugment(index);
        if (augment != null)
        {
            this.card = baseCard.makePopupCopy();
            this.upgradedCard = getUpgradeCard();
            PGR.core.dungeon.addAugment(augment.ID, 1);
            refreshAugments();
        }
    }

    private void renderArrow(SpriteBatch sb, Hitbox hb, CInputAction action, boolean flipX)
    {
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.POPUP_ARROW, hb.cX - 128f, hb.cY - 128f, 128f, 128f, 256f, 256f, Settings.scale, Settings.scale, 0f, 0, 0, 256, 256, flipX, false);
        if (Settings.isControllerMode)
        {
            sb.draw(action.getKeyImg(), hb.cX - 32f, hb.cY - 32f + 100f * Settings.scale, 32f, 32f, 64f, 64f, Settings.scale, Settings.scale, 0f, 0, 0, 64, 64, false, false);
        }

        if (hb.hovered)
        {
            sb.setBlendFunction(770, 1);
            sb.setColor(new Color(1f, 1f, 1f, 0.5f));
            sb.draw(ImageMaster.POPUP_ARROW, hb.cX - 128f, hb.cY - 128f, 128f, 128f, 256f, 256f, Settings.scale, Settings.scale, 0f, 0, 0, 256, 256, flipX, false);
            sb.setColor(Color.WHITE);
            sb.setBlendFunction(770, 771);
        }

        hb.render(sb);
    }

    private void toggleAugmentView(boolean value)
    {
        showAugments = value;
        toggleAugment.setText(showAugments ? PGR.core.strings.singleCardPopupButtons.viewTooltips : PGR.core.strings.singleCardPopupButtons.viewAugments);
    }

    private void toggleBetaArt(boolean value)
    {
        this.viewBetaArt = value;
        UnlockTracker.betaCardPref.putBoolean(this.card.cardID, this.viewBetaArt);
        UnlockTracker.betaCardPref.flush();
    }

    private void toggleUpgrade(boolean value)
    {
        SingleCardViewPopup.isViewingUpgrade = value;
    }

    @Override
    public void updateImpl()
    {
        if (this.effect != null)
        {
            this.effect.update();
            if (this.effect.isDone)
            {
                this.effect = null;
            }
            return;
        }
        this.cardHb.update();
        this.changeVariantHb.update();
        this.changeVariantNextHb.update();
        this.changeVariantPrevHb.update();
        this.changeVariantValueHb.update();
        this.authorHb.update();
        this.updateArrows();
        this.updateInput();

        this.fadeTimer = Math.max(0, fadeTimer - Gdx.graphics.getDeltaTime());
        this.fadeColor.a = Interpolation.pow2In.apply(0.9f, 0f, this.fadeTimer * 4f);

        this.upgradeToggle.setToggle(SingleCardViewPopup.isViewingUpgrade).tryUpdate();
        this.betaArtToggle.setToggle(viewBetaArt).tryUpdate();

        this.changeVariantNumber.setLabel(currentForm);
        this.changeVariantNumber.tryUpdate();
        this.changeVariantPrev.tryUpdate();
        this.changeVariantNext.tryUpdate();
        this.changeVariant.tryUpdate();
        this.changeVariantLabel.tryUpdate();
        this.changeVariantDescription.tryUpdate();
        this.maxCopiesLabel.setLabel((AbstractDungeon.player != null ? buttonStrings.currentCopies : buttonStrings.maxCopies) + ":");
        this.maxCopiesLabel.tryUpdate();
        this.maxCopiesCount.setLabel(getCardCopiesText());
        this.maxCopiesCount.tryUpdate();
        this.maxCopiesDescription.tryUpdate();
        this.artAuthorLabel.tryUpdate();
        this.toggleAugment.tryUpdate();

        this.viewVariants = viewChangeVariants || baseCard != null && baseCard.cardData != null && (SingleCardViewPopup.isViewingUpgrade || baseCard.cardData.unUpgradedCanToggleForms) && baseCard.getMaxForms() > 1;

        if (showAugments)
        {
            for (PCLAugmentViewer augment : currentAugments)
            {
                augment.tryUpdate();
            }
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        sb.setColor(this.fadeColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0f, 0f, (float) Settings.WIDTH, (float) Settings.HEIGHT);
        sb.setColor(Color.WHITE);

        if (this.effect != null)
        {
            this.effect.render(sb);
            return;
        }

        PCLCard card = getCard();

        card.renderInLibrary(sb);

        if (showAugments)
        {
            for (PCLAugmentViewer augment : currentAugments)
            {
                augment.tryRender(sb);
            }
        }
        else
        {
            card.renderCardTip(sb);
        }

        if (this.prevCard != null)
        {
            renderArrow(sb, prevHb, CInputActionSet.pageLeftViewDeck, false);
        }

        if (this.nextCard != null)
        {
            renderArrow(sb, nextHb, CInputActionSet.pageRightViewExhaust, true);
        }

        this.cardHb.render(sb);

        FontHelper.cardTitleFont.getData().setScale(1);
        if (upgradeToggle.isActive)
        {
            upgradeToggle.renderImpl(sb);

            if (Settings.isControllerMode)
            {
                sb.draw(CInputActionSet.proceed.getKeyImg(), this.upgradeHb.cX - 132f * Settings.scale - 32f, -32f + 67f * Settings.scale, 32f, 32f, 64f, 64f, Settings.scale, Settings.scale, 0f, 0, 0, 64, 64, false, false);
            }
        }
        if (betaArtToggle.isActive)
        {
            betaArtToggle.renderImpl(sb);

            if (Settings.isControllerMode)
            {
                sb.draw(CInputActionSet.topPanel.getKeyImg(), this.betaArtHb.cX - 132f * Settings.scale - 32f, -32f + 67f * Settings.scale, 32f, 32f, 64f, 64f, Settings.scale, Settings.scale, 0f, 0, 0, 64, 64, false, false);
            }
        }

        if (viewVariants)
        {
            changeVariant.setInteractable(baseCard.auxiliaryData.form != currentForm);
            changeVariantHb.render(sb);
            changeVariantValueHb.render(sb);
            changeVariantNumber.renderImpl(sb);
            changeVariantLabel.renderImpl(sb);
            if (currentForm > 0)
            {
                changeVariantPrevHb.render(sb);
                changeVariantPrev.renderImpl(sb);
            }
            if (currentForm < card.getMaxForms() - 1)
            {
                changeVariantNextHb.render(sb);
                changeVariantNext.renderImpl(sb);
            }
            if (viewChangeVariants)
            {
                changeVariant.renderImpl(sb);
                changeVariantDescription.renderImpl(sb);
            }
        }

        if (AbstractDungeon.player != null || card.cardData != null)
        {
            maxCopiesLabel.renderImpl(sb);
            maxCopiesCount.renderImpl(sb);
            maxCopiesDescription.renderImpl(sb);
        }

        authorHb.render(sb);
        artAuthorLabel.renderImpl(sb);
        this.toggleAugment.tryRender(sb);
    }

    private void updateArrows()
    {
        if (this.prevCard != null)
        {
            this.prevHb.update();
            if (this.prevHb.justHovered)
            {
                CardCrawlGame.sound.play("UI_HOVER");
            }

            if (this.prevHb.clicked || this.prevCard != null && CInputActionSet.pageLeftViewDeck.isJustPressed())
            {
                this.prevHb.clicked = false;
                CInputActionSet.pageLeftViewDeck.unpress();
                openNext(prevCard);
            }
        }

        if (this.nextCard != null)
        {
            this.nextHb.update();
            if (this.nextHb.justHovered)
            {
                CardCrawlGame.sound.play("UI_HOVER");
            }

            if (this.nextHb.clicked || this.nextCard != null && CInputActionSet.pageRightViewExhaust.isJustPressed())
            {
                this.nextHb.clicked = false;
                CInputActionSet.pageRightViewExhaust.unpress();
                openNext(nextCard);
            }
        }
    }

    private void updateInput()
    {
        if (InputHelper.justClickedLeft)
        {
            if (this.prevCard != null && this.prevHb.hovered)
            {
                this.prevHb.clickStarted = true;
                CardCrawlGame.sound.play("UI_CLICK_1");
                return;
            }

            if (this.nextCard != null && this.nextHb.hovered)
            {
                this.nextHb.clickStarted = true;
                CardCrawlGame.sound.play("UI_CLICK_1");
                return;
            }
        }

        if (InputHelper.justClickedLeft)
        {
            if (!this.cardHb.hovered && !this.upgradeHb.hovered &&
                    !this.toggleAugment.hb.hovered &&
                    (!showAugments || EUIUtils.all(currentAugments, augment -> !augment.augmentButton.hb.hovered)) &&
                    (this.betaArtHb == null || !this.betaArtHb.hovered) &&
                    (!this.viewVariants || (!this.changeVariantHb.hovered && !this.changeVariantNextHb.hovered && !this.changeVariantPrevHb.hovered && !this.changeVariantValueHb.hovered)))
            {
                close();
                InputHelper.justClickedLeft = false;
            }
        }
        else if (InputHelper.pressedEscape || CInputActionSet.cancel.isJustPressed())
        {
            CInputActionSet.cancel.unpress();
            InputHelper.pressedEscape = false;
            close();
        }

        if (this.prevCard != null && InputActionSet.left.isJustPressed())
        {
            openNext(prevCard);
        }
        else if (this.nextCard != null && InputActionSet.right.isJustPressed())
        {
            openNext(nextCard);
        }
    }

}
