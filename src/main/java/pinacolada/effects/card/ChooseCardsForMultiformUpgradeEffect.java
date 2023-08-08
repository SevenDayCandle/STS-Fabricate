package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;
import pinacolada.patches.screens.GridCardSelectScreenPatches;

import java.util.ArrayList;

public class ChooseCardsForMultiformUpgradeEffect extends GenericChooseCardsEffect {
    protected final PCLCard card;

    public ChooseCardsForMultiformUpgradeEffect(PCLCard card) {
        super(1, null);
        this.card = card;
    }

    @Override
    protected ArrayList<AbstractCard> getGroup() {
        GridCardSelectScreenPatches.fillCardListWithUpgrades(card);
        return GridCardSelectScreenPatches.getCardList();
    }

    public void onCardSelected(AbstractCard c) {
        if (c instanceof PCLCard) {
            card.changeForm(((PCLCard) c).getForm(), c.timesUpgraded);
            PCLEffects.Queue.add(new UpgradeShineEffect((float) Settings.WIDTH / 2f, (float) Settings.HEIGHT / 2f));
            PCLEffects.Queue.showCardBriefly(c.makeStatEquivalentCopy());
        }
    }

}
