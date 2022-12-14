package pinacolada.ui.cardView;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIRenderHelpers;
import extendedui.interfaces.delegates.ActionT0;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;

public class PCLAugmentViewer extends EUIHoverable
{

    protected EUIButton augmentButton;
    protected EUILabel augmentTitle;
    protected EUILabel augmentDescription;
    protected PCLCard card;
    protected int index;

    public PCLAugmentViewer(EUIHitbox hb, PCLCard card, int index)
    {
        super(hb);
        augmentButton = new EUIButton(PGR.core.images.augments.augment.texture(), new RelativeHitbox(hb, AbstractRelic.PAD_X, AbstractRelic.PAD_X, 0, 0)).setTooltip("", "");
        augmentTitle = new EUILabel(FontHelper.cardTitleFont, new RelativeHitbox(hb, hb.width, scale(40), augmentButton.hb.width * 2.8f, 0))
                .setFontScale(0.85f)
                .setColor(Settings.GOLD_COLOR)
                .setAlignment(0.5f, 0.01f);
        augmentDescription = new EUILabel(EUIFontHelper.cardTooltipFont, new RelativeHitbox(hb, hb.width, hb.height, augmentButton.hb.width * 2.8f, -augmentTitle.hb.height))
                .setAlignment(0.5f, 0.01f)
                .setSmartText(true);
        this.card = card;
        this.index = index;
        refreshAugment();
    }

    public float getHeight()
    {
        return (-augmentButton.hb.height + augmentDescription.getAutoHeight()) * 1.2f;
    }

    public void refreshAugment()
    {
        PCLAugment augment = card.getAugment(index);
        if (augment != null)
        {
            augmentTitle.setLabel(augment.getName());
            augmentButton
                    .setBackground(augment.getTexture())
                    .setColor(augment.getColor())
                    .setShaderMode(EUIRenderHelpers.ShaderMode.Colorize);
            augmentButton.tooltip.setTitle(augmentTitle.text).setDescription(augment.canRemove() ? PGR.core.strings.singleCardPopupButtons.clickToRemove : PGR.core.strings.singleCardPopupButtons.cannotRemove);
            augmentDescription.setLabel(augment.getFullText());
        }
        else
        {
            augmentTitle.setLabel(PGR.core.strings.singleCardPopupButtons.emptyAugment);
            augmentButton.setBackground(PGR.core.images.augments.augment.texture()).setColor(Color.WHITE).setShaderMode(EUIRenderHelpers.ShaderMode.Normal);
            augmentButton.tooltip.setTitle(augmentTitle.text).setDescription(PGR.core.strings.singleCardPopupButtons.clickToSlot);
            augmentDescription.setLabel("");
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        augmentButton.tryRender(sb);
        augmentTitle.tryRender(sb);
        augmentDescription.tryRender(sb);
    }

    public PCLAugmentViewer setOnClick(ActionT0 action)
    {
        augmentButton.setOnClick(action);
        return this;
    }

    @Override
    public void updateImpl()
    {
        augmentButton.tryUpdate();
        augmentTitle.tryUpdate();
        augmentDescription.tryUpdate();
    }
}
