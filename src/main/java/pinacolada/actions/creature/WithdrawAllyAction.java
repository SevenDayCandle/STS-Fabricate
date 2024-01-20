package pinacolada.actions.creature;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ShowCardAfterWithdrawEffect;
import pinacolada.effects.vfx.SmokeEffect;
import pinacolada.monsters.PCLCardAlly;

import java.util.ArrayList;
import java.util.Collection;

public class WithdrawAllyAction extends PCLAction<ArrayList<PCLCard>> {
    public final ArrayList<PCLCardAlly> allies = new ArrayList<>();
    public boolean clearPowers = true;
    public boolean showEffect = true;
    public CardGroup destination = AbstractDungeon.player.discardPile;

    public WithdrawAllyAction(PCLCardAlly slot, int amount) {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, slot, amount);
        allies.add(slot);
    }

    public WithdrawAllyAction(Collection<PCLCardAlly> slot, int amount) {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, amount);
        allies.addAll(slot);
    }

    @Override
    protected void firstUpdate() {
        ArrayList<PCLCard> returned = new ArrayList<>();
        ArrayList<PCLCardAlly> toRelease = new ArrayList<>();

        for (PCLCardAlly ally : allies) {
            if (ally != null) {
                PCLCard returnedCard = ally.card;

                if (returnedCard != null) {
                    ally.onWithdraw();
                    if (amount > 0) {
                        for (int i = 0; i < amount; i++) {
                            ally.takeTurn(true);
                        }
                        PCLEffects.Queue.add(new ShowCardAfterWithdrawEffect(returnedCard.makeStatEquivalentCopy(), ally.hb.cX, ally.hb.cY));
                    }
                    toRelease.add(ally);
                    if (showEffect) {
                        PCLEffects.Queue.add(new SmokeEffect(ally.hb.cX, ally.hb.cY));
                    }
                }

                returned.add(returnedCard);
            }
        }

        // Callback must be executed after the sequential to avoid incorrect calculations in triggers
        if (!toRelease.isEmpty()) {
            PCLActions.last.callback(() -> {
                for (PCLCardAlly ally : toRelease) {
                    releaseCard(ally);
                }
            }).addCallback(() -> {
                for (ActionT1<ArrayList<PCLCard>> callback : callbacks) {
                    callback.invoke(returned);
                }
            });
            completeImpl();
        }
        else {
            complete(returned);
        }
    }

    protected void releaseCard(PCLCardAlly ally) {
        PCLCard returnedCard = ally.releaseCard(clearPowers);
        if (returnedCard != null) {
            destination.addToTop(returnedCard);
            returnedCard.unhover();
            returnedCard.untip();
            returnedCard.unfadeOut();
        }

        CombatManager.onAllyWithdraw(returnedCard, ally);
    }

    public WithdrawAllyAction setClearPowers(boolean clearPowers) {
        this.clearPowers = clearPowers;
        return this;
    }

    public WithdrawAllyAction setDestination(CardGroup destination) {
        this.destination = destination;
        return this;
    }

    public WithdrawAllyAction showEffect(boolean showEffect) {
        this.showEffect = showEffect;
        return this;
    }
}
