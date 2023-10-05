package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRM;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.blights.PCLBlight;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.LoadoutBlightSlot;

import java.util.ArrayList;

import static pinacolada.ui.characterSelection.PCLCardSlotEditor.ITEM_HEIGHT;
import static pinacolada.ui.characterSelection.PCLLoadoutCanvas.BUTTON_SIZE;

// Copied and modified from STS-AnimatorMod
public class PCLAbilityEditor extends EUIHoverable {
    protected static final float CARD_SCALE = 0.75f;
    protected EUITextBox nameText;
    protected EUIButton changeButton;
    protected EUIButton changeButton2;
    protected EUIImage image;
    protected AbstractBlight item;
    public LoadoutBlightSlot slot;
    public PCLLoadoutCanvas canvas;

    public PCLAbilityEditor(PCLLoadoutCanvas canvas) {
        super(new EUIHitbox(AbstractCard.IMG_WIDTH * 1.1f, ITEM_HEIGHT));
        this.canvas = canvas;

        nameText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), hb)
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontNormal, 1f);


        final float offY = BUTTON_SIZE / 4;
        changeButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new OriginRelativeHitbox(nameText.hb, BUTTON_SIZE, BUTTON_SIZE, nameText.hb.width, BUTTON_SIZE / 4))
                .setTooltip(PGR.core.strings.loadout_change, "")
                .setClickDelay(0.02f);
        changeButton2 = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new OriginRelativeHitbox(changeButton.hb, BUTTON_SIZE, BUTTON_SIZE, changeButton.hb.width, 0))
                .setTooltip(PGR.core.strings.loadout_change, "")
                .setClickDelay(0.02f);
    }

    public ArrayList<String> getAvailableAbilitiesForSelection() {
        ArrayList<String> abilities = canvas.screen.loadout.getAvailableBlightIDs();
        abilities.removeIf(a -> {
                    for (PCLAbilityEditor editor : canvas.abilityEditors) {
                        if (editor.slot != this.slot && a.equals(editor.slot.selected)) {
                            return true;
                        }
                    }
                    return false;
                }
        );
        return abilities;
    }

    protected void onSelect() {
        this.item = BlightHelper.getBlight(slot.selected);
        this.nameText.setLabel(item != null ? item.name : "").setActive(true);
        this.changeButton.setOnClick(this::selectPrev).setActive(true);
        this.changeButton2.setOnClick(this::selectNext).setActive(true);
        if (item != null) {
            this.image = new EUIImage(item.img, new OriginRelativeHitbox(nameText.hb, item.hb.width, item.hb.height, -item.hb.width, 0));
            if (item instanceof PCLBlight) {
                this.image.setScale(0.7f, 0.7f);
            }
            else {
                this.image.setScale(1.4f, 1.4f);
            }
        }
        else {
            this.image = null;
        }
        refreshValues();
    }

    public void refreshValues() {
        int value = slot == null ? 0 : slot.getEstimatedValue();
        canvas.screen.updateValidation();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        nameText.tryRender(sb);
        if (nameText.hb.hovered && item != null) {
            item.renderTip(sb);
        }
        if (this.image != null) {
            image.renderCentered(sb);
            if (image.hb.hovered && item != null) {
                item.renderTip(sb);
            }
        }
        changeButton.tryRender(sb);
        changeButton2.tryRender(sb);
    }

    protected void selectNext() {
        ArrayList<String> abilities = canvas.screen.loadout.getAvailableBlightIDs();
        int base = abilities.indexOf(slot.selected);
        int index = base;
        String abAt = null;
        do {
            index = (abilities.indexOf(slot.selected) + 1) % abilities.size();
            abAt = abilities.get(index);
        }
        while (index != base && slot.selected.equals(abAt));
        this.slot.select(abAt);
        onSelect();
        canvas.screen.updateValidation();
    }

    protected void selectPrev() {
        ArrayList<String> abilities = canvas.screen.loadout.getAvailableBlightIDs();
        int base = abilities.indexOf(slot.selected);
        int index = base;
        String abAt = null;
        do {
            index = abilities.indexOf(slot.selected) - 1;
            if (index < 0) {
                index = abilities.size() - 1;
            }
            abAt = abilities.get(index);
        }
        while (index != base && slot.selected.equals(abAt));
        this.slot.select(abAt);
        onSelect();
        canvas.screen.updateValidation();
    }

    public PCLAbilityEditor setSlot(LoadoutBlightSlot slot) {
        if (slot == null) {
            this.slot = null;
            this.item = null;
            this.nameText.setActive(false);
            this.changeButton.setActive(false);
            return this;
        }

        this.slot = slot;
        onSelect();
        return this;
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
                canvas.screen.trySelectAbility(this);
                return;
            }

            nameText.setFontColor(Color.WHITE);
        }
        else {
            nameText.setFontColor(Color.GOLD);
        }

        if (item != null && this.image != null) {
            image.updateImpl();
        }

        changeButton.tryUpdate();
        changeButton2.tryUpdate();
    }
}