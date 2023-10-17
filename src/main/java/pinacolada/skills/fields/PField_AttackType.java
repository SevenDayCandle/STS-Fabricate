package pinacolada.skills.fields;

import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.monsters.PCLIntentType;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class PField_AttackType extends PField_Random {
    public ArrayList<PCLAttackType> attackTypes = new ArrayList<>();

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_AttackType && attackTypes.equals(((PField_AttackType) other).attackTypes) && ((PField_AttackType) other).random == random && ((PField_AttackType) other).not == not;
    }

    @Override
    public PField_AttackType makeCopy() {
        return (PField_AttackType) new PField_AttackType().setAttackType(attackTypes).setRandom(random).setNot(not);
    }

    public PField_AttackType setAttackType(Collection<PCLAttackType> nt) {
        this.attackTypes.clear();
        for (PCLAttackType t : nt) {
            if (t != null) {
                this.attackTypes.add(t);
            }
        }
        return this;
    }

    public PField_AttackType setAttackType(PCLAttackType... orbs) {
        return setAttackType(Arrays.asList(orbs));
    }

    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerDropdown(Arrays.asList(PCLAttackType.values()), attackTypes, t -> t.getTooltip().getTitleOrIcon(), PGR.core.strings.cedit_attackType, true);
        super.setupEditor(editor);
    }
}
