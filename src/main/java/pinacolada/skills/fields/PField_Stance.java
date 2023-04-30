package pinacolada.skills.fields;

import pinacolada.resources.PGR;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.ui.cardEditor.PCLCustomEffectEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_Stance extends PField_Random {
    public ArrayList<PCLStanceHelper> stances = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Stance && stances.equals(((PField_Stance) other).stances) && ((PField_Stance) other).random == random && ((PField_Stance) other).not == not;
    }

    @Override
    public PField_Stance makeCopy() {
        return (PField_Stance) new PField_Stance().setStance(stances).setRandom(random).setNot(not);
    }

    public void setupEditor(PCLCustomEffectEditor<?> editor) {
        editor.registerStance(stances);
        super.setupEditor(editor);
    }

    public PField_Stance setStance(List<PCLStanceHelper> orbs) {
        this.stances.clear();
        this.stances.addAll(orbs);
        return this;
    }

    public String getAnyStanceString() {
        return stances.isEmpty() ? TEXT.cond_any(PGR.core.tooltips.stance.title) : getStanceString();
    }

    public String getStanceString() {
        return getStanceString(stances);
    }

    public PField_Stance setStance(PCLStanceHelper... orbs) {
        return setStance(Arrays.asList(orbs));
    }
}
