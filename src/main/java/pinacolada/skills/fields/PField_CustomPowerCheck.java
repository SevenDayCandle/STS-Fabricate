package pinacolada.skills.fields;

import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_CustomPowerCheck extends PField_Random {
    public ArrayList<String> cardIDs = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_CustomPowerCheck &&
                ((PField_CustomPowerCheck) other).cardIDs.equals(cardIDs) &&
                ((PField_CustomPowerCheck) other).random == random &&
                ((PField_CustomPowerCheck) other).not == not;
    }

    public String getCardIDAndString() {
        return getCardIDAndString(cardIDs, 0, 0); // Upgrade/form doesn't matter
    }

    public String getCardIDOrString() {
        return getCardIDOrString(cardIDs, 0, 0);
    }

    @Override
    public PField_CustomPowerCheck makeCopy() {
        return (PField_CustomPowerCheck) new PField_CustomPowerCheck().setCardIDs(cardIDs).setRandom(random).setNot(not);
    }

    public PField_CustomPowerCheck setCardIDs(Collection<String> cards) {
        this.cardIDs.clear();
        this.cardIDs.addAll(cards);
        return this;
    }

    public PField_CustomPowerCheck setCardIDs(String... cards) {
        return setCardIDs(Arrays.asList(cards));
    }

    // Indexes should correspond to the indexes of powers in the card being built
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerCard(cardIDs);
    }

}
