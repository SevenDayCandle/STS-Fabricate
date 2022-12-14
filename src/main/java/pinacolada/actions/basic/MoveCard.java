package pinacolada.actions.basic;

import basemod.BaseMod;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT3;
import extendedui.utilities.GenericCallback;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.effects.SFX;
import pinacolada.effects.card.RenderCardEffect;
import pinacolada.effects.card.UnfadeOutEffect;
import pinacolada.misc.CombatStats;
import pinacolada.utilities.CardSelection;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameEffects;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class MoveCard extends PCLActionWithCallback<AbstractCard>
{
    public static final float DEFAULT_CARD_X_LEFT = Settings.WIDTH * 0.35f;
    public static final float DEFAULT_CARD_X_RIGHT = Settings.WIDTH * 0.65f;
    public static final float DEFAULT_CARD_Y = Settings.HEIGHT * 0.5f;

    protected pinacolada.utilities.ListSelection<AbstractCard> destination;
    protected CardGroup targetPile;
    protected CardGroup sourcePile;
    protected boolean showEffect;
    protected Vector2 targetPosition;

    public MoveCard(AbstractCard card, CardGroup targetPile)
    {
        this(card, targetPile, null);
    }

    public MoveCard(AbstractCard card, CardGroup targetPile, CardGroup sourcePile)
    {
        super(ActionType.CARD_MANIPULATION);

        this.card = card;
        this.sourcePile = sourcePile;
        this.targetPile = targetPile;
        this.destination = null;

        initialize(1);
    }

    @Override
    protected void complete(AbstractCard result)
    {
        super.complete(result);

        // Change card spot based on destination
        if (destination != null && targetPile.group.remove(card))
        {
            destination.add(targetPile.group, card, 0);
        }
    }

    @Override
    protected void firstUpdate()
    {
        if (sourcePile == null)
        {
            sourcePile = GameUtilities.findCardGroup(card, false);

            if (sourcePile == null)
            {
                EUIUtils.logWarning(this, "Could not find card source pile.");
                complete();
                return;
            }
        }

        if (sourcePile == targetPile)
        {
            complete();
            return;
        }

        if (!sourcePile.contains(card))
        {
            EUIUtils.logWarning(this, "Could not find " + card.cardID + " in " + sourcePile.type.name().toLowerCase());
            complete();
            return;
        }

        if (GameUtilities.trySetPosition(sourcePile, card) && showEffect)
        {
            GameEffects.TopLevelList.add(new RenderCardEffect(card, duration, isRealtime));
        }

        if (showEffect && targetPosition == null)
        {
            targetPosition = new Vector2();

            if (card.current_x < Settings.WIDTH / 2f)
            {
                targetPosition.x = DEFAULT_CARD_X_LEFT;
            }
            else
            {
                targetPosition.x = DEFAULT_CARD_X_RIGHT;
            }

            targetPosition.y = DEFAULT_CARD_Y;
        }

        if (targetPile.type == CombatStats.PURGED_CARDS.type)
        {
            purge();
            return;
        }

        switch (targetPile.type)
        {
            case DRAW_PILE:
                moveToDrawPile();
                break;

            case HAND:
                moveToHand();
                break;

            case DISCARD_PILE:
                moveToDiscardPile();
                break;

            case EXHAUST_PILE:
                moveToExhaustPile();
                break;

            case MASTER_DECK:
            case CARD_POOL:
            case UNSPECIFIED:
                moveToPile();
                break;
        }
        CombatStats.onCardMoved(card, sourcePile, targetPile);
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (showEffect && targetPile.type != CombatStats.PURGED_CARDS.type)
        {
            updateCard();
        }

        if (tickDuration(deltaTime))
        {
            complete(card);

            if (targetPile.type == CardGroup.CardGroupType.HAND || (sourcePile != null && sourcePile.type == CardGroup.CardGroupType.HAND))
            {
                GameUtilities.refreshHandLayout();
            }

            if (sourcePile != null && (sourcePile.type == CardGroup.CardGroupType.EXHAUST_PILE || sourcePile == CombatStats.PURGED_CARDS))
            {
                GameEffects.Queue.add(new UnfadeOutEffect(card));
                GameActions.bottom.callback(() -> GameEffects.Queue.add(new UnfadeOutEffect(card)));
            }

            if (targetPile != player.limbo && player.limbo.contains(card))
            {
                GameActions.bottom.add(new UnlimboAction(card, false));
            }
        }
    }

    protected void moveToDiscardPile()
    {
        if (showEffect)
        {
            showCard();

            callbacks.add(0, GenericCallback.fromT1(this::moveToDiscardPile));
        }
        else
        {
            moveToDiscardPile(card);
        }
    }

    protected void moveToDiscardPile(AbstractCard card)
    {
        sourcePile.moveToDiscardPile(card);

        if (sourcePile.type != CardGroup.CardGroupType.EXHAUST_PILE)
        {
            player.onCardDrawOrDiscard();
            card.triggerOnManualDiscard();
            GameActionManager.incrementDiscard(false);
        }
    }

    protected void moveToDrawPile()
    {
        if (showEffect)
        {
            showCard();

            callbacks.add(0, GenericCallback.fromT1(this::moveToDrawPile));
        }
        else
        {
            moveToDrawPile(card);
        }
    }

    protected void moveToDrawPile(AbstractCard card)
    {
        sourcePile.moveToDeck(card, true);
        CombatStats.onCardReshuffled(card, sourcePile);
    }

    protected void moveToExhaustPile()
    {
        if (showEffect)
        {
            showCard();

            callbacks.add(0, GenericCallback.fromT1(this::moveToExhaustPile));
        }
        else
        {
            moveToExhaustPile(card);
        }
    }

    protected void moveToExhaustPile(AbstractCard card)
    {
        sourcePile.moveToExhaustPile(card);
        CardCrawlGame.dungeon.checkForPactAchievement();
        card.exhaustOnUseOnce = false;
        card.freeToPlayOnce = false;
    }

    protected void moveToHand()
    {
        if (showEffect)
        {
            showCard();

            callbacks.add(0, GenericCallback.fromT1(this::moveToHand));
        }
        else
        {
            moveToHand(card);
        }
    }

    protected void moveToHand(AbstractCard card)
    {
        if (player.hand.size() >= BaseMod.MAX_HAND_SIZE)
        {
            player.createHandIsFullDialog();
            sourcePile.moveToDiscardPile(card);
        }
        else
        {
            card.triggerWhenDrawn();
            SFX.play(SFX.CARD_OBTAIN);
            sourcePile.moveToHand(card, sourcePile);
            CombatStats.onAfterDraw(card);
        }
    }

    protected void moveToPile()
    {
        if (showEffect)
        {
            showCard();

            callbacks.add(0, GenericCallback.fromT1(this::moveToPile));
        }
        else
        {
            moveToPile(card);
        }
    }

    protected void moveToPile(AbstractCard card)
    {
        card.untip();
        card.unhover();
        card.unfadeOut();
        card.targetAngle = 0;
        sourcePile.removeCard(card);
        targetPile.addToTop(card);
    }

    protected void purge()
    {
        sourcePile.removeCard(card);
        CombatStats.onCardPurged(card);

        if (showEffect)
        {
            showCard();

            final Vector2 pos = GameUtilities.tryGetPosition(sourcePile, card);
            final AbstractGameEffect effect = GameEffects.List.add(new PurgeCardEffect(card, pos.x, pos.y));
            if (targetPosition != null)
            {
                card.target_x = targetPosition.x;
                card.target_y = targetPosition.y;
            }

            this.startDuration = (this.duration = effect.startingDuration = effect.duration = Settings.ACTION_DUR_LONG) + 0.001f;
        }
        else
        {
            SFX.play(SFX.CARD_BURN);
        }

    }

    public MoveCard setCardPosition(float x, float y)
    {
        this.targetPosition = new Vector2(x, y);

        return this;
    }

    public MoveCard setDestination(ActionT3<List<AbstractCard>, AbstractCard, Integer> addCard)
    {
        this.destination = CardSelection.special(addCard, null);

        return this;
    }

    public MoveCard setDestination(pinacolada.utilities.ListSelection<AbstractCard> destination)
    {
        this.destination = destination;

        return this;
    }

    protected void showCard()
    {
        if (card.drawScale < 0.3f)
        {
            card.targetDrawScale = 0.75f;
        }

        card.untip();
        card.unhover();
        card.unfadeOut();
        card.target_x = targetPosition.x;
        card.target_y = targetPosition.y;
        card.targetAngle = 0;
        updateCard();
    }

    public MoveCard showEffect(boolean showEffect, boolean isRealtime)
    {
        float duration = showEffect ? Settings.ACTION_DUR_MED : Settings.ACTION_DUR_FAST;

        if (Settings.FAST_MODE)
        {
            duration *= 0.7f;
        }

        return showEffect(showEffect, isRealtime, duration);
    }

    public MoveCard showEffect(boolean showEffect, boolean isRealtime, float duration)
    {
        setDuration(duration, isRealtime);

        this.showEffect = showEffect;

        return this;
    }

    protected void updateCard()
    {
        if (player.hoveredCard == card)
        {
            player.releaseCard();
        }

        card.target_x = targetPosition.x;
        card.target_y = targetPosition.y;
        card.targetAngle = 0;
        card.hoverTimer = 0.5f;
        card.update();
    }
}
