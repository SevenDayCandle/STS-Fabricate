package pinacolada.ui.editor.potion;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import extendedui.EUIRM;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.screen.PCLCustomImageEffect;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLDynamicPotion;
import pinacolada.potions.PCLDynamicPotionData;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;

public class PCLCustomPotionEditScreen extends PCLCustomEditEntityScreen<PCLCustomPotionSlot, PCLDynamicPotionData, PCLDynamicPotion, PotionStrings> {
    protected PCLDynamicPotion preview;
    protected PCLCustomImageEffect imageEditor;
    protected EUITextBox previewDescription;
    protected Texture loadedImage;

    public PCLCustomPotionEditScreen(PCLCustomPotionSlot slot) {
        this(slot, false);
    }

    public PCLCustomPotionEditScreen(PCLCustomPotionSlot slot, boolean fromInGame) {
        super(slot);
    }

    protected void addSkillPages() {
        if (!fromInGame) {
            primaryPages.add(new PCLCustomPotionPrimaryInfoPage(this));
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
        imageEditor = (PCLCustomImageEffect) PCLCustomImageEffect.forPotion(image)
                .addCallback(pixmap -> {
                            if (pixmap != null) {
                                setLoadedImage(new Texture(pixmap));
                            }
                        }
                );
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), page instanceof PCLCustomPotionPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : "");
    }

    public void preInitialize(PCLCustomPotionSlot slot) {
        super.preInitialize(slot);
        previewDescription = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 256f))
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.tooltipFont, 1f)
                .setPosition(Settings.WIDTH * 0.105f, CARD_Y - LABEL_HEIGHT * 2);
        previewDescription.label.setSmartText(true);
    }

    protected void rebuildItem() {
        preview = getBuilder().create();
        preview.setTimesUpgraded(upgradeLevel);
        preview.scale = 1f;
        preview.posX = CARD_X;
        preview.posY = RELIC_Y;
        preview.hb.move(preview.posX, preview.posY);
        previewDescription.setLabel(preview.getUpdatedDescription());
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

    public void renderInnerElements(SpriteBatch sb) {
        super.renderInnerElements(sb);
        //imageButton.tryRender(sb);
        formEditor.tryRender(sb);
        upgradeToggle.tryRender(sb);
        preview.labRender(sb);
        previewDescription.tryRender(sb);
    }

    public void setLoadedImage(Texture texture) {
        loadedImage = texture;
        modifyAllBuilders((e, i) -> e
                .setImagePath(currentSlot.getImagePath())
                .setImage(texture));
    }

    protected void toggleViewUpgrades(int value) {
        super.toggleViewUpgrades(value);
        preview.setTimesUpgraded(upgradeLevel);
        previewDescription.setLabel(preview.getUpdatedDescription());
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
}
