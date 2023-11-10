package pinacolada.ui.editor.relic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.screen.PCLCustomImageEffect;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLDynamicRelic;
import pinacolada.relics.PCLDynamicRelicData;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;

public class PCLCustomRelicEditScreen extends PCLCustomEditEntityScreen<PCLCustomRelicSlot, PCLDynamicRelicData, PCLDynamicRelic> {
    protected PCLDynamicRelic previewRelic;
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
            primaryPages.add(new PCLCustomRelicPrimaryInfoPage(this));
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
        previewDescription = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 256f))
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.cardTipBodyFont, 1f)
                .setPosition(Settings.WIDTH * 0.105f, CARD_Y - LABEL_HEIGHT * 2);
        previewDescription.label.setSmartText(true);
    }

    protected void rebuildItem() {
        previewRelic = getBuilder().create();
        previewRelic.setTimesUpgraded(upgradeLevel);
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

    protected void toggleViewUpgrades(int value) {
        super.toggleViewUpgrades(value);
        previewRelic.setTimesUpgraded(upgradeLevel);
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
}
