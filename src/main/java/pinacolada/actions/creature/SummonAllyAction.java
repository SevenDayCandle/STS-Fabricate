package pinacolada.actions.creature;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;
import pinacolada.utilities.GameUtilities;

public class SummonAllyAction extends PCLAction<PCLCard> {
    public final PCLCard card;
    private boolean requireTarget;
    private boolean retainPowers;
    private boolean delayForTurn = true;
    private boolean showEffect = true;
    private boolean summonCardOnly = true;
    private boolean triggerWithdraw = true;
    public PCLCardAlly ally;

    public SummonAllyAction(PCLCard card, PCLCardAlly slot) {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, slot, 1);
        this.card = card;
        this.ally = slot;
    }

    @Override
    protected void firstUpdate() {
        if ((this.card == null || summonCardOnly && this.card.type != PCLEnum.CardType.SUMMON)) {
            complete(null);
            return;
        }
        // If missing target, choose a random empty one, then a random filled one.
        if (this.ally == null) {
            if (!requireTarget) {
                this.ally = GameUtilities.getRandomSummon(false);
                if (this.ally == null) {
                    this.ally = GameUtilities.getRandomSummon(true);
                }
                if (this.ally == null) {
                    complete(null);
                    return;
                }
            }
            else {
                complete(null);
                return;
            }
        }

        PCLCard returnedCard = this.ally.card;
        // If ally is withdrawn, setting up the new card must come after the previous card is withdrawn
        // Also, do not clear powers in this withdraw call, in case we want to retain powers on the card
        if (returnedCard != null) {
            PCLActions.instant.withdrawAlly(ally, triggerWithdraw ? CombatManager.summons.triggerTimes : 0)
                    .triggerWithdraw(triggerWithdraw)
                    .setClearPowers(false)
                    .addCallback(this::initializeAlly);
        }
        else {
            initializeAlly();
        }

        CombatManager.onAllySummon(ally, card, returnedCard);
        complete(returnedCard);
    }

    protected void initializeAlly() {
        PCLActions.bottom.callback(() -> {
            if (showEffect) {
                Color particleColor = null;
                for (PCLAffinity affinity : GameUtilities.getVisiblePCLAffinities(card)) {
                    if (particleColor == null) {
                        particleColor = affinity.getAlternateColor();
                    }
                    else {
                        particleColor.lerp(affinity.getAlternateColor(), 0.5f);
                    }
                }
                if (particleColor == null) {
                    particleColor = Color.WHITE;
                }
                PCLEffects.Queue.add(new SmokeEffect(ally.hb.cX, ally.hb.cY, particleColor));
            }
            this.ally.initializeForCard(card, retainPowers, delayForTurn);
        });
    }

    public SummonAllyAction requireTarget(boolean v) {
        this.requireTarget = v;
        return this;
    }

    public SummonAllyAction retainPowers(boolean v) {
        this.retainPowers = v;
        return this;
    }

    public SummonAllyAction setDelay(boolean v) {
        this.delayForTurn = v;
        return this;
    }

    public SummonAllyAction showEffect(boolean v) {
        this.showEffect = v;
        return this;
    }

    public SummonAllyAction triggerWithdraw(boolean v) {
        this.triggerWithdraw = v;
        return this;
    }
}
