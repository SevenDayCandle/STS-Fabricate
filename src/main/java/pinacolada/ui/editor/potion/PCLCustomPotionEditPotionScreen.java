package pinacolada.ui.editor.potion;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.screen.PCLCustomImageEffect;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLDynamicPotion;
import pinacolada.potions.PCLDynamicPotionData;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomFormEditor;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.ui.editor.relic.PCLCustomRelicPrimaryInfoPage;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;
import static pinacolada.ui.editor.PCLCustomEffectEditingPane.invalidateItems;

public class PCLCustomPotionEditPotionScreen extends PCLCustomEditEntityScreen<PCLCustomPotionSlot, PCLDynamicPotionData> {
    public static final float RELIC_Y = Settings.HEIGHT * 0.87f;

    protected EUIToggle upgradeToggle;
    protected PCLDynamicPotion preview;
    protected PCLCustomImageEffect imageEditor;
    protected PCLCustomFormEditor formEditor;
    protected EUIButton imageButton;
    protected EUITextBox previewDescription;
    protected Texture loadedImage;

    public PCLCustomPotionEditPotionScreen(PCLCustomPotionSlot slot) {
        this(slot, false);
    }

    public PCLCustomPotionEditPotionScreen(PCLCustomPotionSlot slot, boolean fromInGame) {
        super(slot);
    }

    protected void addSkillPages() {
        if (!fromInGame) {
            pages.add(new PCLCustomPotionPrimaryInfoPage(this));
        }
        super.addSkillPages();
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), page instanceof PCLCustomRelicPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : "");
    }

    public void preInitialize(PCLCustomPotionSlot slot) {
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

        previewDescription = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 256f))
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 1f)
                .setPosition(Settings.WIDTH * 0.116f, CARD_Y - LABEL_HEIGHT * 2);
        previewDescription.label.setSmartText(true);

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(Settings.WIDTH * 0.116f, CARD_Y - LABEL_HEIGHT - AbstractCard.IMG_HEIGHT / 2f)
                .setBackground(EUIRM.images.greySquare.texture(), Color.DARK_GRAY)
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

    @Override
    public void render(SpriteBatch sb) {
        if (imageEditor != null) {
            imageEditor.render(sb);
        }
        else {
            super.render(sb);
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (imageEditor != null) {
            imageEditor.update();
            if (imageEditor.isDone) {
                imageEditor = null;
            }
        }
        else {
            super.updateInternal(deltaTime);
        }
    }

    public void renderInnerElements(SpriteBatch sb) {
        super.renderInnerElements(sb);
        //imageButton.tryRender(sb);
        formEditor.tryRender(sb);
        upgradeToggle.tryRender(sb);
        preview.labRender(sb);
        previewDescription.tryRender(sb);
    }

    public void updateInnerElements() {
        super.updateInnerElements();
        //imageButton.tryUpdate();
        formEditor.tryUpdate();
        upgradeToggle.tryUpdate();
        preview.hb.update();
        previewDescription.tryUpdate();
        if (preview.hb.hovered) {
            EUITooltip.queueTooltips(preview);
        }
    }

    protected void updateVariant() {
        formEditor.refresh();
    }

    protected void rebuildItem() {
        preview = getBuilder().create();
        preview.scale = 1f;
        preview.posX = CARD_X;
        preview.posY = RELIC_Y;
        preview.hb.move(preview.posX, preview.posY);
        previewDescription.setLabel(preview.getUpdatedDescription());
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
            image = getBuilder().portraitImage;
        }
        imageEditor = (PCLCustomImageEffect) PCLCustomImageEffect.forRelic(image)
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
                .setImage(texture));
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = !SingleCardViewPopup.isViewingUpgrade;
        modifyBuilder(__ -> {
        });
    }

}
