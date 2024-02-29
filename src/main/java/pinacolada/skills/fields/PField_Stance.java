package pinacolada.skills.fields;

import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Stance extends PField_Random {
    public ArrayList<PCLStanceHelper> stances = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Stance && stances.equals(((PField_Stance) other).stances) && ((PField_Stance) other).random == random && ((PField_Stance) other).not == not;
    }

    public String getAnyStanceString() {
        return stances.isEmpty() ? TEXT.cond_any(PGR.core.tooltips.stance.title) : getStanceString();
    }

    public String getStanceString() {
        return PCLCoreStrings.joinWithOr(stance -> "{" + stance.tooltip.title + "}", stances);
    }

    @Override
    public PField_Stance makeCopy() {
        return (PField_Stance) new PField_Stance().setStance(stances).setRandom(random).setNot(not);
    }

    public PField_Stance setStance(Collection<PCLStanceHelper> orbs) {
        this.stances.clear();
        this.stances.addAll(orbs);
        return this;
    }

    public PField_Stance setStance(PCLStanceHelper... orbs) {
        return setStance(Arrays.asList(orbs));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerStance(stances);
        super.setupEditor(editor);
    }
}
