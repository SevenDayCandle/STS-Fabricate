package pinacolada.actions.creature;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLAction;
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
    public int triggerTimes = 2;
    public boolean clearPowers = true;
    public boolean showEffect = true;
    public CardGroup destination = AbstractDungeon.player.discardPile;

    public WithdrawAllyAction(PCLCardAlly slot) {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, slot, 1);
        allies.add(slot);
    }

    public WithdrawAllyAction(Collection<PCLCardAlly> slot) {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);
        initialize(AbstractDungeon.player, 1);
        allies.addAll(slot);
    }

    @Override
    protected void firstUpdate() {
        ArrayList<PCLCard> returned = new ArrayList<>();

        for (PCLCardAlly ally : allies) {
            if (ally != null) {
                PCLCard returnedCard = ally.card;

                if (returnedCard != null) {
                    for (int i = 0; i < triggerTimes; i++) {
                        ally.takeTurn(true);
                    }
                    releaseCard(ally);
                }

                returned.add(returnedCard);
            }
        }

        complete(returned);
    }

    protected void releaseCard(PCLCardAlly ally) {
        PCLCard returnedCard = ally.releaseCard(clearPowers);
        if (returnedCard != null) {
            PCLEffects.Queue.add(new ShowCardAfterWithdrawEffect(returnedCard.makeStatEquivalentCopy(), ally.hb.cX, ally.hb.cY));
            destination.addToTop(returnedCard);
            returnedCard.unhover();
            returnedCard.untip();
            returnedCard.unfadeOut();
        }

        if (showEffect) {
            PCLEffects.Queue.add(new SmokeEffect(ally.hb.cX, ally.hb.cY));
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

    public WithdrawAllyAction setTriggerTimes(int showEffect) {
        this.triggerTimes = showEffect;
        return this;
    }

    public WithdrawAllyAction showEffect(boolean showEffect) {
        this.showEffect = showEffect;
        return this;
    }
}
