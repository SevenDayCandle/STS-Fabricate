package pinacolada.ui.characterSelection;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIDropdown;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.resources.loadout.PCLLoadoutData;

// Copied and modified from STS-AnimatorMod
public class PCLBaseStatEditor extends EUIBase {
    public static final float ICON_SIZE = 64f * Settings.scale;
    protected boolean interactable;
    protected Hitbox hb;
    protected EUIImage image;
    protected EUILabel label;
    protected EUIButton decreaseButton;
    protected EUIButton increaseButton;
    protected EUIDropdown<Integer> valueDropdown;
    protected PCLLoadoutEditor editor;
    public StatType type;
    public PCLLoadout loadout;
    public PCLLoadoutData data;

    public PCLBaseStatEditor(StatType type, float cX, float cY, PCLLoadoutEditor editor) {
        this.type = type;
        this.hb = new EUIHitbox(0, 0, ICON_SIZE * 2.5f, ICON_SIZE).setCenter(cX, cY);
        this.editor = editor;

        final float w = hb.width;
        final float h = hb.height;

        image = new EUIImage(type.icon, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.13f, h * 0.5f));
        label = new EUILabel(FontHelper.tipHeaderFont, new RelativeHitbox(hb, w - ICON_SIZE, h, w * 0.5f + ICON_SIZE * -0.13f, h * 0.5f))
                .setAlignment(0.5f, 0f, false)
                .setColor(type.labelColor);

        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, ICON_SIZE * 0.9f, ICON_SIZE * 0.9f, -(ICON_SIZE * 0.5f), h * -0.15f))
                .setOnClick(this::decrease)
                .setText(null);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, ICON_SIZE * 0.9f, ICON_SIZE * 0.9f, w + (ICON_SIZE * 0.5f), h * -0.15f))
                .setOnClick(this::increase)
                .setText(null);

        valueDropdown = new EUIDropdown<Integer>(RelativeHitbox.fromPercentages(hb, 0.85f, 0.75f, 0.5f, -0.2f))
                .setFontForButton(EUIFontHelper.cardTitleFontSmall, 1f)
                .setOnOpenOrClose(isOpen -> {
                    editor.activeEditor = isOpen ? this : null;
                })
                .setOnChange(value -> {
                    if (value.size() > 0) {
                        set(value.get(0));
                    }
                })
                .setCanAutosizeButton(false)
                .setLabelFunctionForOption(
                        value -> this.type.getAmountForValue(value) + " (" + value + ")", false
                )
                .setLabelColorFunctionForButton(value -> {
                    if (value.isEmpty()) {
                        return Settings.CREAM_COLOR;
                    }
                    int first = value.get(0);
                    return first == 0 ? Settings.CREAM_COLOR : first < 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR;
                })
                .setLabelFunctionForButton(
                        (value, __) -> {
                            if (value.isEmpty()) {
                                return String.valueOf(0);
                            }
                            return String.valueOf(value.get(0));
                        },

                        false)
                .setItems(EUIUtils.range(type.minValue, type.maxValue, type.valuePerStep));
    }

    public boolean canDecrease() {
        return valueDropdown.getCurrentIndex() > 0;
    }

    public boolean canIncrease() {
        return valueDropdown.getCurrentIndex() < valueDropdown.rows.size() - 1;
    }

    public void decrease() {
        valueDropdown.setSelectionIndices(new int[]{valueDropdown.getCurrentIndex() - 1}, true);
    }

    public void increase() {
        valueDropdown.setSelectionIndices(new int[]{valueDropdown.getCurrentIndex() + 1}, true);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        image.renderImpl(sb);
        label.renderImpl(sb);
        decreaseButton.renderImpl(sb);
        increaseButton.renderImpl(sb);
        valueDropdown.tryRender(sb);
    }

    @Override
    public void updateImpl() {
        hb.update();
        image.updateImpl();
        label.setLabel(type.getText(loadout, data)).updateImpl();
        decreaseButton.setInteractable(interactable && canDecrease()).updateImpl();
        increaseButton.setInteractable(interactable && canIncrease()).updateImpl();
        valueDropdown.tryUpdate();
    }

    public void set(int amount) {
        type.setAmount(data, amount);
        editor.updateValidation();
    }

    public PCLBaseStatEditor setInteractable(boolean interactable) {
        this.interactable = interactable;

        return this;
    }

    public void setLoadout(PCLLoadout loadout, PCLLoadoutData data) {
        this.loadout = loadout;
        this.data = data;
        valueDropdown.setSelection(data.values.getOrDefault(type, 0), true);
    }

    public enum StatType {
        HP(ImageMaster.TP_HP, Settings.RED_TEXT_COLOR, 2, 1, -6, 6, 0),
        Gold(ImageMaster.TP_GOLD, Settings.GOLD_COLOR, 15, 1, -6, 6, 0),
        PotionSlot(ImageMaster.POTION_PLACEHOLDER, Settings.CREAM_COLOR, 1, 15, -2, 2, 12),
        OrbSlot(ImageMaster.ORB_SLOT_1, Settings.CREAM_COLOR, 1, 20, -3, 1, 12),
        CardDraw(ImageMaster.TINY_CARD_BACKGROUND, Settings.CREAM_COLOR, 1, 25, -1, 1, 12),
        Energy(ImageMaster.ENERGY_RED_LAYER1, Settings.CREAM_COLOR, 1, 30, -1, 1, 12);

        public final Texture icon;
        public final Color labelColor;
        public final int amountPerStep;
        public final int valuePerStep;
        public final int minValue;
        public final int maxValue;
        public final int unlockLevel;

        StatType(Texture icon, Color labelColor, int amountPerStep, int valuePerStep, int minValue, int maxValue, int unlockLevel) {
            this.icon = icon;
            this.labelColor = labelColor;
            this.amountPerStep = amountPerStep;
            this.valuePerStep = valuePerStep;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.unlockLevel = unlockLevel;
        }

        public int getAmount(PCLLoadout loadout, PCLLoadoutData data) {
            return getBase(loadout) + getAmountForValue(data);
        }

        public int getAmountForValue(PCLLoadoutData data) {
            return getAmountForValue(data != null ? data.values.getOrDefault(this, 0) : 0);
        }

        public int getAmountForValue(int value) {
            return (amountPerStep * value) / valuePerStep;
        }

        public int getBase(PCLLoadout loadout) {
            switch (this) {
                case Gold:
                    return loadout.getBaseGold();
                case HP:
                    return loadout.getBaseHP();
                case CardDraw:
                    return loadout.getBaseDraw();
                case PotionSlot:
                    return 0;
                case Energy:
                    return loadout.getBaseEnergy();
                default:
                    return 0;
            }
        }

        public String getText(PCLLoadout loadout, PCLLoadoutData data) {
            if (loadout == null || data == null) {
                return "";
            }
            switch (this) {
                case Gold:
                    return CharacterOption.TEXT[5] + getAmount(loadout, data);
                case HP:
                    return CharacterOption.TEXT[4] + getAmount(loadout, data);
                case CardDraw:
                    return PGR.core.strings.rewards_orbSlot + ": " + getAmount(loadout, data);
                case PotionSlot:
                    return PGR.core.strings.rewards_potionSlot + ": " + getAmount(loadout, data);
                case Energy:
                    return PGR.core.strings.rewards_commonUpgrade + ": " + getAmount(loadout, data);
                default:
                    return "";
            }
        }

        public void setAmount(PCLLoadoutData data, int amount) {
            if (data != null) {
                data.values.put(this, amount);
            }
        }
    }
}
