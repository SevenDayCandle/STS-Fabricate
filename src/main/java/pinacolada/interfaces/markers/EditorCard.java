package pinacolada.interfaces.markers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.CacheableCard;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;

import java.util.ArrayList;

public interface EditorCard extends PointerProvider, CacheableCard {

    default void doEffects(ActionT1<PSkill<?>> action) {
        for (PSkill<?> be : getFullEffects()) {
            action.invoke(be);
        }
    }

    default void doNonPowerEffects(ActionT1<PSkill<?>> action) {
        for (PSkill<?> be : getFullEffects()) {
            if (!(be instanceof SummonOnlyMove)) {
                action.invoke(be);
            }
        }
    }

    default PTrigger getPowerEffect(int i) {
        ArrayList<PTrigger> effects = getPowerEffects();
        return effects != null && effects.size() > i ? effects.get(i) : null;
    }

    default void renderForPreview(SpriteBatch sb) {
        if (this instanceof AbstractCard) {
            if (SingleCardViewPopup.isViewingUpgrade) {
                ((AbstractCard) this).renderUpgradePreview(sb);
            }
            else {
                ((AbstractCard) this).render(sb);
            }
        }
    }

    default void setup(Object input) {
    }

    default void triggerOnPurge() {
        doEffects(be -> be.triggerOnPurge((AbstractCard) this));
    }

    default void triggerOnReshuffle(CardGroup sourcePile) {
        doEffects(be -> be.triggerOnReshuffle((AbstractCard) this, sourcePile));
    }

    default void triggerWhenCreated(boolean startOfBattle) {
        doEffects(be -> be.triggerOnCreate((AbstractCard) this, startOfBattle));
    }

    void fullReset();

    void loadImage(String path);

    int hitCount();

    int hitCountBase();

    int rightCount();

    int rightCountBase();

    PCLAttackType attackType();

    Texture getPortraitImageTexture();
}
