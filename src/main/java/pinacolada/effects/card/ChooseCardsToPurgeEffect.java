package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.FuncT1;

import java.util.ArrayList;

public class ChooseCardsToPurgeEffect extends GenericChooseCardsEffect {

    public ChooseCardsToPurgeEffect(int remove) {
        this(remove, null);
    }

    public ChooseCardsToPurgeEffect(int remove, FuncT1<Boolean, AbstractCard> filter) {
        super(remove, filter);
    }

    protected boolean forPurge() {
        return true;
    }

    @Override
    protected ArrayList<AbstractCard> getGroup() {
        return AbstractDungeon.player.masterDeck.getPurgeableCards().group;
    }

    public void onCardSelected(AbstractCard c) {
        AbstractDungeon.player.masterDeck.removeCard(c);
    }


}
