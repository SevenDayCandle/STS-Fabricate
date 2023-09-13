package pinacolada.skills.fields;

import extendedui.EUIUtils;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    // Logic is defined in the skills that use this field
    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
    }

    public PField_Numeric setIndexes(Collection<Integer> orbs) {
        this.indexes.clear();
        this.indexes.addAll(orbs);
        return this;
    }

    public PField_Numeric setIndexes(Integer... indexes) {
        return setIndexes(Arrays.asList(indexes));
    }
}
