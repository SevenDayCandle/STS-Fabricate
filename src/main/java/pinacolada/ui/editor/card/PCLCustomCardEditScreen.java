package pinacolada.ui.editor.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.effects.screen.PCLCustomImageEffect;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomEffectPage;
import pinacolada.ui.editor.PCLCustomFormEditor;
import pinacolada.ui.editor.PCLCustomGenericPage;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_HEIGHT;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_WIDTH;

public class PCLCustomCardEditScreen extends PCLCustomEditEntityScreen<PCLCustomCardSlot, PCLDynamicCardData, PCLDynamicCard> {
    protected PCLDynamicCard previewCard;
    protected Texture loadedImage;

    public PCLCustomCardEditScreen(PCLCustomCardSlot slot) {
        this(slot, false);
    }

    public PCLCustomCardEditScreen(PCLCustomCardSlot slot, boolean fromInGame) {
        super(slot);
    }

    @Override
    protected void addSkillPages() {
        if (!fromInGame) {
            primaryPages.add(new PCLCustomCardPrimaryInfoPage(this));
        }
        primaryPages.add(new PCLCustomCardAttributesPage(this));
        super.addSkillPages();
    }

    @Override
    protected PCLCustomEffectPage createPageForEffect(PSkill<?> eff) {
        if (eff instanceof PCardPrimary_DealDamage) {
            return new PCLCustomAttackEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), eff
                    , PGR.core.strings.cedit_damage);
        }
        else if (eff instanceof PCardPrimary_GainBlock) {
            return new PCLCustomBlockEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), eff
                    , PGR.core.strings.cedit_block);
        }
        else {
            return new PCLCustomEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), eff
                    , PGR.core.strings.cedit_effectX);
        }
    }

    @Override
    protected void complete() {
        super.complete();
        invalidateItems();
        if (loadedImage != null) {
            loadedImage.dispose();
        }
    }

    protected void editImage() {
        Texture image = loadedImage;
        if (image == null) {
            ColoredTexture portrait = getBuilder().portraitImage;
            if (portrait != null) {
                image = portrait.texture;
            }
        }
        currentDialog = PCLCustomImageEffect.forCard(image)
                .addCallback(pixmap -> {
                            if (pixmap != null) {
                                setLoadedImage(new Texture(pixmap));
                            }
                        }
                );
    }

    @Override
    protected NewPageOption[] getNewPageOptions() {
        // Only allow one of each of damage and block
        boolean hasDamage = false;
        boolean hasBlock = false;
        for (PCLCustomEffectPage pg : effectPages) {
            if (pg instanceof PCLCustomAttackEffectPage) {
                hasDamage = true;
            }
            else if (pg instanceof PCLCustomBlockEffectPage) {
                hasBlock = true;
            }
        }
        return hasDamage && hasBlock ? super.getNewPageOptions() :
                hasDamage ? EUIUtils.array(NewPageOption.Generic, NewPageOption.Power, NewPageOption.Block) :
                hasBlock ? EUIUtils.array(NewPageOption.Generic, NewPageOption.Power, NewPageOption.Damage) :
                EUIUtils.array(NewPageOption.Generic, NewPageOption.Power, NewPageOption.Damage, NewPageOption.Block);
    }

    @Override
    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), page instanceof PCLCustomCardPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : "");
    }

    @Override
    protected PCLCustomEffectPage makeNewBlockEffect() {
        return makeEffectPage(new PCardPrimary_GainBlock());
    }

    @Override
    protected PCLCustomEffectPage makeNewDamageEffect() {
        return makeEffectPage(new PCardPrimary_DealDamage());
    }

    @Override
    public void preInitialize(PCLCustomCardSlot slot) {
        super.preInitialize(slot);
        imageButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(saveButton.hb.cX, undoButton.hb.y + undoButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.WHITE)
                .setTooltip(PGR.core.strings.cedit_loadImage, PGR.core.strings.cetut_primaryImage)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, PGR.core.strings.cedit_loadImage)
                .setOnClick(this::editImage);

        formEditor = new PCLCustomFormEditor(
                new EUIHitbox(Settings.WIDTH * 0.04f, imageButton.hb.y + imageButton.hb.height + LABEL_HEIGHT * 3.2f, Settings.scale * 90f, Settings.scale * 48f), this);

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(Settings.WIDTH * 0.105f, CARD_Y - LABEL_HEIGHT - AbstractCard.IMG_HEIGHT / 2f)
                .setBackground(EUIRM.images.greySquare.texture(), Color.DARK_GRAY)
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setToggle(SingleCardViewPopup.isViewingUpgrade)
                .setOnToggle(this::toggleViewUpgrades);

        upgradeToggle.setActive(slot.maxUpgradeLevel != 0);
    }

    protected void rebuildItem() {
        previewCard = getBuilder().createImplWithForms(currentBuilder, upgraded ? 1 : 0);
        previewCard.setForms(getTempBuilders());

        if (upgraded) {
            previewCard.displayUpgrades();
        }
        else {
            previewCard.displayUpgradesForSkills(false);
        }

        previewCard.drawScale = previewCard.targetDrawScale = 1f;
        previewCard.current_x = previewCard.target_x = CARD_X;
        previewCard.current_y = previewCard.target_y = CARD_Y;
    }

    public void renderInnerElements(SpriteBatch sb) {
        super.renderInnerElements(sb);
        imageButton.tryRender(sb);
        formEditor.tryRender(sb);
        upgradeToggle.tryRender(sb);
        previewCard.render(sb);
    }

    public void setLoadedImage(Texture texture) {
        loadedImage = texture;
        modifyAllBuilders((e, i) -> e
                .setImagePath(currentSlot.getImagePath())
                .setImage(new ColoredTexture(texture)));
    }

    protected void toggleViewUpgrades(boolean value) {
        super.toggleViewUpgrades(value);
        if (upgraded) {
            previewCard.changeForm(previewCard.getForm(), 0, 1);
            previewCard.displayUpgrades();
        }
        else {
            previewCard.changeForm(previewCard.getForm(), 1, 0);
            previewCard.displayUpgradesForSkills(false);
            previewCard.initializeDescription();
        }
    }

    public void updateInnerElements() {
        super.updateInnerElements();
        imageButton.tryUpdate();
        formEditor.tryUpdate();
        upgradeToggle.tryUpdate();
        previewCard.update();
        previewCard.hb.update();
        if (previewCard.hb.hovered) {
            EUITooltip.queueTooltips(previewCard);
        }
    }

    protected void updateVariant() {
        formEditor.refresh();
    }

}
