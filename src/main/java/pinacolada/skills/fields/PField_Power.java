package pinacolada.skills.fields;

import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_Power extends PField
{
    public ArrayList<PCLPowerHelper> powers = new ArrayList<>();
    public boolean random;
    public boolean debuff;

    @Override
    public boolean equals(PField other)
    {
        return other instanceof PField_Power && powers.equals(((PField_Power) other).powers) && ((PField_Power) other).random == random && ((PField_Power) other).debuff == debuff;
    }

    @Override
    public PField_Power makeCopy()
    {
        return (PField_Power) new PField_Power().setPower(powers).setRandom(random);
    }

    public void setupEditor(PCLCustomCardEffectEditor editor)
    {
        editor.registerPower(powers);
        super.setupEditor(editor);
        editor.registerBoolean(PGR.core.tooltips.debuff.title, v -> debuff = v, debuff);
    }

    public PField_Power setPower(PCLPowerHelper... powers)
    {
        return setPower(Arrays.asList(powers));
    }

    public PField_Power setPower(List<PCLPowerHelper> powers)
    {
        this.powers.clear();
        this.powers.addAll(powers);
        return this;
    }

    public PField_Power setDebuff(boolean value)
    {
        this.debuff = value;
        return this;
    }

    public PField_Power setRandom(boolean value)
    {
        this.random = value;
        return this;
    }

    public final FuncT1<Boolean, AbstractPower> getPowerFilter()
    {
        return (c -> (powers.isEmpty() || EUIUtils.any(powers, power -> power.ID.equals(c.ID))));
    }

    public String getBuffString() {return debuff ? PGR.core.tooltips.debuff.title : PGR.core.tooltips.buff.title;}

    public String getPowerAndString()
    {
        return getPowerAndString(powers);
    }

    public String getPowerOrString()
    {
        return getPowerOrString(powers);
    }

    public String getPowerAndOrString()
    {
        return random ? getPowerOrString() : getPowerAndString();
    }

    public String getPowerString()
    {
        return getPowerString(powers);
    }

    public String getPowerSubjectString()
    {
        return powers.isEmpty() ? getBuffString() : getPowerAndOrString();
    }
}
