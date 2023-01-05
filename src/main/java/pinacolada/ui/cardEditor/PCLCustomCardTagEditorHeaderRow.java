package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;

import static pinacolada.ui.cardEditor.PCLCustomCardAttributesPage.MENU_HEIGHT;

public class PCLCustomCardTagEditorHeaderRow extends EUIHoverable
{
    protected EUILabel valueHeader;
    protected EUILabel upgradeHeader;

    public PCLCustomCardTagEditorHeaderRow(EUIDropdown<?> parent)
    {
        super(new RelativeHitbox(parent.hb, parent.hb.width, parent.getRowHeight(), 0.0F, 0.0F));
        valueHeader = new EUILabel(EUIFontHelper.cardtitlefontSmall, new RelativeHitbox(hb, MENU_HEIGHT, MENU_HEIGHT, MENU_HEIGHT * 6, 0).setIsPopupCompatible(true))
                .setLabel(PGR.core.strings.cardEditor.value)
                .setFontScale(0.6f);
        upgradeHeader = new EUILabel(EUIFontHelper.cardtitlefontSmall, new RelativeHitbox(valueHeader.hb, valueHeader.hb.width, valueHeader.hb.height, MENU_HEIGHT * 2.7f, 0).setIsPopupCompatible(true))
                .setLabel(PGR.core.strings.cardEditor.upgrades)
                .setFontScale(0.6f);
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        valueHeader.tryUpdate();
        upgradeHeader.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        valueHeader.tryRender(sb);
        upgradeHeader.tryRender(sb);
    }
}
