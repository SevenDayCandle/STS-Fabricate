package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIInputManager;
import extendedui.configuration.EUIHotkeys;
import extendedui.ui.EUIBase;
import pinacolada.interfaces.listeners.DrawPileCardPreviewProvider;
import pinacolada.utilities.RotatingList;

public class DrawPileCardPreview
{
    private static final float DRAW_X = 75 * Settings.scale;
    private static final float DRAW_Y = 220 * Settings.scale;
    private static final RotatingList<DrawPileCardPreview> PREVIEWS = new RotatingList<>();
    private static DrawPileCardPreview current = null;

    public final DrawPileCardPreviewProvider provider;
    protected AbstractCard foundCard;
    protected AbstractCreature target;
    protected boolean highlighted;

    public DrawPileCardPreview(DrawPileCardPreviewProvider provider)
    {
        this.provider = provider;
        provider.findCard();
    }

    // This is performed as a single action because we need to store the draw scale/angle in one go
    public void updateAndRender(SpriteBatch sb)
    {
        if (foundCard != null)
        {
            float lastCurrentX = foundCard.current_x;
            float lastCurrentY = foundCard.current_y;
            float lastDrawScale = foundCard.drawScale;
            float lastAngle = foundCard.angle;

            foundCard.angle = 0;
            foundCard.unfadeOut();
            foundCard.lighten(true);
            foundCard.drawScale = 0.45f;
            foundCard.current_x = DRAW_X;
            foundCard.current_y = DRAW_Y;
            foundCard.hb.move(foundCard.current_x, foundCard.current_y);
            foundCard.hb.resize(EUIBase.scale(AbstractCard.RAW_W) * foundCard.drawScale, EUIBase.scale(AbstractCard.RAW_H) * foundCard.drawScale);
            foundCard.hb.update();
            foundCard.render(sb);
            if (foundCard.hb.hovered) {
                highlighted = true;
                provider.highlight(this);
                foundCard.renderCardTip(sb);
                if (EUIInputManager.leftClick.isJustPressed())
                {
                    provider.onClick(foundCard);
                }
            }
            else
            {
                highlighted = false;
            }

            foundCard.current_x = lastCurrentX;
            foundCard.current_y = lastCurrentY;
            foundCard.drawScale = lastDrawScale;
            foundCard.angle = lastAngle;
        }
        else
        {
            highlighted = false;
        }
    }

    public AbstractCard getCard()
    {
        return foundCard;
    }

    public static DrawPileCardPreview subscribe(DrawPileCardPreviewProvider provider)
    {
        DrawPileCardPreview preview = new DrawPileCardPreview(provider);
        PREVIEWS.add(new DrawPileCardPreview(provider));
        updatePreviews();
        return preview;
    }

    public static void unsubscribe(DrawPileCardPreviewProvider provider)
    {
        PREVIEWS.removeIf(preview -> preview.provider == provider);
        updatePreviews();
    }

    public static void refreshCard(DrawPileCardPreviewProvider provider)
    {
        for (DrawPileCardPreview p : PREVIEWS)
        {
            if (p.provider == provider)
            {
                p.foundCard = provider.findCard();
                return;
            }
        }
    }

    public static void updatePreviews()
    {
        if (PREVIEWS.size() > 1)
        {
            if (EUIHotkeys.cycle.isJustPressed())
            {
                current = PREVIEWS.next(true);
            }
            else
            {
                current = PREVIEWS.current();
            }
        }
        else
        {
            current = PREVIEWS.current();
        }
    }

    public static void updateAndRenderCurrent(SpriteBatch sb)
    {
        updatePreviews();

        if (current != null)
        {
            current.updateAndRender(sb);
        }
    }

    public boolean isHighlighted()
    {
        return highlighted;
    }
}
