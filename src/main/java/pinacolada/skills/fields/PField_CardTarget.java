package pinacolada.skills.fields;

import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_CardTarget extends PField_Random {
    public ArrayList<PCLCardTarget> cardTargets = new ArrayList<>();
    public boolean random;

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_CardTarget && cardTargets.equals(((PField_CardTarget) other).cardTargets) && ((PField_CardTarget) other).random == random && ((PField_CardTarget) other).not == not;
    }

    @Override
    public PField_CardTarget makeCopy() {
        return (PField_CardTarget) new PField_CardTarget().setCardTarget(cardTargets).setRandom(random).setNot(not);
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerDropdown(Arrays.asList(PCLCardTarget.values()), cardTargets, PCLCardTarget::getTitle, PGR.core.strings.cedit_cardTarget, true);
        super.setupEditor(editor);
    }

    public PField_CardTarget setCardTarget(List<PCLCardTarget> orbs) {
        this.cardTargets.clear();
        this.cardTargets.addAll(orbs);
        return this;
    }

    public PField_CardTarget setCardTarget(PCLCardTarget... orbs) {
        return setCardTarget(Arrays.asList(orbs));
    }
}
