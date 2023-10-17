package pinacolada.skills.fields;

import extendedui.EUIRM;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_CardModifyAffinity extends PField_CardModify {
    public ArrayList<PCLAffinity> addAffinities = new ArrayList<>();

    public PField_CardModifyAffinity() {
        super();
    }

    public PField_CardModifyAffinity(PField_CardModifyAffinity other) {
        super(other);
        setAddAffinity(other.addAffinities);
    }

    @Override
    public boolean equals(PField other) {
        return super.equals(other);
    }

    public String getAddAffinityChoiceString() {
        if (addAffinities.isEmpty()) {
            return or ? PGR.core.strings.subjects_anyX(getGeneralAffinityString()) : EUIRM.strings.numNoun(1, PGR.core.strings.subjects_randomX(getGeneralAffinityString()));
        }
        return getAffinityAndOrString(addAffinities, or);
    }

    @Override
    public PField_CardModifyAffinity makeCopy() {
        return new PField_CardModifyAffinity(this);
    }

    public PField_CardModifyAffinity setAddAffinity(Collection<PCLAffinity> nt) {
        this.addAffinities.clear();
        for (PCLAffinity t : nt) {
            if (t != null) {
                this.addAffinities.add(t);
            }
        }
        return this;
    }

    public PField_CardModifyAffinity setAddAffinity(PCLAffinity... affinities) {
        return setAddAffinity(Arrays.asList(affinities));
    }

    public PField_CardModifyAffinity setOr(boolean value) {
        this.or = value;
        return this;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerPile(groupTypes);
        editor.registerAffinity(addAffinities);
    }
}
