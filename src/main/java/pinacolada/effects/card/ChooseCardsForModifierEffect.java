package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.patches.screens.GridCardSelectScreenPatches;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.base.moves.PMove_Modify;

import java.util.ArrayList;

public class ChooseCardsForModifierEffect extends GenericChooseCardsEffect {
    public final PMove_Modify<?> move;
    public final ActionT1<AbstractCard> onSelect;

    public ChooseCardsForModifierEffect(PMove_Modify<?> move, ActionT1<AbstractCard> onSelect) {
        super(move.extra, move::canCardPass);
        this.move = move;
        this.canCancel = true;
        this.onSelect = onSelect;
    }

    @Override
    protected ArrayList<AbstractCard> getGroup() {
        return AbstractDungeon.player.masterDeck.group;
    }

    public void onCardSelected(AbstractCard c) {
        onSelect.invoke(c);
    }
}
