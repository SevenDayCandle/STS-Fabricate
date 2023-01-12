package pinacolada.skills.fields;

import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

public class PField_Not extends PField
{
    public boolean not;

    @Override
    public boolean equals(PField other)
    {
        return other instanceof PField_Not && not == ((PField_Not) other).not;
    }

    @Override
    public PField_Not makeCopy()
    {
        return new PField_Not().setNot(not);
    }

    public PField_Not setNot(boolean value)
    {
        this.not = value;
        return this;
    }

    public void setupEditor(PCLCustomCardEffectEditor editor)
    {
        editor.registerBoolean(PGR.core.strings.cardEditor.not, v -> not = v, not);
    }
}
