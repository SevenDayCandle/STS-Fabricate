package pinacolada.ui.editor.relic;

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
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLDynamicRelic;
import pinacolada.relics.PCLDynamicRelicData;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomFormEditor;
import pinacolada.ui.editor.PCLCustomGenericPage;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;
import static pinacolada.ui.editor.PCLCustomEffectEditingPane.invalidateItems;

public class PCLCustomRelicEditScreen extends PCLCustomEditEntityScreen<PCLCustomRelicSlot, PCLDynamicRelicData> {
    protected EUIToggle upgradeToggle;
    protected PCLDynamicRelic previewRelic;
    protected PCLCustomFormEditor formEditor;
    protected EUIButton imageButton;
    protected EUITextBox previewDescription;
    protected Texture loadedImage;

    public PCLCustomRelicEditScreen(PCLCustomRelicSlot slot) {
        this(slot, false);
    }

    public PCLCustomRelicEditScreen(PCLCustomRelicSlot slot, boolean fromInGame) {
        super(slot);
    }

    protected void addSkillPages() {
        if (!fromInGame) {
            pages.add(new PCLCustomRelicPrimaryInfoPage(this));
        }
        super.addSkillPages();
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
        currentDialog = PCLCustomImageEffect.forRelic(image)
                .addCallback(pixmap -> {
                            if (pixmap != null) {
                                setLoadedImage(new Texture(pixmap));
                            }
                        }
                );
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), page instanceof PCLCustomRelicPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : "");
    }

    public void preInitialize(PCLCustomRelicSlot slot) {
        super.preInitialize(slot);
        imageButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, undoButton.hb.y + undoButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.WHITE)
                .setTooltip(PGR.core.strings.cedit_loadImage, PGR.core.strings.cetut_primaryImage)
                .setLabel(EUIFontHelper.buttonFont, 0.85f, PGR.core.strings.cedit_loadImage)
                .setOnClick(this::editImage);

        formEditor = new PCLCustomFormEditor(
                new EUIHitbox(Settings.WIDTH * 0.04f, imageButton.hb.y + imageButton.hb.height + LABEL_HEIGHT * 3.2f, Settings.scale * 90f, Settings.scale * 48f), this);

        previewDescription = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 256f))
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 1f)
                .setPosition(Settings.WIDTH * 0.105f, CARD_Y - LABEL_HEIGHT * 2);
        previewDescription.label.setSmartText(true);

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(Settings.WIDTH * 0.105f, CARD_Y - LABEL_HEIGHT - AbstractCard.IMG_HEIGHT / 2f)
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

    protected void rebuildItem() {
        previewRelic = getBuilder().create();
        previewRelic.setTimesUpgraded(upgraded ? 1 : 0);
        previewRelic.scale = 1f;
        previewRelic.currentX = previewRelic.targetX = CARD_X;
        previewRelic.currentY = previewRelic.targetY = RELIC_Y;
        previewRelic.hb.move(previewRelic.currentX, previewRelic.currentY);
        previewDescription.setLabel(previewRelic.getDescriptionImpl());
    }

    public void renderInnerElements(SpriteBatch sb) {
        super.renderInnerElements(sb);
        imageButton.tryRender(sb);
        formEditor.tryRender(sb);
        upgradeToggle.tryRender(sb);
        previewRelic.render(sb);
        previewDescription.tryRender(sb);
    }

    public void setLoadedImage(Texture texture) {
        loadedImage = texture;
        modifyAllBuilders((e, i) -> e
                .setImagePath(currentSlot.getImagePath())
                .setImage(texture));
    }

    protected void toggleViewUpgrades(boolean value) {
        super.toggleViewUpgrades(value);
        previewRelic.setTimesUpgraded(upgraded ? 1 : 0);
        previewDescription.setLabel(previewRelic.getDescriptionImpl());
    }

    public void updateInnerElements() {
        super.updateInnerElements();
        imageButton.tryUpdate();
        formEditor.tryUpdate();
        upgradeToggle.tryUpdate();
        previewRelic.hb.update();
        previewDescription.tryUpdate();
        if (previewRelic.hb.hovered) {
            EUITooltip.queueTooltips(previewRelic);
        }
    }

    protected void updateVariant() {
        formEditor.refresh();
    }

}
