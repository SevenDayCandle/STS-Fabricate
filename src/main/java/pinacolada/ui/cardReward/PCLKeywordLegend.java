package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIRM;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUITextBox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIFontHelper;

public class PCLKeywordLegend extends EUIBase
{
    public EUIImage image;
    public EUITextBox textBox;

    public PCLKeywordLegend(EUITooltip tooltip)
    {
        image = new EUIImage(tooltip.icon.getTexture(), Color.WHITE);
        textBox = new EUITextBox(EUIRM.images.panel.texture(), new EUIHitbox(0, 0, scale(148), scale(36)))
                .setAlignment(0.5f, 0.31f) // 0.1f
                .setColors(Color.DARK_GRAY, Settings.CREAM_COLOR)
                .setFont(EUIFontHelper.carddescriptionfontNormal, 1)
                .setLabel(tooltip.title);
    }

    public PCLKeywordLegend setPosition(float x, float y)
    {
        textBox.setPosition(x, y);

        return this;
    }

    @Override
    public void updateImpl()
    {
        textBox.updateImpl();
        image.updateImpl();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        textBox.renderImpl(sb);
        float size = textBox.hb.width * 0.3f;
        image.render(sb, textBox.hb.x /*+ (textBox.hb.width - size)*/, textBox.hb.cY - (size * 0.5f), size, size);
    }
}
