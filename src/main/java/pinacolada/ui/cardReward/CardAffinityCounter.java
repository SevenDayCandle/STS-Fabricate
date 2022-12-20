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
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardAffinityStatistics;
import pinacolada.effects.PCLEffects;

public class CardAffinityCounter extends EUIBase
{
    private static final Color PANEL_COLOR = new Color(0.05f, 0.05f, 0.05f, 1f);
    public final PCLAffinity type;
    public PCLCardAffinityStatistics.Group affinityGroup;
    public EUIButton backgroundButton;
    public EUIImage affinityImage;
    public EUILabel counterweakText;
    public EUILabel counterpercentageText;
    private ActionT1<CardAffinityCounter> onClick;

    public CardAffinityCounter(Hitbox hb, PCLAffinity affinity)
    {
        type = affinity;

        backgroundButton = new EUIButton(EUIRM.images.panelRoundedHalfH.texture(), RelativeHitbox.fromPercentages(hb, 1, 1, 0.5f, 0))
                .setColor(PANEL_COLOR);

        affinityImage = new EUIImage(affinity.getIcon(), Color.WHITE)
                .setHitbox(new RelativeHitbox(hb, CardAffinityPanel.ICON_SIZE, CardAffinityPanel.ICON_SIZE, -0.5f * (CardAffinityPanel.ICON_SIZE / hb.width), 0));

        counterweakText = new EUILabel(EUIFontHelper.cardTooltipFont,
                RelativeHitbox.fromPercentages(hb, 0.28f, 1, 0.3f, 0f))
                .setAlignment(0.5f, 0.5f) // 0.1f
                .setLabel("-");

        counterpercentageText = new EUILabel(EUIFontHelper.carddescriptionfontNormal,
                RelativeHitbox.fromPercentages(hb, 0.38f, 1, 0.8f, 0f))
                .setAlignment(0.5f, 0.5f) // 0.1f
                .setLabel("0%");
    }

    public void initialize(PCLCardAffinityStatistics statistics)
    {
        affinityGroup = statistics.getGroup(type);
        affinityImage.setTexture(type.getIcon());
    }

    public CardAffinityCounter setIndex(int index)
    {
        float y = -index * 1.05f;
        backgroundButton.hb.setOffsetY(y);
        counterweakText.hb.setOffsetY(y);
        counterpercentageText.hb.setOffsetY(y);
        affinityImage.hb.setOffsetY(y);

        return this;
    }

    public CardAffinityCounter setOnClick(ActionT1<CardAffinityCounter> onClick)
    {
        this.onClick = onClick;
        this.backgroundButton.setOnClick(onClick == null ? null : () -> this.onClick.invoke(this));

        return this;
    }

    @Override
    public void updateImpl()
    {
        final int lv1 = affinityGroup.getTotal(1);
        final int lv2 = affinityGroup.getTotal(2);

        backgroundButton.setInteractable(PCLEffects.isEmpty()).updateImpl();
        counterweakText.setLabel(lv1 == 0 ? "-" : lv1).updateImpl();
        counterpercentageText.setLabel(affinityGroup.getPercentageString(0)).updateImpl();
        affinityImage.updateImpl();
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        backgroundButton.renderImpl(sb);
        counterpercentageText.renderImpl(sb);
        counterweakText.renderImpl(sb);
        affinityImage.renderImpl(sb);
    }
}
