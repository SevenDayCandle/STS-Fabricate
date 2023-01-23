package pinacolada.skills.fields;

import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PField_AttackType extends PField_Random
{
    public ArrayList<PCLAttackType> attackTypes = new ArrayList<>();
    public boolean random;

    @Override
    public boolean equals(PField other)
    {
        return other instanceof PField_AttackType && attackTypes.equals(((PField_AttackType) other).attackTypes) && ((PField_AttackType) other).random == random;
    }

    @Override
    public PField_AttackType makeCopy()
    {
        return (PField_AttackType) new PField_AttackType().setAttackType(attackTypes).setRandom(random);
    }

    public void setupEditor(PCLCustomCardEffectEditor<?> editor)
    {
        editor.registerDropdown(Arrays.asList(PCLAttackType.values()), attackTypes, t -> t.getTooltip().getTitleOrIcon(), PGR.core.strings.cardEditor.attackType, true);
        super.setupEditor(editor);
    }

    public PField_AttackType setAttackType(PCLAttackType... orbs)
    {
        return setAttackType(Arrays.asList(orbs));
    }

    public PField_AttackType setAttackType(List<PCLAttackType> orbs)
    {
        this.attackTypes.clear();
        this.attackTypes.addAll(orbs);
        return this;
    }
}
