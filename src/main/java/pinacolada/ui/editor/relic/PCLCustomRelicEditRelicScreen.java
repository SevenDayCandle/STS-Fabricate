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
import extendedui.utilities.ColoredTexture;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.screen.PCLCustomImageEffect;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLDynamicRelic;
import pinacolada.relics.PCLDynamicRelicData;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomFormEditor;
import pinacolada.ui.editor.PCLCustomGenericPage;

import static extendedui.ui.AbstractScreen.createHexagonalButton;
import static pinacolada.ui.editor.PCLCustomEffectEditingPane.invalidateCards;

public class PCLCustomRelicEditRelicScreen extends PCLCustomEditEntityScreen<PCLCustomRelicSlot, PCLDynamicRelicData> {
    public static final float RELIC_Y = Settings.HEIGHT * 0.87f;

    protected EUIToggle upgradeToggle;
    protected PCLDynamicRelic previewRelic;
    protected PCLCustomImageEffect imageEditor;
    protected PCLCustomFormEditor formEditor;
    protected EUIButton imageButton;
    protected EUITextBox previewDescription;
    protected Texture loadedImage;

    public PCLCustomRelicEditRelicScreen(PCLCustomRelicSlot slot) {
        this(slot, false);
    }

    public PCLCustomRelicEditRelicScreen(PCLCustomRelicSlot slot, boolean fromInGame) {
        super(slot);
    }

    protected void addSkillPages() {
        if (!fromInGame) {
            pages.add(new PCLCustomRelicPrimaryInfoPage(this));
        }
        super.addSkillPages();
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), page instanceof PCLCustomRelicPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : "");
    }

    public void preInitialize(PCLCustomRelicSlot slot) {
        super.preInitialize(slot);
        imageButton = createHexagonalButton(0, 0, BUTTON_WIDTH, BUTTON_HEIGHT)
                .setPosition(cancelButton.hb.cX, undoButton.hb.y + undoButton.hb.height + LABEL_HEIGHT * 0.8f)
                .setColor(Color.WHITE)
                .setText(PGR.core.strings.cedit_loadImage)
                .setTooltip(PGR.core.strings.cedit_loadImage, PGR.core.strings.cetut_primaryImage)
                .setFont(EUIFontHelper.buttonFont, 0.85f)
                .setOnClick(this::editImage);

        formEditor = new PCLCustomFormEditor(
                new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 48f)
                        .setCenter(Settings.WIDTH * 0.116f, imageButton.hb.y + imageButton.hb.height + LABEL_HEIGHT * 3.2f), this);

        previewDescription = new EUITextBox(EUIRM.images.panel.texture(), new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 256f))
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 1f)
                .setPosition(Settings.WIDTH * 0.116f, CARD_Y - LABEL_HEIGHT * 2);
        previewDescription.label.setSmartText(true);

        upgradeToggle = new EUIToggle(new EUIHitbox(Settings.scale * 256f, Settings.scale * 48f))
                .setPosition(Settings.WIDTH * 0.116f, CARD_Y - LABEL_HEIGHT - AbstractCard.IMG_HEIGHT / 2f)
                .setBackground(EUIRM.images.panel.texture(), Color.DARK_GRAY)
                .setFont(EUIFontHelper.carddescriptionfontLarge, 0.5f)
                .setText(SingleCardViewPopup.TEXT[6])
                .setToggle(SingleCardViewPopup.isViewingUpgrade)
                .setOnToggle(this::toggleViewUpgrades);

        upgradeToggle.setActive(slot.maxUpgradeLevel != 0);

        invalidateCards();
    }

    protected void rebuildItem() {
        previewRelic = getBuilder().create();
        previewRelic.scale = 1f;
        previewRelic.currentX = previewRelic.targetX = CARD_X;
        previewRelic.currentY = previewRelic.targetY = RELIC_Y;
        previewRelic.hb.move(previewRelic.currentX, previewRelic.currentY);
        previewDescription.setLabel(previewRelic.getUpdatedDescription());
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
        imageButton.tryRender(sb);
        formEditor.tryRender(sb);
        upgradeToggle.tryRender(sb);
        previewRelic.render(sb);
        previewDescription.tryRender(sb);
    }

    public void updateInnerElements() {
        super.updateInnerElements();
        imageButton.tryUpdate();
        formEditor.tryUpdate();
        upgradeToggle.tryUpdate();
        previewRelic.update();
        previewRelic.hb.update();
        previewDescription.tryUpdate();
        if (previewRelic.hb.hovered) {
            EUITooltip.queueTooltips(previewRelic);
        }
    }

    protected void updateVariant() {
        formEditor.refresh();
    }

    protected void complete() {
        super.complete();
        invalidateCards();
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
        modifyAllBuilders(e -> e
                .setImagePath(currentSlot.getImagePath())
                .setImage(new ColoredTexture(loadedImage)));
    }

    private void toggleViewUpgrades(boolean value) {
        SingleCardViewPopup.isViewingUpgrade = !SingleCardViewPopup.isViewingUpgrade;
        modifyBuilder(__ -> {});
    }

}