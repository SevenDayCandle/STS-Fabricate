package pinacolada.interfaces.markers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.interfaces.markers.CacheableCard;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;

public interface EditorCard extends PointerProvider, CacheableCard
{
    Texture getPortraitImageTexture();

    int hitCount();

    int hitCountBase();

    int rightCount();

    int rightCountBase();

    void fullReset();

    void loadImage(String path, boolean refresh);

    default PTrigger getPowerEffect(int i)
    {
        ArrayList<PTrigger> effects = getPowerEffects();
        return effects != null && effects.size() > i ? effects.get(i) : null;
    }

    default void loadImage(String path)
    {
        loadImage(path, false);
    }

    default void renderForPreview(SpriteBatch sb)
    {
        if (this instanceof AbstractCard)
        {
            if (SingleCardViewPopup.isViewingUpgrade)
            {
                ((AbstractCard) this).renderUpgradePreview(sb);
            }
            else
            {
                ((AbstractCard) this).render(sb);
            }
        }
    }

    default void setup(Object input) {}
}
