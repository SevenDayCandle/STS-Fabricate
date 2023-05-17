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
import pinacolada.resources.loadout.PCLCardSlot;
import pinacolada.resources.pcl.PCLCoreImages;

// Copied and modified from STS-AnimatorMod
public class PCLCardSlotEditor extends EUIBase {
    protected static final float CARD_SCALE = 0.75f;
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
    public PCLCardSlot slot;
    public PCLLoadoutEditor loadoutEditor;

    public PCLCardSlotEditor(PCLLoadoutEditor loadoutEditor, float cX, float cY) {
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

        decrementButton = new EUIButton(EUIRM.images.minus.texture(), new EUIHitbox(cardnameText.hb.x + cardnameText.hb.width, cardnameText.hb.y + 12, 48, 48))
                .setClickDelay(0.02f);
        addButton = new EUIButton(EUIRM.images.plus.texture(), new EUIHitbox(decrementButton.hb.x + decrementButton.hb.width + 16, cardnameText.hb.y + 12, 48, 48))
                .setClickDelay(0.02f);
        clearButton = new EUIButton(EUIRM.images.x.texture(), new EUIHitbox(addButton.hb.x + addButton.hb.width + 16, cardnameText.hb.y + 12, 48, 48))
                .setClickDelay(0.02f);
        changeButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new EUIHitbox(clearButton.hb.x + clearButton.hb.width + 16, cardnameText.hb.y + 12, 48, 48))
                .setClickDelay(0.02f);

        setSlot(null);
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

    @Override
    public void updateImpl() {
        if (slot == null) {
            return;
        }
        cardnameText.tryUpdate();

        if (changeButton.isActive && cardnameText.hb.hovered) {
            if (InputHelper.justClickedLeft) {
                cardnameText.hb.clickStarted = true;
            }

            if (cardnameText.hb.clicked) {
                cardnameText.hb.clicked = false;
                loadoutEditor.trySelectCard(this.slot);
                return;
            }

            cardnameText.setFontColor(Color.WHITE);
        }
        else {
            cardnameText.setFontColor(Color.GOLD);
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

        int value = slot.getEstimatedValue();
        cardvalueText.setLabel(value)
                .setFontColor(value == 0 ? Settings.CREAM_COLOR : value < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR)
                .tryUpdate();

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

    public PCLCardSlotEditor setSlot(PCLCardSlot slot) {
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
        final boolean change = slot.cards.size() > 1;
        final boolean remove = card != null && slot.max > slot.min;

        this.slot = slot;
        this.card = slot.getCard(true);
        this.cardnameText.setLabel(card != null ? card.name : "").setActive(true);
        this.cardvalueText.setActive(true);
        this.cardamountText.setActive(card != null);
        this.addButton.setOnClick(this.slot::add).setInteractable(slot.canAdd()).setActive(true);
        this.decrementButton.setOnClick(this.slot::decrement).setInteractable(slot.canDecrement()).setActive(true);
        this.clearButton.setOnClick(() -> {
            this.slot.clear();
            this.cardnameText.setLabel("");
        }).setInteractable(slot.canRemove()).setActive(true);
        this.changeButton.setOnClick(() -> loadoutEditor.trySelectCard(this.slot)).setInteractable(change).setActive(true);

        return this;
    }

    public PCLCardSlotEditor translate(float cX, float cY) {
        cardvalueText.setPosition(cX, cY);
        cardamountText.setPosition(cardvalueText.hb.x + cardvalueText.hb.width, cY);
        cardnameText.setPosition(cardamountText.hb.x + cardamountText.hb.width, cY);
        decrementButton.setPosition(cardnameText.hb.x + cardnameText.hb.width, cY + 12);
        addButton.setPosition(decrementButton.hb.x + decrementButton.hb.width + 16, cY + 12);
        clearButton.setPosition(addButton.hb.x + addButton.hb.width + 16, cY + 12);
        changeButton.setPosition(clearButton.hb.x + clearButton.hb.width + 16, cY + 12);

        return this;
    }
}