package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRM;
import extendedui.ui.EUIBase;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.OriginRelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import org.apache.commons.lang3.StringUtils;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.LoadoutCardSlot;
import pinacolada.resources.loadout.LoadoutRelicSlot;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

import static pinacolada.ui.characterSelection.PCLCardSlotEditor.ITEM_HEIGHT;
import static pinacolada.ui.characterSelection.PCLLoadoutCanvas.BUTTON_SIZE;

// Copied and modified from STS-AnimatorMod
public class PCLRelicSlotEditor extends EUIHoverable {
    protected static final float CARD_SCALE = 0.75f;
    public static final float SPACING = 64f * Settings.scale;
    protected EUITextBox nameText;
    protected EUITextBox relicValueText;
    protected EUIButton changeButton;
    protected EUIButton clearButton;
    protected EUIImage relicImage;
    protected AbstractRelic relic;
    public LoadoutRelicSlot slot;
    protected PCLLoadoutCanvas canvas;

    public PCLRelicSlotEditor(PCLLoadoutCanvas canvas) {
        super(new EUIHitbox(AbstractCard.IMG_WIDTH * 0.2f, ITEM_HEIGHT));
        this.canvas = canvas;

        relicValueText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(), hb)
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.1f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontSmall, 1f);

        nameText = new EUITextBox(EUIRM.images.panelRoundedHalfH.texture(),new OriginRelativeHitbox(hb,AbstractCard.IMG_WIDTH * 1.1f, ITEM_HEIGHT, hb.width + SPACING, 0))
                .setColors(Settings.HALF_TRANSPARENT_BLACK_COLOR, Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardTitleFontNormal, 1f);

        clearButton = new EUIButton(EUIRM.images.xButton.texture(), new OriginRelativeHitbox(nameText.hb, BUTTON_SIZE, BUTTON_SIZE, nameText.hb.width, BUTTON_SIZE / 4))
                .setOnClick(() -> {
                    canvas.queueDeleteRelicSlot(this);
                })
                .setTooltip(PGR.core.strings.loadout_remove, "")
                .setClickDelay(0.02f);
        changeButton = new EUIButton(PCLCoreImages.Menu.edit.texture(), new OriginRelativeHitbox(clearButton.hb, BUTTON_SIZE, BUTTON_SIZE, clearButton.hb.width, 0))
                .setOnClick(this::trySelect)
                .setTooltip(PGR.core.strings.loadout_change, "")
                .setClickDelay(0.02f);
    }

    public ArrayList<String> getAvailableRelics() {
        final ArrayList<String> relics = new ArrayList<>();

        for (String relicID : canvas.screen.loadout.getAvailableRelicIDs()) {
            boolean add = isRelicAllowed(relicID);
            if (add) {
                for (PCLRelicSlotEditor editor : canvas.relicsEditors) {
                    if (editor.slot != this.slot && relicID.equals(editor.slot.selected)) {
                        add = false;
                        break;
                    }
                }
            }

            if (add && RelicLibrary.getRelic(relicID) != null) {
                relics.add(relicID);
            }
        }

        relics.sort((a, b) -> {
            int aEst = LoadoutRelicSlot.getLoadoutValue(a);
            if (aEst < 0) {
                aEst = -aEst * 1000;
            }
            int bEst = LoadoutRelicSlot.getLoadoutValue(b);
            if (bEst < 0) {
                bEst = -bEst * 1000;
            }
            return aEst - bEst;
        });

        return relics;
    }

    protected boolean isRelicAllowed(String id) {
        return !canvas.screen.loadout.isRelicBanned(id) && (!GameUtilities.isRelicLocked(id) || PCLCustomRelicSlot.get(id) != null);
    }

    private void onSelect() {
        this.relic = RelicLibrary.getRelic(slot.selected);
        this.nameText.setLabel(relic != null ? relic.name : "");
        this.clearButton.setInteractable(slot.canRemove());
        if (relic != null) {
            this.relicImage = new EUIImage(relic.img, new OriginRelativeHitbox(relicValueText.hb,relic.hb.width, relic.hb.height, relicValueText.hb.width,0));
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
    }

    public void refreshValues() {
        int value = slot == null ? 0 : slot.getEstimatedValue();
        relicValueText.setLabel(value)
                .setFontColor(value == 0 ? Settings.CREAM_COLOR : value < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR);
        canvas.screen.updateValidation();
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

    protected void trySelect() {
        canvas.screen.trySelectRelic(this).addCallback((ef) ->
        {
            if (ef != null && ef.getSelectedRelic() != null) {
                slot.select(ef.getSelectedRelic().relicId);
                onSelect();
            }
        });
    }

    public PCLRelicSlotEditor setSlot(LoadoutRelicSlot slot) {
        if (slot == null) {
            canvas.queueDeleteRelicSlot(this);
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
                canvas.screen.trySelectRelic(this);
                return;
            }

            nameText.setFontColor(Color.WHITE);
        }
        else {
            nameText.setFontColor(Color.GOLD);
        }

        if (relic != null && this.relicImage != null) {
            relicImage.updateImpl();
        }

        relicValueText.tryUpdate();

        clearButton.setInteractable(slot.canRemove()).updateImpl();
        changeButton.tryUpdate();
    }
}