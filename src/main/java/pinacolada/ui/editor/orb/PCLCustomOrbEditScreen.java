package pinacolada.ui.editor.orb;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.OrbStrings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.screen.PCLCustomImageEffect;
import pinacolada.orbs.PCLCustomOrbSlot;
import pinacolada.orbs.PCLDynamicOrb;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.base.conditions.PCond_OnRemove;
import pinacolada.skills.skills.special.primary.PRoot;
import pinacolada.ui.PCLOrbRenderable;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;

public class PCLCustomOrbEditScreen extends PCLCustomEditEntityScreen<PCLCustomOrbSlot, PCLDynamicOrbData, PCLDynamicOrb, OrbStrings> {
    public static NewPageOption Evoke = new NewPageOption(PGR.core.tooltips.evoke.title, s -> s.initializeEffectPage(s.makeEffectPage(new PRoot().setChild(new PCond_OnRemove()))));
    protected PCLOrbRenderable preview;
    protected EUITextBox previewDescription;
    protected Texture loadedImage;

    public PCLCustomOrbEditScreen(PCLCustomOrbSlot slot) {
        this(slot, false);
    }

    public PCLCustomOrbEditScreen(PCLCustomOrbSlot slot, boolean fromInGame) {
        super(slot);
    }

    protected void addSkillPages() {
        if (!fromInGame) {
            primaryPages.add(new PCLCustomOrbPrimaryInfoPage(this));
        }
        primaryPages.add(new PCLCustomOrbAttributesPage(this));
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
        currentDialog = PCLCustomImageEffect.forOrb(image)
                .addCallback(pixmap -> {
                            if (pixmap != null) {
                                setLoadedImage(new Texture(pixmap));
                            }
                        }
                );
    }

    @Override
    protected NewPageOption[] getNewPageOptions() {
        return EUIUtils.array(NewPageOption.Generic, NewPageOption.Power, Evoke);
    }

    protected EUITooltip getPageTooltip(PCLCustomGenericPage page) {
        return new EUITooltip(page.getTitle(), page instanceof PCLCustomOrbPrimaryInfoPage ? PGR.core.strings.cedit_primaryInfoDesc : "");
    }

    // Does not use power effects
    public int getPowerLimit() {
        return 0;
    }

    public void preInitialize(PCLCustomOrbSlot slot) {
        super.preInitialize(slot);
        previewDescription = new EUITextBox(EUIRM.images.greySquare.texture(), new EUIHitbox(0, 0, Settings.scale * 256f, Settings.scale * 256f))
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.tooltipFont, 1f)
                .setPosition(Settings.WIDTH * 0.105f, CARD_Y - LABEL_HEIGHT * 2);
        previewDescription.label.setSmartText(true);
    }

    protected void rebuildItem() {
        preview = getBuilder().makeRenderableWithLevel(upgradeLevel);
        preview.scale = 1f;
        preview.currentX = preview.targetX = CARD_X;
        preview.currentY = preview.targetY = RELIC_Y;
        preview.hb.move(preview.currentX, preview.currentY);
        previewDescription.setLabel(!StringUtils.isEmpty(preview.mainTooltip.description) ? preview.mainTooltip.description : getBuilder().getEffectTextForPreview(upgradeLevel));
        preview.amountText = String.valueOf(upgradeLevel);
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
