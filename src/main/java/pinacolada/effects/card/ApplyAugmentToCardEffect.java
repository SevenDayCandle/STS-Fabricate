package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;
import pinacolada.resources.PGR;

import java.util.ArrayList;

public class ApplyAugmentToCardEffect extends GenericChooseCardsEffect {

    public final PCLAugment augment;

    public ApplyAugmentToCardEffect(PCLAugment augment) {
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

    public void onCardSelected(AbstractCard c) {
        PGR.dungeon.addAugment(augment.ID, -1);
        augment.addToCard((PCLCard) c);
    }
}
