package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.utilities.PCLRenderHelpers;

public class PowerFormulaItem extends EUIHoverable
{
    public static final float ICON_SIZE = 32f;
    public TextureRegion iconRegion;
    public Texture icon;
    public EUILabel owner;
    public EUILabel modifier;
    public EUILabel result;

    protected PowerFormulaItem(EUIHitbox hb, boolean isPlayer, float result)
    {
        super(hb);
        this.owner = new EUILabel(FontHelper.powerAmountFont, RelativeHitbox.fromPercentages(hb, 1, 1, 0.2f, 1.1f))
                .setLabel(isPlayer ? 'P' : 'E')
                .setColor(isPlayer ? Color.ROYAL : Color.SALMON);
        this.modifier = new EUILabel(FontHelper.powerAmountFont, RelativeHitbox.fromPercentages(hb, 1, 1, 0f, -1.2f))
                .setSmartText(false);
        this.result = new EUILabel(FontHelper.powerAmountFont, RelativeHitbox.fromPercentages(hb, 1, 1, 0, -2f))
                .setLabel((int) result)
                .setSmartText(false);
    }

    public PowerFormulaItem(EUIHitbox hb, boolean isPlayer, Texture icon, float result)
    {
        this(hb, isPlayer, result);
        this.icon = icon;
    }

    public PowerFormulaItem(EUIHitbox hb, boolean isPlayer, TextureRegion icon, float result)
    {
        this(hb, isPlayer, result);
        this.iconRegion = icon;
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        if (this.iconRegion != null)
        {
            PCLRenderHelpers.drawCentered(sb, Color.WHITE, this.iconRegion, hb.x, hb.y, ICON_SIZE, ICON_SIZE, 1f, 0);
        }
        else
        {
            PCLRenderHelpers.drawCentered(sb, Color.WHITE, this.icon, hb.x, hb.y, ICON_SIZE, ICON_SIZE, 1f, 0);
        }
        FontHelper.renderFontCentered(sb, FontHelper.powerAmountFont, ">>", hb.x - hb.width * 1.1f, hb.cY - hb.height, Color.WHITE);
        this.owner.renderImpl(sb);
        this.result.renderImpl(sb);
        this.modifier.renderImpl(sb);
    }

    public PowerFormulaItem setAddition(float addition)
    {
        this.modifier.setLabel(addition > 0 ? "+" + PCLRenderHelpers.decimalFormat(addition) : PCLRenderHelpers.decimalFormat(addition)).setColor(addition < 0 ? Color.RED : Color.GREEN);
        return this;
    }

    public PowerFormulaItem setMultiplier(float multiplier)
    {
        this.modifier.setLabel("x" + PCLRenderHelpers.decimalFormat(multiplier)).setColor(multiplier < 1 ? Color.RED : Color.GREEN);
        return this;
    }

    @Override
    public void updateImpl()
    {
        super.updateImpl();
        this.owner.updateImpl();
        this.result.updateImpl();
        this.modifier.updateImpl();
    }
}
