package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.RelicInfo;
import pinacolada.effects.PCLEffect;
import pinacolada.resources.loadout.LoadoutRelicSlot;
import pinacolada.ui.characterSelection.PCLRelicSlotEditor;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLRelicSlotSelectionEffect extends PCLEffect {
    private static final EUITextBox cardValue_text = new
            EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(AbstractCard.IMG_WIDTH * 0.15f, AbstractCard.IMG_HEIGHT * 0.15f))
            .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
            .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
            .setAlignment(0.5f, 0.5f)
            .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

    private final PCLRelicSlotEditor slot;
    private EUIRelicGrid grid;
    private AbstractRelic selectedRelic;

    public PCLRelicSlotSelectionEffect(PCLRelicSlotEditor slot) {
        super(0.7f, true);

        this.selectedRelic = slot.slot.getRelic();
        this.slot = slot;
        ArrayList<LoadoutRelicSlot.Item> cards = slot.getSelectableRelics();
        if (cards.isEmpty()) {
            complete();
            return;
        }

        this.grid = (EUIRelicGrid) new EUIRelicGrid()
                .addPadY(AbstractCard.IMG_HEIGHT * 0.15f)
                .setOnClick(this::onRelicClicked)
                .setOnRender(this::onRelicRender);

        for (LoadoutRelicSlot.Item item : cards) {
            item.relic.currentX = InputHelper.mX;
            item.relic.currentY = InputHelper.mY;
            grid.add(new RelicInfo(item.relic));
        }
    }

    @Override
    protected void complete() {
        super.complete();

        if (selectedRelic != null && !selectedRelic.relicId.equals(slot.slot.getRelic().relicId)) {
            slot.slot.select(selectedRelic);
        }
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        super.firstUpdate(deltaTime);

        if (selectedRelic != null) {
            for (RelicInfo item : grid.group) {
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
        grid.tryRender(sb);
    }

    @Override
    protected void updateInternal(float deltaTime) {
        grid.tryUpdate();

        if (InputHelper.justClickedLeft && !grid.isHovered()) {
            complete();
        }
    }

    private void onRelicClicked(RelicInfo relic) {
        if (selectedRelic != null) {
            selectedRelic.stopPulse();

            if (selectedRelic == relic.relic) {
                slot.slot.select((AbstractRelic) null);
                selectedRelic = null;
                complete();
                return;
            }
        }

        selectedRelic = relic.relic;
        CardCrawlGame.sound.play("CARD_SELECT");
        slot.slot.select(relic.relic);
        relic.relic.beginLongPulse();
        complete();
    }

    private void onRelicRender(SpriteBatch sb, RelicInfo relic) {
        for (LoadoutRelicSlot.Item item : slot.slot.relics) {
            if (item.relic.relicId.equals(relic.relic.relicId)) {
                cardValue_text
                        .setLabel(item.estimatedValue)
                        .setFontColor(item.estimatedValue < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                        .setPosition(relic.relic.hb.cX, relic.relic.hb.cY - (relic.relic.hb.height * 0.65f))
                        .renderImpl(sb);
                return;
            }
        }
    }
}