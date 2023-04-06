package pinacolada.skills.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT5;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.misc.PCLUseInfo;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_CardID extends PField_CardGeneric
{
    public ArrayList<String> cardIDs = new ArrayList<>();

    public PField_CardID()
    {
        super();
    }

    public PField_CardID(PField_CardID other)
    {
        super(other);
        setCardIDs(other.cardIDs);
    }

    @Override
    public boolean equals(PField other)
    {
        return super.equals(other)
                && cardIDs.equals(((PField_CardID) other).cardIDs)
                && groupTypes.equals(((PField_CardID) other).groupTypes);
    }

    @Override
    public PField_CardID makeCopy()
    {
        return new PField_CardID(this);
    }

    public void setupEditor(PCLCustomCardEffectEditor<?> editor)
    {
        editor.registerOrigin(origin, origins -> setOrigin(origins.size() > 0 ? origins.get(0) : PCLCardSelection.Manual));
        editor.registerPile(groupTypes);
        editor.registerCard(cardIDs);
    }

    public FuncT1<Boolean, AbstractCard> getFullCardFilter()
    {
        return c -> cardIDs.isEmpty() || EUIUtils.any(cardIDs, id -> id.equals(c.cardID));
    }

    public String getFullCardString()
    {
        return cardIDs.isEmpty() ? getCardIDOrString() : getShortCardString();
    }

    public String getFullCardStringSingular()
    {
        return getFullCardString();
    }

    public SelectFromPile createFilteredAction(FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> action, PCLUseInfo info, int subchoices)
    {
        return super.createFilteredAction(action, info, subchoices).setFilter(getFullCardFilter());
    }

    public PField_CardID setCardIDs(String... cards)
    {
        return setCardIDs(Arrays.asList(cards));
    }

    public PField_CardID setCardIDs(Collection<String> cards)
    {
        this.cardIDs.clear();
        this.cardIDs.addAll(cards);
        return this;
    }

    public String getCardIDAndString()
    {
        return getCardIDAndString(cardIDs);
    }

    public String getCardIDOrString()
    {
        return getCardIDOrString(cardIDs);
    }
}
