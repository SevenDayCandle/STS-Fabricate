package pinacolada.ui.cardEditor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUITextBoxNumericalInput;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIColors;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;

import static pinacolada.cards.base.fields.PCLAffinity.MAX_LEVEL;

public class PCLCustomCardAffinityValueEditor extends EUIHoverable
{
    protected static final float ICON_SIZE = 36f * Settings.scale;

    protected ActionT3<PCLAffinity, Integer, Integer> onUpdate;
    protected PCLAffinity affinity;
    protected int value;
    protected int value2;
    protected EUITextBoxNumericalInput displayValue;
    protected EUITextBoxNumericalInput displayValue2;
    protected EUIButton decreaseButton;
    protected EUIButton decreaseButton2;
    protected EUIButton increaseButton;
    protected EUIButton increaseButton2;
    protected EUIImage affinityImage;

    public PCLCustomCardAffinityValueEditor(EUIHitbox hb, PCLAffinity affinity, ActionT3<PCLAffinity, Integer, Integer> onUpdate)
    {
        super(hb);
        this.affinity = affinity;
        this.onUpdate = onUpdate;

        final float w = hb.width;
        final float h = hb.height;

        decreaseButton = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, h * 0.5f))
                .setOnClick(this::decreasePrimary)
                .setHoverBlendColor(EUIColors.lerp(Color.SCARLET, Color.ORANGE, 0.5f))
                .setText(null);

        decreaseButton2 = new EUIButton(ImageMaster.CF_LEFT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, ICON_SIZE * -0.4f, -h * 0.35f))
                .setOnClick(this::decreaseSecondary)
                .setHoverBlendColor(EUIColors.lerp(Color.SCARLET, Color.ORANGE, 0.5f))
                .setText(null);

        increaseButton = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, w + (ICON_SIZE * 0.4f), h * 0.5f))
                .setOnClick(this::increasePrimary)
                .setHoverBlendColor(EUIColors.lerp(Color.SCARLET, Color.ORANGE, 0.5f))
                .setText(null);

        increaseButton2 = new EUIButton(ImageMaster.CF_RIGHT_ARROW, new RelativeHitbox(hb, ICON_SIZE, ICON_SIZE, w + (ICON_SIZE * 0.4f), -h * 0.35f))
                .setOnClick(this::increaseSecondary)
                .setHoverBlendColor(EUIColors.lerp(Color.SCARLET, Color.ORANGE, 0.5f))
                .setText(null);

        displayValue = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(), hb)
                .setOnComplete(v -> this.setValue(v, true))
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);

        displayValue2 = (EUITextBoxNumericalInput) new EUITextBoxNumericalInput(EUIRM.images.panelRoundedHalfH.texture(),
                new RelativeHitbox(hb, hb.width, hb.height, w * 0.5f, -h * 0.35f))
                .setOnComplete(v -> this.setSecondaryValue(v, true))
                .setBackgroundTexture(EUIRM.images.panelRoundedHalfH.texture(), new Color(0.5f, 0.5f, 0.5f, 1f), 1.05f)
                .setColors(new Color(0, 0, 0, 0.85f), Settings.CREAM_COLOR)
                .setAlignment(0.5f, 0.5f)
                .setFont(EUIFontHelper.cardtitlefontSmall, 1f);

        this.affinityImage = new EUIImage(affinity.getIcon(), new EUIHitbox(hb.x, hb.y + hb.height * 0.8f, ICON_SIZE, ICON_SIZE))
                .setTooltip(affinity.getTooltip().title, PGR.core.strings.cardEditorTutorial.attrAffinity);
    }

    public void decreasePrimary()
    {
        setValue(value - 1, value2, true);
    }

    public void decreaseSecondary()
    {
        setValue(value, value2 - 1, true);
    }

    public int getValue()
    {
        return value;
    }

    public void increasePrimary()
    {
        setValue(value + 1, value2, true);
    }

    public void increaseSecondary()
    {
        setValue(value, value2 + 1, true);
    }

    public PCLCustomCardAffinityValueEditor setSecondaryValue(int valueSecondary, boolean update)
    {
        return setValue(value, valueSecondary, update);
    }

    public PCLCustomCardAffinityValueEditor setValue(int value, boolean update)
    {
        return setValue(value, value2, update);
    }

    public PCLCustomCardAffinityValueEditor setValue(int value, int valueSecondary, boolean update)
    {
        this.value = MathUtils.clamp(value, 0, MAX_LEVEL);
        displayValue.setLabel(this.value);
        this.value2 = MathUtils.clamp(valueSecondary, -this.value, MAX_LEVEL - this.value);
        displayValue2.setLabel(this.value2);
        if (update)
        {
            onUpdate.invoke(affinity, this.value, this.value2);
        }

        return this;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        decreaseButton.setInteractable(value > 0).tryUpdate();
        decreaseButton2.setInteractable(value + value2 > 0).tryUpdate();
        increaseButton.setInteractable(value < MAX_LEVEL).tryUpdate();
        increaseButton2.setInteractable(value + value2 < MAX_LEVEL).tryUpdate();
        displayValue.tryUpdate();
        displayValue2.tryUpdate();
        affinityImage.tryUpdate();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        this.hb.render(sb);
        decreaseButton2.tryRender(sb);
        increaseButton2.tryRender(sb);
        decreaseButton.tryRender(sb);
        increaseButton.tryRender(sb);
        displayValue2.tryRender(sb);
        displayValue.tryRender(sb);
        affinityImage.tryRender(sb);
    }
}
