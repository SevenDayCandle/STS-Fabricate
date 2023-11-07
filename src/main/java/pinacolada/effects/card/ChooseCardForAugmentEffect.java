package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.patches.screens.GridCardSelectScreenPatches;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class ChooseCardForAugmentEffect extends GenericChooseCardsEffect {

    public final PCLAugment augment;

    public ChooseCardForAugmentEffect(PCLAugment augment) {
        super(1, augment::canApply);
        this.augment = augment;
        this.canCancel = true;
    }

    public void complete() {
        super.complete();
        GridCardSelectScreenPatches.setAugment(null);
    }

    protected boolean forUpgrade() {
        return true;
    }

    @Override
    protected ArrayList<AbstractCard> getGroup() {
        return AbstractDungeon.player.masterDeck.group;
    }

    public void onCardSelected(AbstractCard c) {
        PGR.dungeon.removeAugment(augment.save);
        augment.addToCard((PCLCard) c);
    }

    @Override
    protected void openGridScreen(CardGroup cardGroup) {
        GridCardSelectScreenPatches.setAugment(augment);
        super.openGridScreen(cardGroup);
    }
}
