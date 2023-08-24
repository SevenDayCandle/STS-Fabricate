package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import extendedui.EUIRM;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.blights.PCLBlight;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.LoadoutBlightSlot;
import pinacolada.resources.pcl.PCLCoreImages;

import java.util.ArrayList;

import static pinacolada.ui.characterSelection.PCLCardSlotEditor.BUTTON_SIZE;
import static pinacolada.ui.characterSelection.PCLCardSlotEditor.ITEM_HEIGHT;

// Copied and modified from STS-AnimatorMod
public class PCLAbilityEditor extends EUIBase {
    protected static final float CARD_SCALE = 0.75f;
    protected EUITextBox relicNameText;
    protected EUIButton changeButton;
    protected EUIButton changeButton2;
    protected EUIImage image;
    protected AbstractBlight item;
    public LoadoutBlightSlot slot;
    public PCLLoadoutScreen loadoutEditor;

    public PCLAbilityEditor(PCLLoadoutScreen loadoutEditor, float cX, float cY) {
        this.loadoutEditor = loadoutEditor;

        relicNameText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(cX, cY, AbstractCard.IMG_WIDTH * 1.1f, ITEM_HEIGHT))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontNormal, 1f);


        final float offY = BUTTON_SIZE / 4;
        changeButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new EUIHitbox(relicNameText.hb.x + relicNameText.hb.width + offY, relicNameText.hb.y + offY, BUTTON_SIZE, BUTTON_SIZE))
                .setTooltip(PGR.core.strings.loadout_change, "")
                .setClickDelay(0.02f);
        changeButton2 = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new EUIHitbox(changeButton.hb.x + changeButton.hb.width + offY, relicNameText.hb.y + offY, BUTTON_SIZE, BUTTON_SIZE))
                .setTooltip(PGR.core.strings.loadout_change, "")
                .setClickDelay(0.02f);

        setSlot(null);
    }

    public ArrayList<LoadoutBlightSlot.Item> getSelectables() {
        final ArrayList<LoadoutBlightSlot.Item> relics = new ArrayList<>();
        for (LoadoutBlightSlot.Item item : this.slot.items) {
            boolean add = true;
            item.item.isSeen = true;
            for (PCLAbilityEditor slot : loadoutEditor.abilityEditors) {
                if (slot.slot != this.slot && slot.slot.getItem() == item.item) {
                    add = false;
                }
            }

            if (add) {
                relics.add(item);
            }
        }
        relics.sort((a, b) -> {
            if (a.estimatedValue == b.estimatedValue) {
                return StringUtils.compare(a.item.name, b.item.name);
            }
            return a.estimatedValue - b.estimatedValue;
        });

        return relics;
    }

    public void refreshValues() {
        int value = slot == null ? 0 : slot.getEstimatedValue();
        loadoutEditor.updateValidation();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        relicNameText.tryRender(sb);
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
        this.slot.next();
        loadoutEditor.setSlotsActive(true);
        refreshValues();
    }

    protected void selectPrev() {
        this.slot.previous();
        loadoutEditor.setSlotsActive(true);
        refreshValues();
    }

    public PCLAbilityEditor setSlot(LoadoutBlightSlot slot) {
        if (slot == null) {
            this.slot = null;
            this.item = null;
            this.relicNameText.setActive(false);
            this.changeButton.setActive(false);
            return this;
        }

        final boolean change = slot.items.size() > 1;

        this.slot = slot;
        this.item = slot.getItem();
        this.relicNameText.setLabel(item != null ? item.name : "").setActive(true);
        this.changeButton.setOnClick(this::selectPrev).setActive(change);
        this.changeButton2.setOnClick(this::selectNext).setActive(change);
        if (item != null) {
            this.image = new EUIImage(item.img, new EUIHitbox(relicNameText.hb.x - item.hb.width, relicNameText.hb.y, item.hb.width, item.hb.height));
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
        return this;
    }

    @Override
    public void updateImpl() {
        if (slot == null) {
            return;
        }
        relicNameText.tryUpdate();

        if (changeButton.isActive && relicNameText.hb.hovered) {
            if (InputHelper.justClickedLeft) {
                relicNameText.hb.clickStarted = true;
            }

            if (relicNameText.hb.clicked) {
                relicNameText.hb.clicked = false;
                loadoutEditor.trySelectAbility(this);
                return;
            }

            relicNameText.setFontColor(Color.WHITE);
        }
        else {
            relicNameText.setFontColor(Color.GOLD);
        }

        item = slot.getItem();
        if (item != null && this.image != null) {
            image.updateImpl();
        }

        changeButton.tryUpdate();
        changeButton2.tryUpdate();
    }
}