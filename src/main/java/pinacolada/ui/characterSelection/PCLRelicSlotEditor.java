package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.LoadoutRelicSlot;
import pinacolada.resources.pcl.PCLCoreImages;

import java.util.ArrayList;

import static pinacolada.ui.characterSelection.PCLCardSlotEditor.BUTTON_SIZE;
import static pinacolada.ui.characterSelection.PCLCardSlotEditor.ITEM_HEIGHT;

// Copied and modified from STS-AnimatorMod
public class PCLRelicSlotEditor extends EUIBase {
    protected static final float CARD_SCALE = 0.75f;
    public static final float SPACING = 64f * Settings.scale;
    protected EUITextBox nameText;
    protected EUITextBox relicValueText;
    protected EUIButton changeButton;
    protected EUIButton clearButton;
    protected EUIImage relicImage;
    protected AbstractRelic relic;
    public LoadoutRelicSlot slot;
    public PCLLoadoutScreen loadoutEditor;

    public PCLRelicSlotEditor(PCLLoadoutScreen loadoutEditor, float cX, float cY) {
        this.loadoutEditor = loadoutEditor;

        relicValueText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(cX, cY, AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT))
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        nameText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(relicValueText.hb.x + relicValueText.hb.width + SPACING, cY, AbstractCard.IMG_WIDTH * 1.1f, ITEM_HEIGHT))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontNormal, 1f);


        final float offY = BUTTON_SIZE / 4;
        clearButton = new EUIButton(EUIRM.images.xButton.texture(), new EUIHitbox(nameText.hb.x + nameText.hb.width, nameText.hb.y + offY, BUTTON_SIZE, BUTTON_SIZE))
                .setTooltip(PGR.core.strings.loadout_remove, "")
                .setClickDelay(0.02f);
        changeButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new EUIHitbox(clearButton.hb.x + clearButton.hb.width + offY, nameText.hb.y + offY, BUTTON_SIZE, BUTTON_SIZE))
                .setTooltip(PGR.core.strings.loadout_change, "")
                .setClickDelay(0.02f);

        setSlot(null);
    }

    public ArrayList<LoadoutRelicSlot.Item> getSelectableRelics() {
        final ArrayList<LoadoutRelicSlot.Item> relics = new ArrayList<>();
        for (LoadoutRelicSlot.Item item : this.slot.items) {
            // Customs should not be treated as locked in this effect
            boolean add = !item.isBanned() && (!item.isLocked() || PCLCustomRelicSlot.get(item.item.relicId) != null);
            if (add) {
                // Custom relics may incorrectly be marked as not seen
                item.item.isSeen = true;
                for (PCLRelicSlotEditor slot : loadoutEditor.relicsEditors) {
                    if (slot.slot != this.slot && slot.slot.getItem() == item.item) {
                        add = false;
                    }
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
        relicValueText.setLabel(value)
                .setFontColor(value == 0 ? Settings.CREAM_COLOR : value < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR);
        loadoutEditor.updateValidation();
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        nameText.tryRender(sb);
        if (nameText.hb.hovered && relic != null) {
            relic.renderTip(sb);
        }
        if (this.relicImage != null) {
            relicImage.renderCentered(sb);
            if (relicImage.hb.hovered && relic != null) {
                relic.renderTip(sb);
            }
        }
        relicValueText.tryRender(sb);
        changeButton.tryRender(sb);
        clearButton.tryRender(sb);
    }

    public PCLRelicSlotEditor setSlot(LoadoutRelicSlot slot) {
        if (slot == null) {
            this.slot = null;
            this.relic = null;
            this.nameText.setActive(false);
            this.relicValueText.setActive(false);
            this.changeButton.setActive(false);
            this.clearButton.setActive(false);
            return this;
        }

        final boolean change = slot.items.size() > 1;

        this.slot = slot;
        this.relic = slot.getItem();
        this.nameText.setLabel(relic != null ? relic.name : "").setActive(true);
        this.relicValueText.setActive(true);
        this.clearButton.setOnClick(() -> {
            this.slot.clear();
            this.nameText.setLabel("");
            this.relicImage = null;
            refreshValues();
        }).setInteractable(slot.canRemove()).setActive(relic != null);
        this.changeButton.setOnClick(() -> loadoutEditor.trySelectRelic(this)).setActive(change);
        if (relic != null) {
            this.relicImage = new EUIImage(relic.img, new EUIHitbox(relicValueText.hb.x + relicValueText.hb.width, relicValueText.hb.y, relic.hb.width, relic.hb.height));
            if (relic instanceof PCLRelic) {
                this.relicImage.setScale(0.7f, 0.7f);
            }
            else {
                this.relicImage.setScale(1.4f, 1.4f);
            }
        }
        else {
            this.relicImage = null;
        }

        refreshValues();
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
                loadoutEditor.trySelectRelic(this);
                return;
            }

            nameText.setFontColor(Color.WHITE);
        }
        else {
            nameText.setFontColor(Color.GOLD);
        }

        relic = slot.getItem();
        if (relic != null && this.relicImage != null) {
            relicImage.updateImpl();
        }

        relicValueText.tryUpdate();

        changeButton.tryUpdate();
        if (clearButton.isActive) {
            clearButton.setInteractable(slot.canRemove()).updateImpl();
        }
    }
}