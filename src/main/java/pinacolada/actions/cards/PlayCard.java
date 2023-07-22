package pinacolada.actions.cards;

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
import pinacolada.actions.PCLActions;
import pinacolada.actions.PCLConditionalAction;
import pinacolada.actions.utility.DelayAllActions;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.patches.card.AbstractCardPatches;
import pinacolada.resources.PCLEnum;
import pinacolada.utilities.GameUtilities;

// Copied and modified from STS-AnimatorMod
public class PlayCard extends PCLConditionalAction<AbstractMonster, AbstractCard> {
    public static final float DEFAULT_TARGET_X_LEFT = (Settings.WIDTH / 2f) - (300f * Settings.scale);
    public static final float DEFAULT_TARGET_X_RIGHT = (Settings.WIDTH / 2f) + (200f * Settings.scale);
    public static final float DEFAULT_TARGET_Y = (Settings.HEIGHT / 2f);

    protected CardGroup sourcePile;
    protected int sourcePileIndex;
    protected boolean force = true;
    protected boolean purge;
    protected boolean exhaust;
    protected boolean spendEnergy;
    protected Vector2 currentPosition;
    protected Vector2 targetPosition;
    protected boolean renderLast;

    public PlayCard(AbstractCard card, AbstractCreature target, boolean copy, boolean renderLast) {
        super(ActionType.WAIT, Settings.ACTION_DUR_FAST);

        this.isRealtime = true;

        if (copy) {
            this.card = card.makeSameInstanceOf();
            this.card.energyOnUse = card.energyOnUse;
        }
        else {
            this.card = card;
        }

        this.renderLast = renderLast;

        addToLimbo();

        initialize(target, 1);
    }

    protected void addToLimbo() {
        if (card != null && !player.limbo.contains(card)) {
            if (renderLast) {
                player.limbo.addToTop(card);
            }
            else {
                player.limbo.addToBottom(card);
            }
        }
    }

    protected boolean canUse() {
        return card.canUse(player, GameUtilities.asMonster(target)) || card.dontTriggerOnUseCard;
    }

    @Override
    protected void firstUpdate() {
        super.firstUpdate();

        if (!checkCondition(card)) {
            completeImpl();
            return;
        }

        if (sourcePile != null) {
            sourcePileIndex = sourcePile.group.indexOf(card);
            if (sourcePileIndex >= 0) {
                sourcePile.group.remove(sourcePileIndex);
            }
            else {
                EUIUtils.logWarning(this, "Could not find " + card.cardID + " in " + sourcePile.type.name().toLowerCase());
                completeImpl(); // Do not call callback
                return;
            }
        }

        if (targetPosition == null) {
            setTargetPosition(DEFAULT_TARGET_X_LEFT, DEFAULT_TARGET_Y);
        }

        showCard();
    }

    @Override
    protected void updateInternal(float deltaTime) {
        if (tickDuration(deltaTime)) {
            AbstractMonster enemy = GameUtilities.asMonster(target);
            if (GameUtilities.requiresTarget(card) && (enemy == null || GameUtilities.isDeadOrEscaped(enemy))) {
                if (card.type == PCLEnum.CardType.SUMMON) {
                    enemy = GameUtilities.getRandomSummon(false);
                    if (enemy == null) {
                        enemy = GameUtilities.getRandomSummon(true);
                    }
                }
                else {
                    enemy = GameUtilities.getRandomEnemy(true);
                }
            }

            if (!spendEnergy) {
                card.freeToPlayOnce = true;
                card.ignoreEnergyOnUse = false;
            }

            AbstractCardPatches.forcePlay = force;

            if (canUse()) {
                queueCardItem(enemy);
                return;
            }
            else if (purge) {
                PCLActions.top.add(new UnlimboAction(card));
            }
            else if (exhaust) {
                PCLActions.top.exhaust(card, player.limbo).setRealtime(true);
            }
            else if (spendEnergy && sourcePile == player.hand) {
                player.limbo.removeCard(card);
                sourcePile.group.add(MathUtils.clamp(sourcePileIndex, 0, sourcePile.size()), card);
            }
            else {
                PCLActions.top.discard(card, player.limbo).setRealtime(true);
                PCLActions.top.add(new WaitAction(Settings.ACTION_DUR_FAST));
            }

            if (card.cantUseMessage != null) {
                PCLEffects.List.add(new ThoughtBubble(player.dialogX, player.dialogY, 3, card.cantUseMessage, true));
            }

            AbstractCardPatches.forcePlay = false;
            card.freeToPlayOnce = false;
        }
    }

    protected void queueCardItem(AbstractMonster enemy) {
        addToLimbo();

        if (!spendEnergy) {
            card.freeToPlayOnce = true;
        }

        card.exhaustOnUseOnce = exhaust;
        card.purgeOnUse = purge;
        card.calculateCardDamage(enemy);

        //GameActions.Top.Add(new UnlimboAction(card));
        PCLActions.top.wait(Settings.FAST_MODE ? Settings.ACTION_DUR_FASTER : Settings.ACTION_DUR_MED);

        int energyOnUse = EnergyPanel.getCurrentEnergy();

        if (spendEnergy) {
            PCLActions.top.add(new DelayAllActions()) // So the result of canUse() does not randomly change after queueing the card
                    .except(a -> a instanceof UnlimboAction || a instanceof WaitAction);
        }
        else if (card.energyOnUse != -1) {
            energyOnUse = card.energyOnUse;
        }

        AbstractDungeon.actionManager.cardQueue.add(0, new CardQueueItem(card, enemy, energyOnUse, true, !spendEnergy));

        complete(enemy);
    }

    public PlayCard setCondition(FuncT1<Boolean, AbstractCard> condition) {
        super.setCondition(condition);

        return this;
    }

    public PlayCard setCurrentPosition(float x, float y) {
        currentPosition = new Vector2(x, y);

        return this;
    }

    public PlayCard setExhaust(boolean exhaust) {
        this.exhaust = exhaust;
        if (exhaust) {
            this.purge = false;
        }

        return this;
    }

    public PlayCard setPurge(boolean purge) {
        this.purge = purge;
        if (purge) {
            this.exhaust = false;
        }

        return this;
    }

    public PlayCard setSourcePile(CardGroup sourcePile) {
        this.sourcePile = sourcePile;

        if (card != null) {
            GameUtilities.trySetPosition(sourcePile, card);
        }

        return this;
    }

    public PlayCard setTargetPosition(float x, float y) {
        targetPosition = new Vector2(x, y);

        return this;
    }

    protected void showCard() {
        addToLimbo();

        CombatManager.queueRefreshHandLayout();
        AbstractDungeon.getCurrRoom().souls.remove(card);

        if (currentPosition != null) {
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

    public PlayCard spendEnergy(boolean spendEnergy) {
        this.spendEnergy = spendEnergy;

        return this;
    }
}
