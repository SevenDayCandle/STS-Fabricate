package pinacolada.ui.editor.card;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCard;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.effects.screen.PCLCustomImageEffect;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.special.primary.PCardPrimary_DealDamage;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomFormEditor;
import pinacolada.ui.editor.PCLCustomGenericPage;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;
import static pinacolada.ui.editor.PCLCustomEffectEditingPane.invalidateItems;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_HEIGHT;
import static pinacolada.ui.editor.PCLCustomEffectPage.MENU_WIDTH;

public class PCLCustomCardEditCardScreen extends PCLCustomEditEntityScreen<PCLCustomCardSlot, PCLDynamicCardData> {

    protected EUIToggle upgradeToggle;
    protected PCardPrimary_DealDamage currentDamage;
    protected PCardPrimary_GainBlock currentBlock;
    protected PCLDynamicCard previewCard;
    protected PCLCustomFormEditor formEditor;
    protected EUIButton imageButton;
    protected Texture loadedImage;

    public PCLCustomCardEditCardScreen(PCLCustomCardSlot slot) {
        this(slot, false);
    }

    public PCLCustomCardEditCardScreen(PCLCustomCardSlot slot, boolean fromInGame) {
        super(slot);
    }

    protected void addSkillPages() {
        if (!fromInGame) {
            pages.add(new PCLCustomCardPrimaryInfoPage(this));
        }
        pages.add(new PCLCustomCardAttributesPage(this));
        pages.add(new PCLCustomAttackEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), 0, PGR.core.strings.cedit_damage, be -> {
            currentDamage = EUIUtils.safeCast(be, PCardPrimary_DealDamage.class);
            modifyBuilder(e -> e.setAttackSkill(currentDamage));
        }));
        pages.add(new PCLCustomBlockEffectPage(this, new EUIHitbox(START_X, START_Y, MENU_WIDTH, MENU_HEIGHT), 0, PGR.core.strings.cedit_block, be -> {
            currentBlock = EUIUtils.safeCast(be, PCardPrimary_GainBlock.class);
            modifyBuilder(e -> e.setBlockSkill(currentBlock));
        }));
        super.addSkillPages();
    }

    protected void clearPages() {
        super.clearPages();

        currentDamage = getBuilder().attackSkill;
        currentBlock = getBuilder().blockSkill;
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), page instanceof PCLCustomCardPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : "");
    }

    public void preInitialize(PCLCustomCardSlot slot) {
        super.preInitialize(slot);
        imageButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, undoButton.hb.y + undoButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.WHITE)
                .setTooltip(PGR.core.strings.cedit_loadImage, PGR.core.strings.cetut_primaryImage)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, PGR.core.strings.cedit_loadImage)
                .setOnClick(this::editImage);

        formEditor = new PCLCustomFormEditor(
                new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 48f)
                        .setCenter(Settings.WIDTH * 0.116f, imageButton.hb.y + imageButton.hb.height + LABEL_HEIGHT * 3.2f), this);

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(Settings.WIDTH * 0.116f, CARD_Y - LABEL_HEIGHT - AbstractCard.IMG_HEIGHT / 2f)
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setFont(EUIFontHelper.cardDescriptionFontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setToggle(SingleCardViewPopup.isViewingUpgrade)
                .setOnToggle(this::toggleViewUpgrades);

        upgradeToggle.setActive(slot.maxUpgradeLevel != 0);
        invalidateItems();
        EUITourTooltip.queueFirstView(PGR.config.tourEditorForm,
                new EUITourTooltip(formEditor.header.hb, formEditor.header.tooltip.title, formEditor.header.tooltip.description).setCanDismiss(true),
                formEditor.add.makeTour(true),
                formEditor.remove.makeTour(true),
                imageButton.makeTour(true),
                undoButton.makeTour(true),
                saveButton.makeTour(true));
    }

    public void renderInnerElements(SpriteBatch sb) {
        super.renderInnerElements(sb);
        imageButton.tryRender(sb);
        formEditor.tryRender(sb);
        upgradeToggle.tryRender(sb);
        previewCard.render(sb);
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

    protected void rebuildItem() {
        previewCard = getBuilder().createImplWithForms(false);
        previewCard.setForm(currentBuilder, 0);

        if (SingleCardViewPopup.isViewingUpgrade) {
            //previewCard.upgrade();
            previewCard.displayUpgrades();
        }
        else {
            previewCard.displayUpgradesForSkills(false);
        }

        previewCard.drawScale = previewCard.targetDrawScale = 1f;
        previewCard.current_x = previewCard.target_x = CARD_X;
        previewCard.current_y = previewCard.target_y = CARD_Y;
    }

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

    public void setLoadedImage(Texture texture) {
        loadedImage = texture;
        modifyAllBuilders((e, i) -> e
                .setImagePath(currentSlot.getImagePath())
                .setImage(new ColoredTexture(texture)));
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = !SingleCardViewPopup.isViewingUpgrade;
        modifyBuilder(__ -> {
        });
    }

}
