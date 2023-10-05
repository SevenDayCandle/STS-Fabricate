package pinacolada.effects.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIRelicGrid;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import extendedui.utilities.RelicInfo;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.ui.characterSelection.PCLRelicSlotEditor;

// Copied and modified from STS-AnimatorMod
public class PCLRelicSlotSelectionEffect extends PCLEffectWithCallback<PCLRelicSlotSelectionEffect> {
    private static final EUITextBox cardValue_text = new
            EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(AbstractCard.IMG_WIDTH * 0.15f, AbstractCard.IMG_HEIGHT * 0.15f))
            .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
            .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
            .setAlignment(0.5f, 0.5f)
            .setFont(EUIFontHelper.cardTitleFontSmall, 1f);
    public final PCLRelicSlotEditor slot;
    private EUIRelicGrid grid;
    private AbstractRelic selectedRelic;

    public PCLRelicSlotSelectionEffect(PCLRelicSlotEditor slot) {
        super(0.7f, true);

        this.slot = slot;
        if (slot.getAvailableRelics().isEmpty()) {
            complete();
            return;
        }

        this.grid = (EUIRelicGrid) new EUIRelicGrid()
                .addPadY(AbstractCard.IMG_HEIGHT * 0.15f)
                .setOnClick(this::onRelicClicked)
                .setOnRender(this::onRelicRender);

        for (String item : slot.getAvailableRelics()) {
            AbstractRelic relic = RelicLibrary.getRelic(item);
            if (relic != null) {
                relic.currentX = InputHelper.mX;
                relic.currentY = InputHelper.mY;
                relic.isSeen = relic.isSeen || UnlockTracker.isRelicSeen(relic.relicId) || PCLCustomRelicSlot.get(relic.relicId) != null;
                grid.add(new RelicInfo(relic));
            }
        }

        EUI.relicFilters.initializeForSort(grid.group, __ -> {
            grid.moveToTop();
            grid.forceUpdatePositions();
        }, EUI.actingColor);
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

    public AbstractRelic getSelectedRelic() {
        return selectedRelic;
    }

    private void onRelicClicked(RelicInfo relic) {
        if (selectedRelic != null) {
            selectedRelic.stopPulse();

            if (selectedRelic == relic.relic) {
                selectedRelic = null;
                complete();
                return;
            }
        }

        selectedRelic = relic.relic;
        CardCrawlGame.sound.play("CARD_SELECT");
        complete(this);
    }

    private void onRelicRender(SpriteBatch sb, RelicInfo relic) {
        int estimateValue = relic.relic instanceof PCLRelic ? ((PCLRelic) relic.relic).relicData.loadoutValue : PCLRelicData.getValueForRarity(relic.relic.tier);
        cardValue_text
                .setLabel(estimateValue)
                .setFontColor(estimateValue < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                .setPosition(relic.relic.hb.cX, relic.relic.hb.cY - (relic.relic.hb.height * 0.65f))
                .renderImpl(sb);
    }

    @Override
    public void render(SpriteBatch sb) {
        grid.tryRender(sb);
        if (!EUI.relicFilters.isActive) {
            EUI.openFiltersButton.tryRender(sb);
        }
    }

    @Override
    protected void updateInternal(float deltaTime) {
        boolean shouldDoStandardUpdate = !EUI.relicFilters.tryUpdate();
        if (shouldDoStandardUpdate) {
            grid.tryUpdate();
            EUI.sortHeader.update();
            EUI.openFiltersButton.update();

            if (InputHelper.justClickedLeft && !grid.isHovered() && !EUI.openFiltersButton.hb.hovered) {
                complete();
            }
        }
    }
}