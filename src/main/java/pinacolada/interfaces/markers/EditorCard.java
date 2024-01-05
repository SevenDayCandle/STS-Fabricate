package pinacolada.interfaces.markers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
import extendedui.interfaces.markers.CacheableCard;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.providers.PointerProvider;

public interface EditorCard extends PointerProvider, CacheableCard {

    default int getCounterValue() {
        return ((AbstractCard) this).magicNumber;
    }

    default int getXValue() {
        return ((AbstractCard) this).misc;
    }

    default void renderForPreview(SpriteBatch sb) {
        if (SingleCardViewPopup.isViewingUpgrade) {
            ((AbstractCard) this).renderUpgradePreview(sb);
        }
        else {
            ((AbstractCard) this).render(sb);
        }
    }

    default void setup(Object input) {
    }

    default void triggerOnFetch(CardGroup sourcePile) {
        doEffects(be -> be.triggerOnFetch((AbstractCard) this, sourcePile));
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

    PCLCardTarget pclTarget();

    Texture getPortraitImageTexture();
}
