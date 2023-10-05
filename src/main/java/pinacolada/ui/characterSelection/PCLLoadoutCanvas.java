package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUICanvas;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.LoadoutBlightSlot;
import pinacolada.resources.loadout.LoadoutCardSlot;
import pinacolada.resources.loadout.LoadoutRelicSlot;
import pinacolada.resources.loadout.PCLLoadoutData;

import java.util.ArrayList;

public class PCLLoadoutCanvas extends EUICanvas {
    private static final float SLOT_SPACING = screenH(0.05f);
    private static final float BASE_HEIGHT = screenH(0.84f);
    private static final float BASE_X = screenW(0.1f);
    protected static final float BUTTON_SIZE = scale(42);
    protected final ArrayList<PCLCardSlotEditor> cardEditors = new ArrayList<>();
    protected final ArrayList<PCLRelicSlotEditor> relicsEditors = new ArrayList<>();
    protected final ArrayList<PCLAbilityEditor> abilityEditors = new ArrayList<>();
    protected final PCLLoadoutScreen screen;
    private PCLCardSlotEditor queuedCardSlot;
    private PCLRelicSlotEditor queuedRelicSlot;
    protected EUIButton addCardButton;
    protected EUIButton addRelicButton;
    protected EUILabel deckText;
    protected EUILabel relicText;
    protected EUILabel abilityText;

    public PCLLoadoutCanvas(PCLLoadoutScreen screen) {
        this.screen = screen;

        final float buttonHeight = screenH(0.07f);
        deckText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.1f), BASE_HEIGHT, buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.loadout_deckHeader)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.01f)
                .autosize(1f, null);

        relicText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.1f), screenH(0.4f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.loadout_relicHeader)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.01f)
                .autosize(1f, null);

        abilityText = new EUILabel(EUIFontHelper.cardTitleFontLarge,
                new EUIHitbox(screenW(0.1f), screenH(0.2f), buttonHeight, buttonHeight))
                .setLabel(PGR.core.strings.csel_ability)
                .setFontScale(0.8f)
                .setAlignment(0.5f, 0.01f);
        addCardButton = new EUIButton(EUIRM.images.plus.texture(), new OriginRelativeHitbox(deckText.hb, BUTTON_SIZE, BUTTON_SIZE, deckText.hb.width + BUTTON_SIZE, BUTTON_SIZE / 2))
                .setOnClick(this::addCardSlot)
                .setTooltip(PGR.core.strings.loadout_add, "");
        addRelicButton = new EUIButton(EUIRM.images.plus.texture(), new OriginRelativeHitbox(relicText.hb, BUTTON_SIZE, BUTTON_SIZE, relicText.hb.width + BUTTON_SIZE, BUTTON_SIZE / 2))
                .setOnClick(this::addRelicSlot)
                .setTooltip(PGR.core.strings.loadout_add, "");
    }

    private void addBlightSlotFromData(LoadoutBlightSlot cardSlot) {
        PCLAbilityEditor slot = new PCLAbilityEditor(this);
        slot.setSlot(cardSlot);
        abilityEditors.add(slot);
    }

    public void addCardSlot() {
        PCLCardSlotEditor editor = new PCLCardSlotEditor(this);
        screen.trySelectCard(editor).addCallback((sl) -> {
            if (sl != null && sl.getSelectedCard() != null) {
                LoadoutCardSlot newSlot = screen.getCurrentPreset().addCardSlot(sl.getSelectedCard().cardID, 1);
                editor.setSlot(newSlot);
                cardEditors.add(editor);
                updateEditorPositions();
                screen.updateValidation();
            }
        });
    }

    private void addCardSlotFromData(LoadoutCardSlot cardSlot) {
        PCLCardSlotEditor slot = new PCLCardSlotEditor(this);
        slot.setSlot(cardSlot);
        cardEditors.add(slot);
    }

    public void addRelicSlot() {
        PCLRelicSlotEditor editor = new PCLRelicSlotEditor(this);
        screen.trySelectRelic(editor).addCallback((sl) -> {
            if (sl != null && sl.getSelectedRelic() != null) {
                LoadoutRelicSlot newSlot = screen.getCurrentPreset().addRelicSlot(sl.getSelectedRelic().relicId);
                editor.setSlot(newSlot);
                relicsEditors.add(editor);
                updateEditorPositions();
                screen.updateValidation();
            }
        });
    }

    private void addRelicSlotFromData(LoadoutRelicSlot cardSlot) {
        PCLRelicSlotEditor slot = new PCLRelicSlotEditor(this);
        slot.setSlot(cardSlot);
        relicsEditors.add(slot);
    }

    private void deleteCardSlot() {
        if (queuedCardSlot != null && screen.getCurrentPreset().cardSlots.remove(queuedCardSlot.slot)) {
            cardEditors.remove(queuedCardSlot);
            updateEditorPositions();
            screen.updateValidation();
        }
        queuedCardSlot = null;
    }

    private void deleteRelicSlot() {
        if (queuedRelicSlot != null && screen.getCurrentPreset().relicSlots.remove(queuedRelicSlot.slot)) {
            relicsEditors.remove(queuedRelicSlot);
            updateEditorPositions();
            screen.updateValidation();
        }
        queuedRelicSlot = null;
    }

    public void initialize(PCLLoadoutData data) {
        queuedCardSlot = null;
        queuedRelicSlot = null;
        abilityEditors.clear();
        cardEditors.clear();
        relicsEditors.clear();

        for (LoadoutCardSlot slot : data.cardSlots) {
            addCardSlotFromData(slot);
        }
        for (LoadoutRelicSlot slot : data.relicSlots) {
            addRelicSlotFromData(slot);
        }
        for (LoadoutBlightSlot slot : data.blightSlots) {
            addBlightSlotFromData(slot);
        }

        updateEditorPositions();
    }

    @Override
    protected void onScroll(float percent) {
        super.onScroll(percent);
        this.updateEditorPositions();
    }

    public void queueDeleteCardSlot(PCLCardSlotEditor slot) {
        queuedCardSlot = slot;
    }

    public void queueDeleteRelicSlot(PCLRelicSlotEditor slot) {
        queuedRelicSlot = slot;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        deckText.renderImpl(sb);
        relicText.renderImpl(sb);
        addCardButton.renderImpl(sb);
        addRelicButton.renderImpl(sb);
        if (abilityEditors.size() > 0) {
            abilityText.renderImpl(sb);
        }

        for (int i = abilityEditors.size() - 1; i >= 0; i--) {
            abilityEditors.get(i).tryRender(sb);
        }

        for (int i = relicsEditors.size() - 1; i >= 0; i--) {
            relicsEditors.get(i).tryRender(sb);
        }

        for (int i = cardEditors.size() - 1; i >= 0; i--) {
            cardEditors.get(i).tryRender(sb);
        }
    }

    protected void updateEditorPositions() {
        float itemX = BASE_X;
        float curY = BASE_HEIGHT + getScrollDelta();

        deckText.hb.translate(itemX, curY);
        curY -= SLOT_SPACING;

        for (PCLCardSlotEditor editor : cardEditors) {
            editor.hb.translate(itemX, curY);
            editor.updateImpl();
            curY -= SLOT_SPACING;
        }

        curY -= SLOT_SPACING / 2;
        relicText.hb.translate(relicText.hb.x, curY);
        curY -= SLOT_SPACING;

        for (PCLRelicSlotEditor editor : relicsEditors) {
            editor.hb.translate(itemX, curY);
            editor.tryUpdate();
            curY -= SLOT_SPACING;
        }

        curY -= SLOT_SPACING / 2;
        abilityText.hb.translate(relicText.hb.x, curY);
        curY -= SLOT_SPACING;
        itemX += AbstractCard.IMG_WIDTH * 0.2f;

        for (PCLAbilityEditor editor : abilityEditors) {
            editor.hb.translate(itemX, curY);
            editor.tryUpdate();
            curY -= SLOT_SPACING;
        }
        upperScrollBound = AbstractCard.IMG_HEIGHT + Settings.DEFAULT_SCROLL_LIMIT + (cardEditors.size() + relicsEditors.size() + abilityEditors.size()) * BUTTON_SIZE;
    }

    @Override
    public void updateImpl() {
        super.updateImpl();
        deckText.updateImpl();
        relicText.updateImpl();
        abilityText.updateImpl();
        addCardButton.updateImpl();
        addRelicButton.updateImpl();
        for (PCLCardSlotEditor editor : cardEditors) {
            editor.tryUpdate();
        }
        for (PCLRelicSlotEditor editor : relicsEditors) {
            editor.tryUpdate();
        }
        for (PCLAbilityEditor editor : abilityEditors) {
            editor.tryUpdate();
        }
        deleteCardSlot();
        deleteRelicSlot();
    }
}
