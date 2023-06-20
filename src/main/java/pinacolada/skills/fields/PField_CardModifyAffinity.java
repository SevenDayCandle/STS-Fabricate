package pinacolada.skills.fields;

import pinacolada.cards.base.fields.PCLAffinity;
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

    @Override
    public PField_CardModifyAffinity makeCopy() {
        return new PField_CardModifyAffinity(this);
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerPile(groupTypes);
        editor.registerAffinity(addAffinities);
    }

    public String getAddAffinityChoiceString() {
        return getAffinityAndOrString(addAffinities, or);
    }

    public PField_CardModifyAffinity setAddAffinity(Collection<PCLAffinity> affinities) {
        this.addAffinities.clear();
        this.addAffinities.addAll(affinities);
        return this;
    }

    public PField_CardModifyAffinity setAddAffinity(PCLAffinity... affinities) {
        return setAddAffinity(Arrays.asList(affinities));
    }

    public PField_CardModifyAffinity setOr(boolean value) {
        this.or = value;
        return this;
    }
}
