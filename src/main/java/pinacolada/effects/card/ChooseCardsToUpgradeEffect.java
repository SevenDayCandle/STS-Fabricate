package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.effects.PCLEffects;

import java.util.ArrayList;

public class ChooseCardsToUpgradeEffect extends GenericChooseCardsEffect {

    public ChooseCardsToUpgradeEffect(int remove) {
        this(remove, null);
    }

    public ChooseCardsToUpgradeEffect(int remove, FuncT1<Boolean, AbstractCard> filter) {
        super(remove, filter);
    }

    @Override
    protected ArrayList<AbstractCard> getGroup() {
        return AbstractDungeon.player.masterDeck.getUpgradableCards().group;
    }

    public void onCardSelected(AbstractCard c) {
        PCLEffects.Queue.add(new UpgradeShineEffect((float) Settings.WIDTH / 2f, (float) Settings.HEIGHT / 2f));
        PCLEffects.Queue.showCardBriefly(c.makeStatEquivalentCopy());
    }


}
