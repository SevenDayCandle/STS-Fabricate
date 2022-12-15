package pinacolada.interfaces.markers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.interfaces.markers.CacheableCard;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.skills.PTrigger;

import java.util.ArrayList;

public interface EditorCard extends PointerProvider, CacheableCard
{
    Texture getPortraitImageTexture();

    default PTrigger getPowerEffect(int i)
    {
        ArrayList<PTrigger> effects = getPowerEffects();
        return effects != null && effects.size() > i ? effects.get(i) : null;
    }

    int getAffinityValue(PCLAffinity affinity);

    int hitCount();

    int rightCount();

    int secondaryValue();

    void loadImage(String suffix, boolean refresh);

    default void loadImage(String suffix)
    {
        loadImage(suffix, false);
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
