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
import extendedui.ui.controls.EUIRelic;
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

// Copied and modified from STS-AnimatorMod
public class PCLRelicSlotEditor extends EUIBase {
    protected static final float CARD_SCALE = 0.75f;
    public static final float SPACING = 64f * Settings.scale;
    public static final float ITEM_HEIGHT = AbstractCard.IMG_HEIGHT * 0.15f;
    protected EUITextBox relicNameText;
    protected EUITextBox relicValueText;
    protected EUIButton changeButton;
    protected EUIButton clearButton;
    protected EUIRelic relicImage;
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

        relicNameText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), new EUIHitbox(relicValueText.hb.x + relicValueText.hb.width + SPACING, cY, AbstractCard.IMG_WIDTH * 1.1f, ITEM_HEIGHT))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontNormal, 1f);

        clearButton = new EUIButton(EUIRM.images.xButton.texture(), new EUIHitbox(relicNameText.hb.x + relicNameText.hb.width, relicNameText.hb.y + 12, 48, 48))
                .setTooltip(PGR.core.strings.loadout_remove, "")
                .setClickDelay(0.02f);
        changeButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new EUIHitbox(clearButton.hb.x + clearButton.hb.width + 16, relicNameText.hb.y + 12, 48, 48))
                .setTooltip(PGR.core.strings.loadout_change, "")
                .setClickDelay(0.02f);

        setSlot(null);
    }

    public ArrayList<LoadoutRelicSlot.Item> getSelectableRelics() {
        final ArrayList<LoadoutRelicSlot.Item> relics = new ArrayList<>();
        for (LoadoutRelicSlot.Item item : this.slot.relics) {
            // Customs should not be treated as locked in this effect
            boolean add = !slot.isIDBanned(item.relic.relicId) && (!item.isLocked() || PCLCustomRelicSlot.get(item.relic.relicId) != null);
            if (add) {
                // Custom relics may incorrectly be marked as not seen
                item.relic.isSeen = true;
                for (PCLRelicSlotEditor slot : loadoutEditor.relicsEditors) {
                    if (slot.slot != this.slot && slot.slot.getRelic() == item.relic) {
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
                return StringUtils.compare(a.relic.name, b.relic.name);
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
        relicNameText.tryRender(sb);
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
                loadoutEditor.trySelectRelic(this);
                return;
            }

            relicNameText.setFontColor(Color.WHITE);
        }
        else {
            relicNameText.setFontColor(Color.GOLD);
        }

        relic = slot.getRelic();
        if (relic != null && this.relicImage != null) {
            relicImage.translate(relicValueText.hb.x + relicValueText.hb.width, relicValueText.hb.y);
            relicImage.updateImpl();
        }

        relicValueText.tryUpdate();

        if (changeButton.isActive) {
            changeButton.updateImpl();
        }
        if (clearButton.isActive) {
            clearButton.setInteractable(slot.canRemove()).updateImpl();
        }
    }

    public PCLRelicSlotEditor setSlot(LoadoutRelicSlot slot) {
        if (slot == null) {
            this.slot = null;
            this.relic = null;
            this.relicNameText.setActive(false);
            this.relicValueText.setActive(false);
            this.changeButton.setActive(false);
            this.clearButton.setActive(false);
            return this;
        }

        final boolean change = slot.relics.size() > 1;

        this.slot = slot;
        this.relic = slot.getRelic();
        this.relicNameText.setLabel(relic != null ? relic.name : "").setActive(true);
        this.relicValueText.setActive(true);
        this.clearButton.setOnClick(() -> {
            this.slot.clear();
            this.relicNameText.setLabel("");
            this.relicImage = null;
            refreshValues();
        }).setInteractable(slot.canRemove()).setActive(relic != null);
        this.changeButton.setOnClick(() -> loadoutEditor.trySelectRelic(this)).setActive(change);
        if (relic != null) {
            this.relicImage = new EUIRelic(relic, new EUIHitbox(relicValueText.hb.x + relicValueText.hb.width + SPACING / 2, relicValueText.hb.y, relic.hb.width, relic.hb.height));
            if (relic instanceof PCLRelic) {
                this.relicImage.setScale(0.65f, 0.65f);
            }
            else {
                this.relicImage.setScale(1.3f, 1.3f);
            }
        }
        else {
            this.relicImage = null;
        }

        refreshValues();
        return this;
    }

    public PCLRelicSlotEditor translate(float cX, float cY) {
        relicValueText.setPosition(cX, cY);
        relicNameText.setPosition(relicValueText.hb.x + relicValueText.hb.width + SPACING, cY);
        clearButton.setPosition(relicNameText.hb.x + relicNameText.hb.width, cY);
        changeButton.setPosition(clearButton.hb.x + clearButton.hb.width + 8, cY);
        if (relic != null && this.relicImage != null) {
            this.relicImage.translate(relicValueText.hb.x + relicValueText.hb.width + SPACING / 2, relicValueText.hb.y);
        }

        return this;
    }
}