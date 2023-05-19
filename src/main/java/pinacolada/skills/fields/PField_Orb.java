package pinacolada.skills.fields;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static pinacolada.skills.PSkill.EXTRA_CHAR;

public class PField_Orb extends PField_Random {
    public ArrayList<PCLOrbHelper> orbs = new ArrayList<>();

    public PField_Orb addOrb(PCLOrbHelper... orbs) {
        this.orbs.addAll(Arrays.asList(orbs));
        return this;
    }

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Orb && orbs.equals(((PField_Orb) other).orbs) && ((PField_Orb) other).random == random;
    }

    @Override
    public PField_Orb makeCopy() {
        return (PField_Orb) new PField_Orb().setOrb(orbs).setRandom(random).setNot(not);
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerOrb(orbs);
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
        return (c -> (orbs.isEmpty() || EUIUtils.any(orbs, orb -> orb.ID.equals(c.ID))));
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

    public PField_Orb setOrb(Collection<PCLOrbHelper> orbs) {
        this.orbs.clear();
        this.orbs.addAll(orbs);
        return this;
    }

    public PField_Orb setOrb(PCLOrbHelper... orbs) {
        return setOrb(Arrays.asList(orbs));
    }
}
