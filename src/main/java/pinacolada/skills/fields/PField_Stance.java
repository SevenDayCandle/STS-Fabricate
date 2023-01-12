package pinacolada.skills.fields;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.resources.PGR;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_Stance extends PField_Random
{
    public ArrayList<PCLStanceHelper> stances = new ArrayList<>();
    public boolean random;

    @Override
    public boolean equals(PField other)
    {
        return other instanceof PField_Stance && stances.equals(((PField_Stance) other).stances) && ((PField_Stance) other).random == random;
    }

    @Override
    public PField_Stance makeCopy()
    {
        return (PField_Stance) new PField_Stance().setStance(stances).setRandom(random);
    }

    public void setupEditor(PCLCustomCardEffectEditor editor)
    {
        editor.registerStance(stances);
        super.setupEditor(editor);
    }

    public PField_Stance setStance(PCLStanceHelper... orbs)
    {
        return setStance(Arrays.asList(orbs));
    }

    public PField_Stance setStance(List<PCLStanceHelper> orbs)
    {
        this.stances.clear();
        this.stances.addAll(orbs);
        return this;
    }

    public String getAnyStanceString()
    {
        return stances.isEmpty() ? TEXT.conditions.any(PGR.core.tooltips.stance.title) : getStanceString();
    }

    public String getStanceString()
    {
        return getStanceString(stances);
    }

    public final FuncT1<Boolean, AbstractOrb> getOrbFilter()
    {
        return (c -> (stances.isEmpty() || EUIUtils.any(stances, orb -> orb.ID.equals(c.ID))));
    }
}
