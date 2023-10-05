package pinacolada.skills.fields;

import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Numeric extends PField {
    public ArrayList<Integer> indexes = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Numeric;
    }

    @Override
    public PField makeCopy() {
        return new PField_Numeric().setIndexes(indexes);
    }

    public PField_Numeric setIndexes(Collection<Integer> orbs) {
        this.indexes.clear();
        this.indexes.addAll(orbs);
        return this;
    }

    public PField_Numeric setIndexes(Integer... indexes) {
        return setIndexes(Arrays.asList(indexes));
    }

    // Logic is defined in the skills that use this field
    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
    }
}
