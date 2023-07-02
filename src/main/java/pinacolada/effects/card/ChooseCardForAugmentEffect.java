package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;
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

    protected boolean forUpgrade() {
        return true;
    }

    @Override
    protected ArrayList<AbstractCard> getGroup() {
        return AbstractDungeon.player.masterDeck.group;
    }

    @Override
    protected void openGridScreen(CardGroup cardGroup) {
        GridCardSelectScreenMultiformPatches.setAugment(augment);
        super.openGridScreen(cardGroup);
    }

    public void complete() {
        super.complete();
        GridCardSelectScreenMultiformPatches.setAugment(null);
    }

    public void onCardSelected(AbstractCard c) {
        PGR.dungeon.addAugment(augment.ID, -1);
        augment.addToCard((PCLCard) c);
    }
}