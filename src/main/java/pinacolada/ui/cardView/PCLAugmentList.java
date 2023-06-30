package pinacolada.ui.cardView;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUICanvasGrid;
import extendedui.ui.hitboxes.EUIHitbox;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.PCLAugment;

import java.util.ArrayList;

import static extendedui.ui.controls.EUIButton.createHexagonalButton;

public class PCLAugmentList extends EUICanvasGrid {

    protected static final float X_START = Settings.WIDTH * 0.22f;
    protected static final float Y_START = Settings.HEIGHT * 0.77f;
    protected static final float X_PAD = Settings.WIDTH * 0.19f;
    protected static final float Y_PAD = scale(80);
    public static final int DEFAULT = 3;
    protected int hoveredIndex;
    protected EUIButton cancel;
    protected AugmentSortButton sortButton;
    protected ActionT1<PCLAugment> onComplete;
    public ArrayList<PCLAugmentListItem> augments = new ArrayList<>();

    public PCLAugmentList(ActionT1<PCLAugment> onComplete) {
        this(onComplete, DEFAULT);
    }

    public PCLAugmentList(ActionT1<PCLAugment> onComplete, int rowSize) {
        super(rowSize, Y_PAD);

        this.onComplete = onComplete;
        cancel = createHexagonalButton(screenW(0.015f), screenH(0.07f), screenW(0.12f), screenH(0.068f))
                .setText(CharacterSelectScreen.TEXT[5])
                .setOnClick(() -> this.onComplete.invoke(null))
                .setColor(Color.FIREBRICK);
        sortButton = new AugmentSortButton(new EUIHitbox(0, 0, scale(135), scale(32)), this::sortAugments);
    }

    public void addListItem(PCLAugment augment, float amount) {
        this.augments.add(new PCLAugmentListItem(onComplete, augment, amount));
    }

    public void addPanelItem(PCLAugment augment, int count, boolean enabled) {
        this.augments.add(new PCLAugmentButtonListItem(onComplete, augment, count, enabled));
    }

    public void clear() {
        augments.clear();
    }

    @Override
    public int currentSize() {
        return augments.size();
    }

    public PCLAugmentList enableCancel(boolean val) {
        cancel.setActive(val);
        return this;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        sortButton.renderImpl(sb);
        for (PCLAugmentListItem item : augments) {
            if (item.hb.y >= -100f * Settings.scale && item.hb.y <= Settings.HEIGHT + 100f * Settings.scale) {
                item.renderImpl(sb);
            }
        }
        cancel.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        int row = 0;
        int column = 0;

        sortButton.setTargetPosition(X_START, Y_START + scrollDelta + padY).updateImpl();

        for (int i = 0; i < augments.size(); i++) {
            PCLAugmentListItem item = augments.get(i);
            item.hb.setTargetCenter((X_START) + (column * X_PAD), Y_START + scrollDelta - (row * padY));
            item.updateImpl();

            if (item.hb.hovered) {
                hoveredIndex = i;
            }

            column += 1;
            if (column >= rowSize) {
                column = 0;
                row += 1;
            }
        }
        cancel.tryUpdate();
    }

    protected void sortAugments(AugmentSortButton.Type sortType, boolean sortDesc) {
        int multiplier = sortDesc ? -1 : 1;
        augments.sort((a, b) -> sortImpl(a, b, sortType) * multiplier);
    }

    protected int sortImpl(PCLAugmentListItem a, PCLAugmentListItem b, AugmentSortButton.Type sortType) {
        switch (sortType) {
            case Name:
                return StringUtils.compare(a.augment.augment.getName(), b.augment.augment.getName());
            case Count:
                return Float.compare(a.amount, b.amount);
            case Category:
                return a.augment.augment.data.category.ordinal() - b.augment.augment.data.category.ordinal();
            case Level:
                return a.augment.augment.data.tier - b.augment.augment.data.tier;
        }
        return 0;
    }

}
