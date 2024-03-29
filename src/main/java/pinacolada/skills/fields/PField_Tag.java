package pinacolada.skills.fields;

import extendedui.EUIUtils;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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

    public String getTagString() {
        return (PGR.config.displayCardTagDescription.get() || PSkill.isVerbose()) ? getTagAndString(tags) :
                tags.isEmpty() ? TEXT.cedit_tags : (EUIUtils.joinStringsMap(" ", PField::safeInvokeTip, tags));
    }

    @Override
    public PField_Tag makeCopy() {
        return (PField_Tag) new PField_Tag().setTag(tags).setRandom(random).setNot(not);
    }

    public PField_Tag setTag(Collection<PCLCardTag> nt) {
        this.tags.clear();
        for (PCLCardTag t : nt) {
            if (t != null) {
                this.tags.add(t);
            }
        }
        return this;
    }

    public PField_Tag setTag(PCLCardTag... tags) {
        return setTag(Arrays.asList(tags));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerTag(tags);
        super.setupEditor(editor);
    }
}
