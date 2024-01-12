package pinacolada.ui.editor.card;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.resources.PGR;

import static pinacolada.ui.editor.card.PCLCustomCardAttributesPage.MENU_HEIGHT;

public class PCLCustomCardTagEditorHeaderRow extends EUIHoverable {
    protected EUILabel valueHeader;
    protected EUILabel upgradeHeader;

    public PCLCustomCardTagEditorHeaderRow(EUIDropdown<?> parent) {
        super(new RelativeHitbox(parent.hb, parent.hb.width, parent.getRowHeight(), 0.0F, 0.0F));
        valueHeader = new EUILabel(FontHelper.topPanelAmountFont, new RelativeHitbox(hb, MENU_HEIGHT, MENU_HEIGHT, MENU_HEIGHT * 6, -MENU_HEIGHT * 0.23f).setIsPopupCompatible(true))
                .setLabel(PGR.core.strings.cedit_value)
                .setAlignment(0.5f, 0.5f)
                .setFontScale(0.6f);
        upgradeHeader = new EUILabel(FontHelper.topPanelAmountFont, new RelativeHitbox(hb, MENU_HEIGHT, MENU_HEIGHT, MENU_HEIGHT * 8.7f, -MENU_HEIGHT * 0.23f).setIsPopupCompatible(true))
                .setLabel(PGR.core.strings.cedit_upgrades)
                .setAlignment(0.5f, 0.5f)
                .setFontScale(0.6f);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        valueHeader.tryRender(sb);
        upgradeHeader.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        valueHeader.tryUpdate();
        upgradeHeader.tryUpdate();
    }
}
