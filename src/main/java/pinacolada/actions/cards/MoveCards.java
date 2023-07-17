package pinacolada.actions.cards;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardSelection;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class MoveCards extends PCLAction<ArrayList<AbstractCard>> {
    protected ArrayList<AbstractCard> selectedCards = new ArrayList<>();
    protected FuncT1<Boolean, AbstractCard> filter;
    protected PCLCardSelection destination = PCLCardSelection.Manual;
    protected PCLCardSelection origin = PCLCardSelection.Top;
    protected CardGroup targetPile;
    protected CardGroup sourcePile;
    protected boolean showEffect = true;
    protected boolean realtime;
    protected float effectDuration;

    public MoveCards(CardGroup targetPile, CardGroup sourcePile) {
        this(targetPile, sourcePile, -1);
    }

    public MoveCards(CardGroup targetPile, CardGroup sourcePile, int amount) {
        super(ActionType.CARD_MANIPULATION);

        this.destination = PCLCardSelection.Manual;
        this.origin = PCLCardSelection.Top;
        this.targetPile = targetPile;
        this.sourcePile = sourcePile;

        initialize(amount);
    }

    @Override
    protected void firstUpdate() {
        ArrayList<AbstractCard> temp = filter != null ? EUIUtils.filter(sourcePile.group, filter) : new ArrayList<>(sourcePile.group);

        int max = amount;
        if (amount == -1 || temp.size() < amount) {
            max = temp.size();
        }

        for (int i = 0; i < max; i++) {
            final AbstractCard card = origin.get(temp, i);
            if (card != null) {
                selectedCards.add(card);
                PCLActions.top.moveCard(card, sourcePile, targetPile)
                        .showEffect(showEffect, realtime, effectDuration)
                        .setDestination(destination);
            }
        }

        complete(selectedCards);
    }

    public MoveCards setDestination(PCLCardSelection destination) {
        this.destination = destination;

        return this;
    }

    public MoveCards setFilter(FuncT1<Boolean, AbstractCard> filter) {
        this.filter = filter;

        return this;
    }

    public MoveCards setOrigin(PCLCardSelection origin) {
        this.origin = (origin != null ? origin : PCLCardSelection.Top);

        return this;
    }

    public MoveCards showEffect(boolean showEffect, boolean isRealtime) {
        float duration = showEffect ? Settings.ACTION_DUR_MED : Settings.ACTION_DUR_FAST;

        if (Settings.FAST_MODE) {
            duration *= 0.7f;
        }

        return showEffect(showEffect, isRealtime, duration);
    }

    public MoveCards showEffect(boolean showEffect, boolean isRealtime, float effectDuration) {
        this.showEffect = showEffect;
        this.realtime = isRealtime;
        this.effectDuration = effectDuration;

        return this;
    }
}
