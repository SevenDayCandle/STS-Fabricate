package pinacolada.actions.cardManipulation;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.actions.utility.UnlimboAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.actions.PCLActionWithCallbackT2;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.DelayAllActions;
import pinacolada.effects.PCLEffects;
import pinacolada.utilities.GameUtilities;

// If this action needs 1 more refactoring due to queueing a card not counting
// as an action, completely override AbstractDungeon.actionManager instead.
public class PlayCard extends PCLActionWithCallbackT2<AbstractMonster, AbstractCard>
{
    public static final float DEFAULT_TARGET_X_LEFT = (Settings.WIDTH / 2f) - (300f * Settings.scale);
    public static final float DEFAULT_TARGET_X_RIGHT = (Settings.WIDTH / 2f) + (200f * Settings.scale);
    public static final float DEFAULT_TARGET_Y = (Settings.HEIGHT / 2f);

    protected FuncT1<AbstractCard, CardGroup> findCard;
    protected CardGroup sourcePile;
    protected int sourcePileIndex;
    protected boolean purge;
    protected boolean exhaust;
    protected boolean spendEnergy;
    protected Vector2 currentPosition;
    protected Vector2 targetPosition;
    protected boolean renderLast;

    public PlayCard(FuncT1<AbstractCard, CardGroup> findCard, CardGroup sourcePile, AbstractCreature target)
    {
        super(ActionType.WAIT, Settings.ACTION_DUR_FAST);

        this.isRealtime = true;
        this.findCard = findCard;
        this.sourcePile = sourcePile;

        initialize(target, 1);
    }

    public PlayCard(AbstractCard card, AbstractCreature target, boolean copy, boolean renderLast)
    {
        super(ActionType.WAIT, Settings.ACTION_DUR_FAST);

        this.isRealtime = true;

        if (copy)
        {
            this.card = card.makeSameInstanceOf();
            this.card.energyOnUse = card.energyOnUse;
        }
        else
        {
            this.card = card;
        }

        this.renderLast = renderLast;

        addToLimbo();

        initialize(target, 1);
    }

    protected void addToLimbo()
    {
        if (card != null && !player.limbo.contains(card))
        {
            if (renderLast)
            {
                player.limbo.addToTop(card);
            }
            else
            {
                player.limbo.addToBottom(card);
            }
        }
    }

    protected boolean canUse()
    {
        return card.canUse(player, (AbstractMonster) target) || card.dontTriggerOnUseCard;
    }

    @Override
    protected void firstUpdate()
    {
        super.firstUpdate();

        if (findCard != null)
        {
            if (sourcePile.size() > 0)
            {
                card = findCard.invoke(sourcePile);
            }

            if (card == null)
            {
                complete();
                return;
            }
            else
            {
                GameUtilities.trySetPosition(sourcePile, card);
            }
        }

        if (!checkConditions(card))
        {
            complete();
            return;
        }

        if (sourcePile != null)
        {
            sourcePileIndex = sourcePile.group.indexOf(card);
            if (sourcePileIndex >= 0)
            {
                sourcePile.group.remove(sourcePileIndex);
            }
            else
            {
                EUIUtils.logWarning(this, "Could not find " + card.cardID + " in " + sourcePile.type.name().toLowerCase());
                complete();
                return;
            }
        }

        if (targetPosition == null)
        {
            setTargetPosition(DEFAULT_TARGET_X_LEFT, DEFAULT_TARGET_Y);
        }

        showCard();
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            if (GameUtilities.requiresTarget(card) && (target == null || GameUtilities.isDeadOrEscaped(target)))
            {
                target = GameUtilities.getRandomEnemy(true);
            }

            if (!spendEnergy)
            {
                card.freeToPlayOnce = true;
                card.ignoreEnergyOnUse = false;
            }

            if (canUse())
            {
                queueCardItem();
                return;
            }
            else if (purge)
            {
                PCLActions.top.add(new UnlimboAction(card));
            }
            else if (exhaust)
            {
                PCLActions.top.exhaust(card, player.limbo).setRealtime(true);
            }
            else if (spendEnergy && sourcePile == player.hand)
            {
                player.limbo.removeCard(card);
                sourcePile.group.add(MathUtils.clamp(sourcePileIndex, 0, sourcePile.size()), card);
            }
            else
            {
                PCLActions.top.discard(card, player.limbo).setRealtime(true);
                PCLActions.top.add(new WaitAction(Settings.ACTION_DUR_FAST));
            }

            if (card.cantUseMessage != null)
            {
                PCLEffects.List.add(new ThoughtBubble(player.dialogX, player.dialogY, 3, card.cantUseMessage, true));
            }

            card.freeToPlayOnce = false;
        }
    }

    protected void queueCardItem()
    {
        addToLimbo();

        final AbstractMonster enemy = (AbstractMonster) target;

        if (!spendEnergy)
        {
            card.freeToPlayOnce = true;
        }

        card.exhaustOnUseOnce = exhaust;
        card.purgeOnUse = purge;
        card.calculateCardDamage(enemy);

        //GameActions.Top.Add(new UnlimboAction(card));
        PCLActions.top.wait(Settings.FAST_MODE ? Settings.ACTION_DUR_FASTER : Settings.ACTION_DUR_MED);

        int energyOnUse = EnergyPanel.getCurrentEnergy();

        if (spendEnergy)
        {
            PCLActions.top.add(new DelayAllActions()) // So the result of canUse() does not randomly change after queueing the card
                    .except(a -> a instanceof UnlimboAction || a instanceof WaitAction);
        }
        else if (card.energyOnUse != -1)
        {
            energyOnUse = card.energyOnUse;
        }

        AbstractDungeon.actionManager.cardQueue.add(0, new CardQueueItem(card, enemy, energyOnUse, true, !spendEnergy));

        complete(enemy);
    }

    public PlayCard setCurrentPosition(float x, float y)
    {
        currentPosition = new Vector2(x, y);

        return this;
    }

    public PlayCard setExhaust(boolean exhaust)
    {
        this.exhaust = exhaust;
        if (exhaust)
        {
            this.purge = false;
        }

        return this;
    }

    public PlayCard setPurge(boolean purge)
    {
        this.purge = purge;
        if (purge)
        {
            this.exhaust = false;
        }

        return this;
    }

    public PlayCard setSourcePile(CardGroup sourcePile)
    {
        this.sourcePile = sourcePile;

        if (card != null)
        {
            GameUtilities.trySetPosition(sourcePile, card);
        }

        return this;
    }

    public PlayCard setTargetPosition(float x, float y)
    {
        targetPosition = new Vector2(x, y);

        return this;
    }

    protected void showCard()
    {
        addToLimbo();

        GameUtilities.refreshHandLayout();
        AbstractDungeon.getCurrRoom().souls.remove(card);

        if (currentPosition != null)
        {
            card.current_x = currentPosition.x;
            card.current_y = currentPosition.y;
        }

        card.target_x = targetPosition.x;
        card.target_y = targetPosition.y;
        card.targetAngle = 0f;
        card.unfadeOut();
        card.lighten(true);
        card.drawScale = 0.5f;
        card.targetDrawScale = 0.75f;
    }

    public PlayCard spendEnergy(boolean spendEnergy)
    {
        this.spendEnergy = spendEnergy;

        return this;
    }
}
