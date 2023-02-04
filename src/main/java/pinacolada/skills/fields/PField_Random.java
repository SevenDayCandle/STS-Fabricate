package pinacolada.skills.fields;

import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

public class PField_Random extends PField
{
    public boolean random;

    @Override
    public boolean equals(PField other)
    {
        return other instanceof PField_Random && random == ((PField_Random) other).random;
    }

    @Override
    public PField_Random makeCopy()
    {
        return new PField_Random().setRandom(random);
    }

    public PField_Random setRandom(boolean value)
    {
        this.random = value;
        return this;
    }

    public void setupEditor(PCLCustomCardEffectEditor<?> editor)
    {
        editor.registerBoolean(PGR.core.strings.cedit_random, v -> random = v, random);
    }
}
