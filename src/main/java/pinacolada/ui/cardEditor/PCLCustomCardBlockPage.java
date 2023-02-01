package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.TextureCache;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.base.traits.PTrait_Block;
import pinacolada.skills.skills.base.traits.PTrait_BlockCount;
import pinacolada.skills.skills.base.traits.PTrait_BlockMultiplier;
import pinacolada.skills.skills.special.primary.PCardPrimary_GainBlock;

import java.util.List;

public class PCLCustomCardBlockPage extends PCLCustomCardEffectPage
{
    protected PCLCustomCardUpgradableEditor blockEditor;
    protected PCLCustomCardUpgradableEditor rightCountEditor;
    protected EUIToggle enableToggle;

    public PCLCustomCardBlockPage(PCLCustomCardEditCardScreen screen, PSkill<?> effect, EUIHitbox hb, int index, String title, ActionT1<PSkill<?>> onUpdate)
    {
        super(screen, effect, hb, index, title, onUpdate);
    }

    protected void setupComponents(PCLCustomCardEditCardScreen screen)
    {
        super.setupComponents(screen);
        effectGroup.setListFunc(PCLCustomCardBlockPage::getAvailableMoves);
        primaryConditions.setItems(new PCardPrimary_GainBlock()).setActive(false);
        delayEditor.setActive(false);

        enableToggle = (EUIToggle) new EUIToggle(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, 0, OFFSET_EFFECT))
                .setFont(EUIFontHelper.carddescriptionfontNormal, 0.9f)
                .setText(PGR.core.strings.cardEditor.enable)
                .setOnToggle(this::setMove);
        blockEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH, OFFSET_EFFECT * 1.3f)
                , PGR.core.strings.cardEditor.block, (val, upVal) -> screen.modifyBuilder(e -> e.setBlock(val, upVal, e.rightCount, e.rightCountUpgrade)))
                .setLimits(0, PSkill.DEFAULT_MAX)
                .setTooltip(PGR.core.tooltips.block);
        rightCountEditor = new PCLCustomCardUpgradableEditor(new OriginRelativeHitbox(hb, MENU_WIDTH / 4, MENU_HEIGHT, MENU_WIDTH * 1.5f, OFFSET_EFFECT * 1.3f)
                , EUIUtils.format(PGR.core.strings.cardEditor.hitCount, PGR.core.strings.cardEditor.block), (val, upVal) -> screen.modifyBuilder(e -> e.setRightCount(val, upVal)))
                .setLimits(1, PSkill.DEFAULT_MAX);
        rightCountEditor.setTooltip(rightCountEditor.header.text, PGR.core.strings.cardEditorTutorial.blockCount);
    }

    public void refresh()
    {
        super.refresh();
        enableToggle.setToggle(primaryCond != null);
        blockEditor.setValue(builder.getBlock(0), builder.getBlockUpgrade(0));
        rightCountEditor.setValue(builder.getRightCount(0), builder.getRightCountUpgrade(0));
        conditionGroup.refresh();
        modifierGroup.refresh();
        effectGroup.refresh();

        repositionItems();
    }

    protected void setMove(boolean add)
    {
        if (add)
        {
            primaryCond = primaryConditions.getAllItems().get(0);
        }
        else
        {
            primaryCond = null;
        }
        constructEffect();
    }

    protected static List<PMove<?>> getAvailableMoves()
    {
        return EUIUtils.list(
                new PTrait_Block(),
                new PTrait_BlockMultiplier(),
                new PTrait_BlockCount()
        );
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        blockEditor.tryUpdate();
        rightCountEditor.tryUpdate();
        enableToggle.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        blockEditor.tryRender(sb);
        rightCountEditor.tryRender(sb);
        enableToggle.tryRender(sb);
    }

    @Override
    public TextureCache getTextureCache()
    {
        return PGR.core.images.editorBlock;
    }
}
