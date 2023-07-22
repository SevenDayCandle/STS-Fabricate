package pinacolada.effects.card;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.PetalEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffects;
import pinacolada.utilities.GameUtilities;

import java.util.Iterator;

public class ShowCardAfterWithdrawEffect extends PCLEffect {
    private static final float PADDING = 30.0F * Settings.scale;
    private final AbstractCard card;

    public ShowCardAfterWithdrawEffect(AbstractCard card) {
        super(1.5F);
        this.card = card;
        this.card.current_x = this.card.target_x = (float)Settings.WIDTH / 2.0F;
        this.card.current_y = this.card.target_y = (float)Settings.HEIGHT / 2.0F;
        this.card.drawScale = 0.01F;
        this.card.targetDrawScale = 0.8F;
        this.card.glowColor = PCLCard.SYNERGY_GLOW_COLOR;
    }

    @Override
    public void firstUpdate() {
        super.firstUpdate();
        AbstractDungeon.effectsQueue.add(new CardPoofEffect(card.target_x, card.target_y));
        card.beginGlowing();
    }

    @Override
    public void updateInternal(float deltaTime) {
        super.updateInternal(deltaTime);
        this.card.update();
    }

    @Override
    public void complete() {
        super.complete();
        card.fadingOut = true;
        card.shrink();
        AbstractDungeon.getCurrRoom().souls.discard(card, true);
        card.stopGlowing();
    }

    @Override
    public void render(SpriteBatch sb) {
        this.card.render(sb);
    }
}