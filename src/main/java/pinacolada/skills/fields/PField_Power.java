package pinacolada.skills.fields;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.powers.PCLPowerData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_Power extends PField_Random {
    public ArrayList<String> powers = new ArrayList<>();
    public boolean debuff;

    public static boolean checkPowers(String po, FuncT1<Boolean, String> valFunc) {
        PCLPowerData data = PCLPowerData.getStaticDataOrCustom(po);
        if (data != null) {
            return data.ifAny(valFunc);
        }
        return valFunc.invoke(po);
    }

    public PField_Power addPower(PCLPowerData... powers) {
        for (PCLPowerData power : powers) {
            this.powers.add(power.ID);
        }
        return this;
    }

    public PField_Power addPower(String... powers) {
        this.powers.addAll(Arrays.asList(powers));
        return this;
    }

    public boolean allOrAnyPower(PCLUseInfo info, AbstractCreature t) {
        return allOrAnyPower(po -> doesValueMatchThreshold(info, GameUtilities.getPowerAmount(t, po)));
    }

    public boolean allOrAnyPower(FuncT1<Boolean, String> valFunc) {
        return allOrAnyR(powers, po -> checkPowers(po, valFunc));
    }

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Power && powers.equals(((PField_Power) other).powers) && ((PField_Power) other).random == random && ((PField_Power) other).debuff == debuff;
    }

    public String getBuffString() {
        return getBuffString(skill.amount);
    }

    public String getBuffString(int amount) {
        return PCLCoreStrings.plural(debuff ? PGR.core.tooltips.debuff : PGR.core.tooltips.buff, amount);
    }

    public String getPowerAndOrString() {
        return random ? getPowerOrString() : getPowerAndString();
    }

    public String getPowerAndString() {
        return getPowerAndString(powers);
    }

    public final FuncT1<Boolean, AbstractPower> getPowerFilter() {
        return (c -> (powers.isEmpty() || EUIUtils.any(powers, power -> power.equals(c.ID))));
    }

    public String getPowerOrString() {
        return getPowerOrString(powers);
    }

    public String getPowerString() {
        return getPowerString(powers);
    }

    public String getPowerSubjectString() {
        return powers.isEmpty() ? getBuffString() : getPowerAndOrString();
    }

    @Override
    public PField_Power makeCopy() {
        return (PField_Power) new PField_Power().setPower(powers).setRandom(random).setDebuff(debuff).setNot(not);
    }

    public PField_Power setDebuff(boolean value) {
        this.debuff = value;
        return this;
    }

    public PField_Power setPower(Collection<String> powers) {
        this.powers.clear();
        this.powers.addAll(powers);
        return this;
    }

    public PField_Power setPower(PCLPowerData... powers) {
        return setPower(EUIUtils.map(powers, po -> po.ID));
    }

    public PField_Power setPower(String... powers) {
        return setPower(Arrays.asList(powers));
    }

    public PField_Power setRandom(boolean value) {
        this.random = value;
        return this;
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerPower(powers);
        editor.registerBoolean(PGR.core.tooltips.debuff.title, PGR.core.tooltips.debuff.description, v -> debuff = v, debuff);
    }
}
