package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRM;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.LoadoutCardSlot;
import pinacolada.resources.pcl.PCLCoreImages;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class PCLCardSlotEditor extends EUIBase {
    protected static final float CARD_SCALE = 0.75f;
    public static final float BUTTON_SIZE = scale(42);
    public static final float PREVIEW_OFFSET_X = AbstractCard.IMG_WIDTH * 0.6f;
    public static final float PREVIEW_OFFSET_Y = -AbstractCard.IMG_HEIGHT * 0.57f;
    public static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    protected EUITextBox cardnameText;
    protected EUITextBox cardvalueText;
    protected EUITextBox cardamountText;
    protected EUIButton addButton;
    protected EUIButton decrementButton;
    protected EUIButton changeButton;
    protected EUIButton clearButton;
    protected AbstractCard card;
    protected Color nameColor;
    public LoadoutCardSlot slot;
    public PCLLoadoutScreen loadoutEditor;

    public PCLCardSlotEditor(PCLLoadoutScreen loadoutEditor, float cX, float cY) {
        this.loadoutEditor = loadoutEditor;

        cardvalueText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(cX, cY, AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT))
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        cardamountText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(cardvalueText.hb.x + cardvalueText.hb.width, cY, AbstractCard.IMG_HEIGHT * 0.15f, ITEM_HEIGHT))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontNormal, 1f);

        cardnameText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(cardamountText.hb.x + cardamountText.hb.width, cY, AbstractCard.IMG_WIDTH * 1.1f, ITEM_HEIGHT))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontNormal, 1f);

        final float offY = BUTTON_SIZE / 4;
        decrementButton = new EUIButton(EUIRM.images.minus.texture(), new EUIHitbox(cardnameText.hb.x + cardnameText.hb.width, cardnameText.hb.y + offY, BUTTON_SIZE, BUTTON_SIZE))
                .setTooltip(PGR.core.strings.loadout_decrease, "")
                .setClickDelay(0.02f);
        addButton = new EUIButton(EUIRM.images.plus.texture(), new EUIHitbox(decrementButton.hb.x + decrementButton.hb.width + offY, cardnameText.hb.y + offY, BUTTON_SIZE, BUTTON_SIZE))
                .setTooltip(PGR.core.strings.loadout_add, "")
                .setClickDelay(0.02f);
        clearButton = new EUIButton(EUIRM.images.xButton.texture(), new EUIHitbox(addButton.hb.x + addButton.hb.width + offY, cardnameText.hb.y + offY, BUTTON_SIZE, BUTTON_SIZE))
                .setTooltip(PGR.core.strings.loadout_remove, "")
                .setClickDelay(0.02f);
        changeButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new EUIHitbox(clearButton.hb.x + clearButton.hb.width + offY, cardnameText.hb.y + offY, BUTTON_SIZE, BUTTON_SIZE))
                .setTooltip(PGR.core.strings.loadout_change, "")
                .setClickDelay(0.02f);
        nameColor = Settings.GOLD_COLOR;

        setSlot(null);
    }

    public ArrayList<LoadoutCardSlot.Item> getSelectableCards() {
        final ArrayList<LoadoutCardSlot.Item> cards = new ArrayList<>();
        for (LoadoutCardSlot.Item item : this.slot.items) {
            // Custom cards should not be treated as locked in this effect
            boolean add = !item.isBanned() && (!item.isLocked() || PCLCustomCardSlot.get(item.item) != null);
            if (add) {
                for (PCLCardSlotEditor slot : loadoutEditor.cardEditors) {
                    if (slot.slot != this.slot && item.item.equals(slot.slot.getSelectedID()) && slot.slot.amount > 0) {
                        add = false;
                        break;
                    }
                }
            }

            if (add) {
                cards.add(item);
            }
        }

        cards.sort((a, b) -> {
            if (a.estimatedValue == b.estimatedValue) {
                return StringUtils.compare(a.getCard(false).name, b.getCard(false).name);
            }
            return a.estimatedValue - b.estimatedValue;
        });

        return cards;
    }

    public void refreshValues() {
        int value = slot == null ? 0 : slot.getEstimatedValue();
        cardvalueText.setLabel(value)
                .setFontColor(value == 0 ? Settings.CREAM_COLOR : value < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR);

        loadoutEditor.updateValidation();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        cardnameText.tryRender(sb);
        cardvalueText.tryRender(sb);
        cardamountText.tryRender(sb);
        addButton.tryRender(sb);
        decrementButton.tryRender(sb);
        changeButton.tryRender(sb);
        clearButton.tryRender(sb);
        if (cardnameText.hb.hovered && card != null) {
            card.renderInLibrary(sb);
            card.renderCardTip(sb);
        }
    }

    public PCLCardSlotEditor setSlot(LoadoutCardSlot slot) {
        if (slot == null) {
            this.slot = null;
            this.card = null;
            this.cardamountText.setActive(false);
            this.cardnameText.setActive(false);
            this.cardvalueText.setActive(false);
            this.addButton.setActive(false);
            this.decrementButton.setActive(false);
            this.changeButton.setActive(false);
            this.clearButton.setActive(false);
            return this;
        }

        final boolean add = card != null && slot.max > 1;
        final boolean change = slot.items.size() > 1;
        final boolean remove = card != null && slot.max > slot.min;

        this.slot = slot;
        this.card = slot.getCard(true);
        this.cardnameText.setLabel(card != null ? card.name : "").setActive(true);
        this.cardvalueText.setActive(true);
        this.cardamountText.setActive(card != null);
        this.addButton.setOnClick(() -> {
            this.slot.add();
            refreshValues();
        }).setInteractable(slot.canAdd()).setActive(true);
        this.decrementButton.setOnClick(() -> {
            this.slot.decrement();
            refreshValues();
        }).setInteractable(slot.canDecrement()).setActive(true);
        this.clearButton.setOnClick(() -> {
            this.slot.clear();
            this.cardnameText.setLabel("");
            refreshValues();
        }).setInteractable(slot.canRemove()).setActive(true);
        this.changeButton.setOnClick(() -> loadoutEditor.trySelectCard(this)).setInteractable(change).setActive(true);
        this.nameColor = card != null && slot.isIDBanned(card.cardID) ? Settings.RED_TEXT_COLOR : Settings.GOLD_COLOR;
        cardamountText.setFontColor(this.nameColor == Settings.RED_TEXT_COLOR ? Settings.RED_TEXT_COLOR : Settings.CREAM_COLOR);

        refreshValues();
        return this;
    }

    @Override
    public void updateImpl() {
        if (slot == null) {
            return;
        }
        cardnameText.tryUpdate();

        if (changeButton.isActive && cardnameText.hb.hovered && slot.items.size() > 1) {
            if (InputHelper.justClickedLeft) {
                cardnameText.hb.clickStarted = true;
            }

            if (cardnameText.hb.clicked) {
                cardnameText.hb.clicked = false;
                loadoutEditor.trySelectCard(this);
                return;
            }

            cardnameText.setFontColor(Color.WHITE);
        }
        else {
            cardnameText.setFontColor(nameColor);
        }

        card = slot.getCard(false);
        if (card != null) {
            card.current_x = card.target_x = card.hb.x = InputHelper.mX + PREVIEW_OFFSET_X;
            card.current_y = card.target_y = card.hb.y = InputHelper.mY + PREVIEW_OFFSET_Y;
            card.update();
            card.updateHoverLogic();
            card.drawScale = card.targetDrawScale = CARD_SCALE * ((card.hb.hovered) ? 0.97f : 0.95f);
            cardamountText.setLabel(slot.amount + "x ").updateImpl();
        }
        else {
            cardamountText.setLabel("").updateImpl();
        }

        cardvalueText.tryUpdate();

        if (addButton.isActive) {
            addButton.setInteractable(slot.canAdd()).updateImpl();
        }
        if (decrementButton.isActive) {
            decrementButton.setInteractable(slot.canDecrement()).updateImpl();
        }
        if (changeButton.isActive) {
            changeButton.updateImpl();
        }
        if (clearButton.isActive) {
            clearButton.setInteractable(slot.canRemove()).updateImpl();
        }
    }
}