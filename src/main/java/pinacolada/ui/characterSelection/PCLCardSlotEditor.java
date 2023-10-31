package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRM;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.LoadoutCardSlot;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

import static pinacolada.ui.characterSelection.PCLLoadoutCanvas.BUTTON_SIZE;

// Copied and modified from STS-AnimatorMod
public class PCLCardSlotEditor extends EUIHoverable {
    protected static final float CARD_SCALE = 0.75f;
    protected static final float PREVIEW_OFFSET_X = AbstractCard.IMG_WIDTH * 0.6f;
    protected static final float PREVIEW_OFFSET_Y = -AbstractCard.IMG_HEIGHT * 0.57f;
    protected static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    protected EUITextBox nameText;
    protected EUITextBox cardvalueText;
    protected EUITextBox cardamountText;
    protected EUIButton addButton;
    protected EUIButton decrementButton;
    protected EUIButton changeButton;
    protected EUIButton clearButton;
    protected AbstractCard card;
    protected Color nameColor;
    protected PCLLoadoutCanvas canvas;
    public LoadoutCardSlot slot;

    public PCLCardSlotEditor(PCLLoadoutCanvas canvas) {
        super(new EUIHitbox(AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT));
        this.canvas = canvas;

        cardvalueText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), hb)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        cardamountText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new OriginRelativeHitbox(hb, AbstractCard.IMG_HEIGHT * 0.15f, ITEM_HEIGHT, hb.width, 0))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontNormal, 1f);

        nameText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new OriginRelativeHitbox(cardamountText.hb, AbstractCard.IMG_WIDTH * 1.1f, ITEM_HEIGHT, cardamountText.hb.width, 0))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontNormal, 1f);

        decrementButton = new EUIButton(EUIRM.images.minus.texture(), new OriginRelativeHitbox(nameText.hb, BUTTON_SIZE, BUTTON_SIZE, nameText.hb.width, BUTTON_SIZE / 4))
                .setOnClick(() -> {
                    this.slot.decrement();
                    refreshValues();
                })
                .setTooltip(PGR.core.strings.loadout_decrease, "");
        addButton = new EUIButton(EUIRM.images.plus.texture(), new OriginRelativeHitbox(decrementButton.hb, BUTTON_SIZE, BUTTON_SIZE, decrementButton.hb.width, 0))
                .setOnClick(() -> {
                    this.slot.add();
                    refreshValues();
                })
                .setTooltip(PGR.core.strings.loadout_add, "");
        clearButton = new EUIButton(EUIRM.images.xButton.texture(), new OriginRelativeHitbox(addButton.hb, BUTTON_SIZE, BUTTON_SIZE, addButton.hb.width, 0))
                .setOnClick(() -> {
                    canvas.queueDeleteCardSlot(this);
                })
                .setTooltip(PGR.core.strings.loadout_remove, "");
        changeButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new OriginRelativeHitbox(clearButton.hb, BUTTON_SIZE, BUTTON_SIZE, clearButton.hb.width, 0))
                .setOnClick(this::trySelect)
                .setTooltip(PGR.core.strings.loadout_change, "");
        nameColor = Settings.GOLD_COLOR;
    }

    public ArrayList<String> getAvailableCards() {
        final ArrayList<String> cards = new ArrayList<>();

        for (String cardID : canvas.screen.loadout.getAvailableCardIDs()) {
            boolean add = isCardAllowed(cardID);
            if (add) {
                for (PCLCardSlotEditor editor : canvas.cardEditors) {
                    if (editor.slot != this.slot && cardID.equals(editor.slot.selected) && editor.slot.getAmount() > 0) {
                        add = false;
                        break;
                    }
                }
            }

            if (add && CardLibrary.getCard(cardID) != null) {
                cards.add(cardID);
            }
        }

        cards.sort(LoadoutCardSlot::getLoadoutCardSort);

        return cards;
    }

    protected boolean isCardAllowed(String id) {
        return !canvas.screen.loadout.isCardBanned(id) && !GameUtilities.isCardLocked(id);
    }

    private void onSelect() {
        this.card = CardLibrary.getCard(slot.selected);
        if (this.card instanceof PCLCard) {
            ((PCLCard) this.card).affinities.updateSortedList();
        }
        this.nameText.setLabel(card != null ? card.name : "").setActive(true);
        this.cardamountText.setActive(card != null);
        this.addButton.setInteractable(slot.canAdd());
        this.decrementButton.setInteractable(slot.canDecrement());
        this.clearButton.setInteractable(slot.canRemove());
        this.changeButton.setInteractable(true);
        this.nameColor = card != null && slot.isBanned() ? Settings.RED_TEXT_COLOR : Settings.GOLD_COLOR;
        cardamountText.setFontColor(this.nameColor == Settings.RED_TEXT_COLOR ? Settings.RED_TEXT_COLOR : Settings.CREAM_COLOR);
        refreshValues();
    }

    public void refreshValues() {
        int value = slot == null ? 0 : slot.getEstimatedValue();
        cardvalueText.setLabel(value)
                .setFontColor(value == 0 ? Settings.CREAM_COLOR : value < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR);

        canvas.screen.updateValidation();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        nameText.tryRender(sb);
        cardvalueText.tryRender(sb);
        cardamountText.tryRender(sb);
        addButton.tryRender(sb);
        decrementButton.tryRender(sb);
        changeButton.tryRender(sb);
        clearButton.tryRender(sb);
        if (nameText.hb.hovered && card != null) {
            card.renderInLibrary(sb);
            card.renderCardTip(sb);
        }
    }

    public PCLCardSlotEditor setSlot(LoadoutCardSlot slot) {
        if (slot == null) {
            canvas.queueDeleteCardSlot(this);
            return this;
        }

        this.slot = slot;
        onSelect();
        return this;
    }

    protected void trySelect() {
        canvas.screen.trySelectCard(this).addCallback((ef) -> {
            if (ef != null && ef.getSelectedCard() != null) {
                slot.select(ef.getSelectedCard().cardID, 1);
                onSelect();
            }
        });
    }

    @Override
    public void updateImpl() {
        if (slot == null) {
            return;
        }
        nameText.tryUpdate();

        if (changeButton.isActive && nameText.hb.hovered) {
            if (InputHelper.justClickedLeft) {
                nameText.hb.clickStarted = true;
            }

            if (nameText.hb.clicked) {
                nameText.hb.clicked = false;
                trySelect();
                return;
            }

            nameText.setFontColor(Color.WHITE);
        }
        else {
            nameText.setFontColor(nameColor);
        }

        if (card != null) {
            card.current_x = card.target_x = card.hb.x = InputHelper.mX + PREVIEW_OFFSET_X;
            card.current_y = card.target_y = card.hb.y = InputHelper.mY + PREVIEW_OFFSET_Y;
            card.update();
            card.updateHoverLogic();
            card.drawScale = card.targetDrawScale = CARD_SCALE * ((card.hb.hovered) ? 0.97f : 0.95f);
            cardamountText.setLabel(slot.getAmount() + "x ").updateImpl();
        }
        else {
            cardamountText.setLabel("").updateImpl();
        }

        cardvalueText.tryUpdate();

        decrementButton.setInteractable(slot.canDecrement()).updateImpl();
        addButton.setInteractable(slot.canAdd()).updateImpl();
        clearButton.setInteractable(slot.canRemove()).updateImpl();
        changeButton.updateImpl();
    }
}