package pinacolada.skills.fields;

import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_RelicID extends PField_Random
{
    public ArrayList<String> relicIDs = new ArrayList<>();

    @Override
    public boolean equals(PField other)
    {
        return other instanceof PField_RelicID && relicIDs.equals(((PField_RelicID) other).relicIDs) && ((PField_RelicID) other).random == random;
    }

    @Override
    public PField_RelicID makeCopy()
    {
        return (PField_RelicID) new PField_RelicID().setRelicID(relicIDs).setRandom(random);
    }

    // TODO custom dropdown for relics
    public void setupEditor(PCLCustomCardEffectEditor editor)
    {
        super.setupEditor(editor);
    }

    public PField_RelicID setRelicID(String... orbs)
    {
        return setRelicID(Arrays.asList(orbs));
    }

    public PField_RelicID setRelicID(List<String> orbs)
    {
        this.relicIDs.clear();
        this.relicIDs.addAll(orbs);
        return this;
    }

    public String getRelicIDAndString()
    {
        return getRelicIDAndString(relicIDs);
    }

    public String getRelicIDOrString()
    {
        return getRelicIDOrString(relicIDs);
    }
}
