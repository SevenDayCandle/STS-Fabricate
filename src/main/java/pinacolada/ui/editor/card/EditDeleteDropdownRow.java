package pinacolada.ui.editor.card;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIDropdownRow;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;

import static pinacolada.ui.editor.card.PCLCustomCardAttributesPage.MENU_HEIGHT;

public class EditDeleteDropdownRow<T,U> extends EUIDropdownRow<T> {
    public static final float ICON_SIZE = 24f * Settings.scale;

    protected EUIButton editButton;
    protected EUIButton deleteButton;

    public EditDeleteDropdownRow(EUIDropdown<T> dr, EUIHitbox hb, T item, U info, int index, ActionT1<U> onEdit, ActionT1<U> onDelete) {
        super(dr, hb, item, index);

        editButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new RelativeHitbox(hb, MENU_HEIGHT * 0.65f, MENU_HEIGHT * 0.65f, MENU_HEIGHT * 6.3f, MENU_HEIGHT * 0.35f)
                .setIsPopupCompatible(true).setParentElement(dr))
                .setTooltip(PGR.core.strings.cedit_renameItem, "");
        deleteButton = new EUIButton(PCLCoreImages.Menu.delete.texture(), new RelativeHitbox(hb, MENU_HEIGHT * 0.65f, MENU_HEIGHT * 0.65f, MENU_HEIGHT * 7.1f, MENU_HEIGHT * 0.35f)
                .setIsPopupCompatible(true).setParentElement(dr))
                .setTooltip(PGR.core.strings.cedit_deleteItem, "");

        if (info != null) {
            editButton.setOnClick(() -> {
                dr.forceClose();
                onEdit.invoke(info);
            });
            deleteButton.setOnClick(() -> {
                dr.forceClose();
                onDelete.invoke(info);
            });
        }
        else {
            editButton.setActive(false);
            deleteButton.setActive(false);
        }
    }

    @Override
    protected boolean isComponentHovered() {
        return super.isComponentHovered() && !(editButton.hb.hovered || deleteButton.hb.hovered);
    }

    public void renderRow(SpriteBatch sb) {
        super.renderRow(sb);
        editButton.tryRenderCentered(sb);
        deleteButton.tryRenderCentered(sb);
    }

    @Override
    public boolean update(boolean isInRange, boolean isSelected) {
        this.hb.update();
        this.label.updateImpl();
        this.checkbox.updateImpl();
        editButton.update();
        deleteButton.update();
        this.isSelected = isSelected;
        if (!isInRange) {
            return false;
        }
        return tryHover(isSelected);
    }
}
