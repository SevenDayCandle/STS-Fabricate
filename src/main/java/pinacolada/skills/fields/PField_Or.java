package pinacolada.skills.fields;

import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

public class PField_Or extends PField_Not
{
    public boolean or;

    @Override
    public boolean equals(PField other)
    {
        return other instanceof PField_Or && not == ((PField_Or) other).not && or == ((PField_Or) other).or;
    }

    @Override
    public PField_Or makeCopy()
    {
        return (PField_Or) new PField_Or().setOr(or).setNot(not);
    }

    public PField_Or setOr(boolean value)
    {
        this.or = value;
        return this;
    }

    public void setupEditor(PCLCustomCardEffectEditor editor)
    {
        super.setupEditor(editor);
        editor.registerBoolean(PGR.core.strings.cardEditor.orCondition, v -> or = v, or);
    }
}
