package pinacolada.ui.cardView;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.compendium.CardLibSortHeader;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT2;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUIToggle;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.resources.PGR;

public class AugmentSortButton extends EUIButton {

    protected final EUIToggle sortDirectionToggle;
    protected ActionT2<Type, Boolean> onSort;
    protected Type sortType = Type.Name;
    protected boolean sortDesc;

    public AugmentSortButton(EUIHitbox hitbox, ActionT2<Type, Boolean> onSort) {
        super(EUIRM.images.rectangularButton.texture(), hitbox);
        this.onSort = onSort;
        setLabel(FontHelper.cardDescFont_N, 0.7f, sortType.getText());
        setOnClick(this::changeSorting);

        sortDirectionToggle = new EUIToggle(new RelativeHitbox(hitbox, scale(48), scale(48), hitbox.width + scale(48), hitbox.height * 0.5f))
                .setTickImage(new EUIImage(EUIRM.images.arrow.texture()), new EUIImage(EUIRM.images.arrow.texture()).setRotation(180f), 32)
                .setOnToggle(val -> {
                    sortDesc = val;
                    onSort.invoke(sortType, sortDesc);
                });
    }

    public void changeSorting() {
        sortType = Type.values()[(sortType.ordinal() + 1) % Type.values().length];
        setText(EUIRM.strings.sortBy(sortType.getText()));
        onSort.invoke(sortType, sortDesc);
    }

    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        sortDirectionToggle.renderImpl(sb);
    }

    public void updateImpl() {
        super.updateImpl();
        sortDirectionToggle.updateImpl();
    }

    public enum Type {
        Name,
        Count,
        Category,
        Level;

        public String getText() {
            switch (this) {
                case Name:
                    return CardLibSortHeader.TEXT[2];
                case Count:
                    return EUIRM.strings.ui_amount;
                case Level:
                    return PGR.core.tooltips.level.title;
                default:
                    return CardLibSortHeader.TEXT[1];
            }
        }
    }
}
