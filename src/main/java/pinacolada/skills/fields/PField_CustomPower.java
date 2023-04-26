package pinacolada.skills.fields;

import extendedui.EUIUtils;
import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_CustomPower extends PField {
    public ArrayList<Integer> indexes = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_CustomPower;
    }

    @Override
    public PField makeCopy() {
        return new PField_CustomPower().setIndexes(indexes);
    }

    // Indexes should correspond to the indexes of powers in the card being built
    public void setupEditor(PCLCustomCardEffectEditor<?> editor) {
        List<Integer> range = Arrays.asList(EUIUtils.range(0, editor.editor.screen.getPowerCount() - 1));
        editor.registerDropdown(range, indexes, item -> String.valueOf(item + 1), PGR.core.strings.cedit_powers, false, false, false);
    }

    public PField_CustomPower setIndexes(List<Integer> orbs) {
        this.indexes.clear();
        this.indexes.addAll(orbs);
        return this;
    }

    public PField_CustomPower setIndexes(Integer... indexes) {
        return setIndexes(Arrays.asList(indexes));
    }
}
