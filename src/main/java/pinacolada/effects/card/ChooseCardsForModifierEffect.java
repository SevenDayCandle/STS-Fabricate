package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.patches.screens.GridCardSelectScreenPatches;
import pinacolada.resources.PGR;
import pinacolada.skills.skills.base.moves.PMove_Modify;

import java.util.ArrayList;
import java.util.List;

public class ChooseCardsForModifierEffect extends GenericChooseCardsEffect {
    private final ActionT1<AbstractCard> onSelect;

    public ChooseCardsForModifierEffect(PMove_Modify<?> move, ActionT1<AbstractCard> onSelect) {
        this(move.extra, move::canCardPass, onSelect);
    }

    public ChooseCardsForModifierEffect(int count, FuncT1<Boolean, AbstractCard> canCardPass, ActionT1<AbstractCard> onSelect) {
        super(count, canCardPass);
        this.canCancel = true;
        this.onSelect = onSelect;
    }

    @Override
    protected List<? extends AbstractCard> getGroup() {
        return AbstractDungeon.player.masterDeck.group;
    }

    @Override
    public void onCardSelected(AbstractCard c) {
        onSelect.invoke(c);
    }
}
