package pinacolada.skills.fields;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.orbs.PCLOrbData;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static pinacolada.skills.PSkill.EXTRA_CHAR;

public class PField_Orb extends PField_Random {
    public ArrayList<String> orbs = new ArrayList<>();

    public PField_Orb addOrb(PCLOrbData... powers) {
        for (PCLOrbData power : powers) {
            this.orbs.add(power.ID);
        }
        return this;
    }

    public PField_Orb addOrb(String... powers) {
        this.orbs.addAll(Arrays.asList(powers));
        return this;
    }

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Orb && orbs.equals(((PField_Orb) other).orbs) && ((PField_Orb) other).random == random;
    }

    public String getOrbAmountString() {
        String efString = skill.getRawString(PSkill.EFFECT_CHAR);
        return (!orbs.isEmpty() ? random ? getOrbOrString(orbs, efString) : getOrbAndString(orbs, efString) : TEXT.subjects_randomX(skill.plural(PGR.core.tooltips.orb)));
    }

    public String getOrbAndOrString() {
        return random ? getOrbOrString() : getOrbAndString();
    }

    public String getOrbAndString() {
        return getOrbAndString(orbs, skill.getAmountRawString());
    }

    public String getOrbAndString(Object amount) {
        return getOrbAndString(orbs, amount);
    }

    public String getOrbExtraString() {
        String orbStr = !orbs.isEmpty() ? getOrbAmountString() : skill.plural(PGR.core.tooltips.orb, EXTRA_CHAR);
        if (random) {
            orbStr = EUIRM.strings.numNoun(skill.getExtraRawString(), TEXT.subjects_randomX(orbStr));
        }
        else {
            if (skill.extra > 0) {
                orbStr = EUIRM.strings.numNoun(skill.getExtraRawString(), orbStr);
            }
            orbStr = skill.extra <= 0 ? TEXT.subjects_allX(orbStr) : TEXT.subjects_yourFirst(orbStr);
        }
        return orbStr;
    }

    public final FuncT1<Boolean, AbstractOrb> getOrbFilter() {
        return (c -> (orbs.isEmpty() || EUIUtils.any(orbs, orb -> orb.equals(c.ID))));
    }

    public String getOrbOrString() {
        return getOrbOrString(orbs, skill.getAmountRawString());
    }

    public String getOrbOrString(Object amount) {
        return getOrbOrString(orbs, amount);
    }

    public String getOrbString() {
        return getOrbString(orbs);
    }

    @Override
    public PField_Orb makeCopy() {
        return (PField_Orb) new PField_Orb().setOrb(orbs).setRandom(random).setNot(not);
    }

    public PField_Orb setOrb(Collection<String> orbs) {
        this.orbs.clear();
        this.orbs.addAll(orbs);
        return this;
    }

    public PField_Orb setOrb(PCLOrbData... powers) {
        return setOrb(EUIUtils.map(powers, po -> po.ID));
    }

    public PField_Orb setOrb(String... powers) {
        return setOrb(Arrays.asList(powers));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerOrb(orbs);
    }
}
