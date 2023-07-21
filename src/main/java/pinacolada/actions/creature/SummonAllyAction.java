package pinacolada.actions.creature;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PCLEnum;
import pinacolada.utilities.GameUtilities;

public class SummonAllyAction extends PCLAction<PCLCard> {
    public final PCLCard card;
    public PCLCardAlly ally;
    public boolean requireTarget;
    public boolean retainPowers;
    public boolean delayForTurn = true;
    public boolean showEffect = true;
    public boolean summonCardOnly = true;

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
            CombatManager.summons.withdraw(ally).setClearPowers(false).addCallback(this::initializeAlly);
        }
        else {
            initializeAlly();
        }

        CombatManager.onAllySummon(card, ally);
        complete(returnedCard);
    }

    protected void initializeAlly() {
        PCLActions.bottom.callback(() -> {
            if (showEffect) {
                PCLEffects.Queue.add(new SmokeEffect(ally.hb.cX, ally.hb.cY));
            }
            this.ally.initializeForCard(card, retainPowers, delayForTurn);
        });
    }

    public SummonAllyAction setOptions(boolean requireTarget, boolean retainPowers, boolean stun, boolean showEffect, boolean summonCardOnly) {
        this.requireTarget = requireTarget;
        this.retainPowers = retainPowers;
        this.delayForTurn = stun;
        this.showEffect = showEffect;
        this.summonCardOnly = summonCardOnly;
        return this;
    }
}
