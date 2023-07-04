package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIInputManager;
import extendedui.EUIRM;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.controls.EUIRelic;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.effects.PCLEffect;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.loadout.LoadoutRelicSlot;
import pinacolada.ui.cardView.PCLRelicSlotList;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLRelicSlotSelectionEffect extends PCLEffect {
    private final LoadoutRelicSlot slot;
    private final PCLRelicSlotList list;
    private AbstractRelic selectedRelic;

    public PCLRelicSlotSelectionEffect(LoadoutRelicSlot slot) {
        super(0.7f, true);

        list = new PCLRelicSlotList();
        this.selectedRelic = slot.getRelic();
        this.slot = slot;
        final ArrayList<LoadoutRelicSlot.Item> slotItems = slot.getSelectableRelics();
        for (LoadoutRelicSlot.Item slotItem : slotItems) {
            list.addListItem(slotItem);
        }

        if (list.relics.isEmpty()) {
            complete();
        }
    }

    @Override
    protected void complete() {
        super.complete();

        if (selectedRelic != null && !selectedRelic.relicId.equals(slot.getRelic().relicId)) {
            slot.select(selectedRelic);
        }
    }

    @Override
    protected void firstUpdate() {
        super.firstUpdate();

        if (selectedRelic != null) {
            for (PCLRelicSlotList.RenderItem item : list.relics) {
                if (item.relic.relicId.equals(selectedRelic.relicId)) {
                    selectedRelic = item.relic;
                    selectedRelic.beginLongPulse();
                    break;
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        list.render(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        list.update();

        if (tickDuration(deltaTime)) {
            if (EUIInputManager.leftClick.isJustReleased()) {
                for (PCLRelicSlotList.RenderItem item : list.relics) {
                    if (item.relicImage.hb.hovered || item.relicNameText.hb.hovered) {
                        onRelicClicked(item.relic);
                    }
                }
                complete();
                return;
            }

            isDone = false;
        }
    }

    private void onRelicClicked(AbstractRelic relic) {
        if (selectedRelic != null) {
            selectedRelic.stopPulse();

            if (selectedRelic == relic) {
                slot.select((AbstractRelic) null);
                selectedRelic = null;
                return;
            }
        }

        selectedRelic = relic;
        CardCrawlGame.sound.play("CARD_SELECT");
        slot.select(relic);
        relic.beginLongPulse();
    }
}