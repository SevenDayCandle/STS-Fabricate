package pinacolada.effects.card;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.effects.PCLEffects;

import java.util.ArrayList;

public class ChooseCardsToObtainEffect extends GenericChooseCardsEffect {

    protected final ArrayList<AbstractCard> choices;
    private float displayCount;

    public ChooseCardsToObtainEffect(int remove, ArrayList<AbstractCard> choices) {
        this(remove, choices, null);
    }

    public ChooseCardsToObtainEffect(int remove, ArrayList<AbstractCard> choices, FuncT1<Boolean, AbstractCard> filter) {
        super(remove, filter);
        this.choices = choices;
    }

    @Override
    protected ArrayList<AbstractCard> getGroup() {
        return choices;
    }

    @Override
    public void onCardSelected(AbstractCard c) {
        PCLEffects.Queue.showAndObtain(c, (float) Settings.WIDTH / 3f + displayCount, (float) Settings.HEIGHT / 2f, false);
        displayCount += Settings.WIDTH / 6f;
    }
}
