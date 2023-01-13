package pinacolada.skills.fields;

import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

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
        editor.registerPile(groupTypes);
        editor.registerCard(cardIDs);
        editor.registerBoolean(PGR.core.strings.cardEditor.random, v -> random = v, random);
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
