package pinacolada.effects.card;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.common.EmptyDeckShuffleAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndAddToHandEffect;
import com.megacrit.cardcrawl.vfx.combat.CardPoofEffect;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffect;
import pinacolada.effects.PCLEffectWithCallback;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;

import java.util.Iterator;

public class ShowCardEffect extends PCLEffectWithCallback<AbstractCard> {
    private static final float PADDING = (15.0F * Settings.scale) + AbstractCard.IMG_WIDTH;
    private final AbstractCard card;
    private boolean showPoof;

    public ShowCardEffect(AbstractCard card) {
        this(card, Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f);
    }

    public ShowCardEffect(AbstractCard card, float duration) {
        this(card, Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f, duration);
    }

    public ShowCardEffect(AbstractCard card, float x, float y) {
        this(card, x, y, 1.3f);
    }

    public ShowCardEffect(AbstractCard card, float x, float y, float duration) {
        super(duration);
        this.card = card;
        identifySpawnLocation(x, y);
        this.card.drawScale = 0.01F;
        this.card.targetDrawScale = 0.9F;
    }

    @Override
    protected void firstUpdate(float deltaTime) {
        if (showPoof) {
            PCLEffects.Queue.add(new CardPoofEffect(this.card.target_x, this.card.target_y));
            PCLSFX.play(PCLSFX.CARD_OBTAIN);
        }
    }

    private void identifySpawnLocation(float x, float y) {
        int effectCount = EUIUtils.count(AbstractDungeon.effectList,
                e -> e instanceof ShowCardEffect);

        this.card.target_y = y;
        switch(effectCount) {
            case 0:
                this.card.target_x = x;
                break;
            case 1:
                this.card.target_x = x - PADDING;
                break;
            case 2:
                this.card.target_x = x + PADDING;
                break;
            case 3:
                this.card.target_x = x - (PADDING * 2.0F);
                break;
            case 4:
                this.card.target_x = x + (PADDING * 2.0F);
                break;
            default:
                this.card.target_x = MathUtils.random((float)Settings.WIDTH * 0.1F, (float)Settings.WIDTH * 0.9F);
                this.card.target_y = MathUtils.random((float)Settings.HEIGHT * 0.2F, (float)Settings.HEIGHT * 0.8F);
        }

        this.card.current_x = this.card.target_x;
        this.card.current_y = this.card.target_y - 200.0F * Settings.scale;
    }

    public ShowCardEffect showPoof(boolean val) {
        this.showPoof = val;
        return this;
    }

    @Override
    public void render(SpriteBatch sb) {
        this.card.render(sb);
    }

    @Override
    public void updateInternal(float deltaTime) {
        this.card.update();
        if (tickDuration(deltaTime)) {
            card.shrink();
            complete(card);
        }
    }
}