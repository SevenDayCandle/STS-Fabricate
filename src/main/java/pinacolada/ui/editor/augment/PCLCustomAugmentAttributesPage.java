package pinacolada.ui.editor.augment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITourTooltip;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLDynamicAugmentData;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEditEntityScreen;
import pinacolada.ui.editor.PCLCustomGenericPage;
import pinacolada.ui.editor.PCLCustomUpgradableEditor;

public class PCLCustomAugmentAttributesPage extends PCLCustomGenericPage {
    protected static final float START_X = screenW(0.25f);
    public static final float MENU_WIDTH = scale(160);
    public static final float MENU_HEIGHT = scale(40);
    public static final float SPACING_WIDTH = screenW(0.06f);
    protected PCLCustomAugmentEditScreen screen;
    protected EUILabel header;
    protected PCLCustomUpgradableEditor tierEditor;
    protected EUILabel upgradeLabel;

    public PCLCustomAugmentAttributesPage(PCLCustomAugmentEditScreen screen) {
        this.screen = screen;

        this.header = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(screenW(0.5f), PCLCustomEditEntityScreen.START_Y, MENU_WIDTH, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontLarge, 0.8f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_attributes);

        // Number editors
        float curW = START_X;
        upgradeLabel = new EUILabel(EUIFontHelper.cardTitleFontSmall,
                new EUIHitbox(curW, screenH(0.8f) - MENU_HEIGHT * 0.8f, MENU_WIDTH / 4, MENU_HEIGHT))
                .setAlignment(0.5f, 0.0f, false)
                .setFont(EUIFontHelper.cardTitleFontSmall, 0.6f).setColor(Color.LIGHT_GRAY)
                .setLabel(PGR.core.strings.cedit_upgrades)
                .setTooltip(PGR.core.strings.cedit_upgrades, PGR.core.strings.cetut_amount);
        curW += SPACING_WIDTH;
        tierEditor = new PCLCustomUpgradableEditor(new EUIHitbox(curW, screenH(0.8f), MENU_WIDTH / 4, MENU_HEIGHT)
                , PGR.core.strings.augment_tier, (val, upVal) -> screen.modifyAllBuilders((e, i) -> e.setTierForForm(screen.currentBuilder, screen.getTempBuilders().size(), val, upVal)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.strings.augment_tier, PGR.core.strings.cetut_augmentTier);

        refresh();
    }

    @Override
    public TextureCache getTextureCache() {
        return EUIRM.images.tag;
    }

    @Override
    public String getTitle() {
        return header.text;
    }

    @Override
    public EUITourTooltip[] getTour() {
        return EUIUtils.array(
                tierEditor.makeTour(true)
        );
    }

    @Override
    public void onOpen() {
        EUITourTooltip.queueFirstView(PGR.config.tourCardAttribute, getTour());
    }

    @Override
    public void refresh() {
        PCLDynamicAugmentData builder = screen.getBuilder();
        int form = screen.currentBuilder;

        tierEditor.setValue(builder.getTier(form), builder.getTierUpgrade(form));
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        header.tryRender(sb);
        upgradeLabel.tryRender(sb);
        tierEditor.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        header.tryUpdate();
        tierEditor.tryUpdate();
        upgradeLabel.tryUpdate();
    }
}
