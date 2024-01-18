package pinacolada.ui.editor.augment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.PCLCustomAugmentSlot;
import pinacolada.augments.PCLDynamicAugment;
import pinacolada.augments.PCLDynamicAugmentData;
import pinacolada.effects.screen.PCLCustomImageEffect;
import pinacolada.misc.AugmentStrings;
import pinacolada.resources.PGR;
import pinacolada.ui.PCLAugmentRenderable;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;

public class PCLCustomAugmentEditScreen extends PCLCustomEditEntityScreen<PCLCustomAugmentSlot, PCLDynamicAugmentData, PCLDynamicAugment, AugmentStrings> {
    protected PCLAugmentRenderable preview;
    protected EUITextBox previewDescription;
    protected Texture loadedImage;

    public PCLCustomAugmentEditScreen(PCLCustomAugmentSlot slot) {
        this(slot, false);
    }

    public PCLCustomAugmentEditScreen(PCLCustomAugmentSlot slot, boolean fromInGame) {
        super(slot);
    }

    protected void addSkillPages() {
        if (!fromInGame) {
            primaryPages.add(new PCLCustomAugmentPrimaryInfoPage(this));
        }
        primaryPages.add(new PCLCustomAugmentAttributesPage(this));
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
        // Augments use the same image size as relics
        currentDialog = PCLCustomImageEffect.forPower(image)
                .addCallback(pixmap -> {
                            if (pixmap != null) {
                                setLoadedImage(new Texture(pixmap));
                            }
                        }
                );
    }

    @Override
    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), page instanceof PCLCustomAugmentPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : "");
    }

    public void preInitialize(PCLCustomAugmentSlot slot) {
        super.preInitialize(slot);


        previewDescription = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 256f))
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.tooltipFont, 0.8f)
                .setPosition(Settings.WIDTH * 0.105f, CARD_Y - LABEL_HEIGHT * 2);
        previewDescription.label.setSmartText(true);

        upgradeToggle.setActive(slot.maxUpgradeLevel != 0);
    }

    protected void rebuildItem() {
        preview = getBuilder().createRenderable(currentBuilder, upgradeLevel);
        preview.scale = 1f;
        preview.currentX = preview.targetX = CARD_X;
        preview.currentY = preview.targetY = RELIC_Y;
        preview.hb.move(preview.currentX, preview.currentY);
        previewDescription.setLabel(!StringUtils.isEmpty(preview.mainTooltip.description) ? preview.mainTooltip.description : getBuilder().getEffectTextForPreview(upgradeLevel));
    }

    public void renderInnerElements(SpriteBatch sb) {
        super.renderInnerElements(sb);
        imageButton.tryRender(sb);
        formEditor.tryRender(sb);
        upgradeToggle.tryRender(sb);
        preview.render(sb);
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
        rebuildItem();
    }

    public void updateInnerElements() {
        super.updateInnerElements();
        imageButton.tryUpdate();
        formEditor.tryUpdate();
        upgradeToggle.tryUpdate();
        preview.hb.update();
        previewDescription.tryUpdate();
        if (preview.hb.hovered) {
            EUITooltip.queueTooltips(preview.tips);
        }
    }
}
