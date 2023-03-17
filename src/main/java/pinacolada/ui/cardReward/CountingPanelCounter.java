package pinacolada.ui.cardReward;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.Hitbox;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.ui.EUIBase;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.cards.base.CountingPanelStats;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.markers.CountingPanelItem;

public class CountingPanelCounter<T extends CountingPanelItem> extends EUIBase
{
    private static final Color PANEL_COLOR = new Color(0.05f, 0.05f, 0.05f, 1f);
    public final T type;
    public EUIButton backgroundButton;
    public EUIImage affinityImage;
    public EUILabel counterText;
    public EUILabel counterpercentageText;

    public CountingPanelCounter(CountingPanelStats<T, ?, ?> panel, Hitbox hb, T type, ActionT1<CountingPanelCounter<T>> onClick)
    {
        this.type = type;

        backgroundButton = new EUIButton(EUIRM.images.panelRoundedHalfH.texture(), RelativeHitbox.fromPercentages(hb, 1, 1, 0.5f, 0))
                .setColor(PANEL_COLOR)
                .setOnClick(onClick == null ? null : () -> onClick.invoke(this));

        affinityImage = new EUIImage(type.getIcon(), Color.WHITE)
                .setHitbox(new RelativeHitbox(hb, CountingPanel.ICON_SIZE, CountingPanel.ICON_SIZE, -0.5f * (CountingPanel.ICON_SIZE / hb.width), 0));

        counterText = new EUILabel(EUIFontHelper.cardTooltipFont,
                RelativeHitbox.fromPercentages(hb, 0.28f, 1, 0.3f, 0f))
                .setAlignment(0.5f, 0.5f) // 0.1f
                .setLabel(panel.getAmount(type));

        counterpercentageText = new EUILabel(EUIFontHelper.carddescriptionfontNormal,
                RelativeHitbox.fromPercentages(hb, 0.38f, 1, 0.8f, 0f))
                .setAlignment(0.5f, 0.5f) // 0.1f
                .setLabel(panel.getPercentageString(type));
    }

    public CountingPanelCounter<T> setIndex(int index)
    {
        float y = -index * backgroundButton.hb.height * 1.05f;
        backgroundButton.hb.setOffsetY(y);
        counterText.hb.setOffsetY(y);
        counterpercentageText.hb.setOffsetY(y);
        affinityImage.hb.setOffsetY(y);

        return this;
    }

    @Override
    public void updateImpl()
    {
        backgroundButton.setInteractable(PCLEffects.isEmpty()).updateImpl();
        counterText.updateImpl();
        counterpercentageText.updateImpl();
        affinityImage.updateImpl();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        backgroundButton.renderImpl(sb);
        counterpercentageText.renderImpl(sb);
        counterText.renderImpl(sb);
        affinityImage.renderImpl(sb);
    }
}
