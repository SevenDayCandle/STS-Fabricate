package pinacolada.skills.fields;

import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_CardModifyTag extends PField_CardCategory
{
    public ArrayList<PCLCardTag> addTags = new ArrayList<>();
    public boolean or;

    public PField_CardModifyTag()
    {
        super();
    }

    public PField_CardModifyTag(PField_CardModifyTag other)
    {
        super(other);
        setAddTag(other.addTags);
        setOr(other.or);
    }

    @Override
    public boolean equals(PField other)
    {
        return super.equals(other);
    }

    @Override
    public PField_CardModifyTag makeCopy()
    {
        return new PField_CardModifyTag(this);
    }

    public PField_CardModifyTag setAddTag(PCLCardTag... affinities)
    {
        return setAddTag(Arrays.asList(affinities));
    }

    public PField_CardModifyTag setAddTag(List<PCLCardTag> affinities)
    {
        this.addTags.clear();
        this.addTags.addAll(affinities);
        return this;
    }

    public PField_CardModifyTag setOr(boolean value)
    {
        this.or = value;
        return this;
    }

    public void setupEditor(PCLCustomCardEffectEditor<?> editor)
    {
        editor.registerPile(groupTypes);
        editor.registerTag(addTags);
        registerUseParentBoolean(editor);
    }

    public String getAddTagChoiceString()
    {
        return getTagAndOrString(addTags, or);
    }
}
