package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;

import java.util.List;

public class ChooseCardsToUpgradeEffect extends GenericChooseCardsEffect {
    private List<? extends AbstractCard> passedCards;

    public ChooseCardsToUpgradeEffect(int remove) {
        this(remove, null);
    }

    public ChooseCardsToUpgradeEffect(int remove, FuncT1<Boolean, AbstractCard> filter) {
        super(remove, filter);
    }

    public static void permanentUpgrade(AbstractCard c) {
        if (c instanceof PCLCard && ((PCLCard) c).isBranchingUpgrade()) {
            PCLEffects.Queue.add(new ChooseCardsForMultiformUpgradeEffect((PCLCard) c));
        }
        else {
            c.upgrade();
            PCLEffects.Queue.add(new UpgradeShineEffect((float) Settings.WIDTH / 2f, (float) Settings.HEIGHT / 2f));
            PCLEffects.Queue.showCardBriefly(c.makeStatEquivalentCopy());
        }
    }

    @Override
    protected List<? extends AbstractCard> getGroup() {
        return passedCards != null ? passedCards : AbstractDungeon.player.masterDeck.getUpgradableCards().group;
    }

    @Override
    public void onCardSelected(AbstractCard c) {
        permanentUpgrade(c);
    }

    public ChooseCardsToUpgradeEffect setPassedCards(List<? extends AbstractCard> passedCards) {
        this.passedCards = passedCards;
        return this;
    }

}
