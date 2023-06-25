package pinacolada.skills.fields;

import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_CardModifyTag extends PField_CardModify {
    public ArrayList<PCLCardTag> addTags = new ArrayList<>();

    public PField_CardModifyTag() {
        super();
    }

    public PField_CardModifyTag(PField_CardModifyTag other) {
        super(other);
        setAddTag(other.addTags);
    }

    @Override
    public boolean equals(PField other) {
        return super.equals(other);
    }

    @Override
    public PField_CardModifyTag makeCopy() {
        return new PField_CardModifyTag(this);
    }

    public PField_CardModifyTag setOr(boolean value) {
        this.or = value;
        return this;
    }

    public String getAddTagChoiceString() {
        return getTagAndOrString(addTags, or);
    }

    public PField_CardModifyTag setAddTag(Collection<PCLCardTag> affinities) {
        this.addTags.clear();
        this.addTags.addAll(affinities);
        return this;
    }

    public PField_CardModifyTag setAddTag(PCLCardTag... affinities) {
        return setAddTag(Arrays.asList(affinities));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerPile(groupTypes);
        editor.registerTag(addTags);
    }
}
