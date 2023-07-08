package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.patches.screens.GridCardSelectScreenMultiformPatches;
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
        GridCardSelectScreenMultiformPatches.setAugment(null);
    }

    protected boolean forUpgrade() {
        return true;
    }

    @Override
    protected void openGridScreen(CardGroup cardGroup) {
        GridCardSelectScreenMultiformPatches.setAugment(augment);
        super.openGridScreen(cardGroup);
    }

    @Override
    protected ArrayList<AbstractCard> getGroup() {
        return AbstractDungeon.player.masterDeck.group;
    }

    public void onCardSelected(AbstractCard c) {
        PGR.dungeon.addAugment(augment.ID, -1);
        augment.addToCard((PCLCard) c);
    }
}
