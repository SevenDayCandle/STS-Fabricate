package pinacolada.skills.fields;

import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PGR;
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

    public String getAddTagChoiceString() {
        return getTagAndOrString(addTags, or);
    }

    @Override
    public PField_CardModifyTag makeCopy() {
        return new PField_CardModifyTag(this);
    }

    public PField_CardModifyTag setAddTag(Collection<PCLCardTag> nt) {
        this.addTags.clear();
        for (PCLCardTag t : nt) {
            if (t != null) {
                this.addTags.add(t);
            }
        }
        return this;
    }

    public PField_CardModifyTag setAddTag(PCLCardTag... affinities) {
        return setAddTag(Arrays.asList(affinities));
    }

    public PField_CardModifyTag setOr(boolean value) {
        this.or = value;
        return this;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        setupEditorBase(editor);
        editor.registerTag(addTags, StringUtils.capitalize(TEXT.act_applyX(TEXT.cedit_tags)));
        setupEditorFilters(editor);
    }
}
