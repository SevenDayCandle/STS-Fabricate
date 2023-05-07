package pinacolada.skills.fields;

import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.ui.cardEditor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_Tag extends PField_Random {
    public ArrayList<PCLCardTag> tags = new ArrayList<>();

    public PField_Tag addTag(PCLCardTag... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Tag && tags.equals(((PField_Tag) other).tags) && ((PField_Tag) other).random == random && ((PField_Tag) other).not == not;
    }

    @Override
    public PField_Tag makeCopy() {
        return (PField_Tag) new PField_Tag().setTag(tags).setRandom(random).setNot(not);
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerTag(tags);
        super.setupEditor(editor);
    }

    public String getTagString() {
        return getTagString(tags);
    }

    public PField_Tag setTag(List<PCLCardTag> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
        return this;
    }

    public PField_Tag setTag(PCLCardTag... tags) {
        return setTag(Arrays.asList(tags));
    }
}
