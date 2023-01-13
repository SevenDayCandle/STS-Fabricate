package pinacolada.skills.fields;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import extendedui.EUIUtils;
import pinacolada.effects.AttackEffects;
import pinacolada.resources.PGR;
import pinacolada.ui.cardEditor.PCLCustomCardEffectEditor;

public class PField_AttackEffect extends PField
{
    public AbstractGameAction.AttackEffect attackEffect = AbstractGameAction.AttackEffect.NONE;

    @Override
    public boolean equals(PField other)
    {
        return other instanceof PField_AttackEffect && attackEffect.equals(((PField_AttackEffect) other).attackEffect);
    }

    @Override
    public PField_AttackEffect makeCopy()
    {
        return (PField_AttackEffect) new PField_AttackEffect().setAttackEffect(attackEffect);
    }

    public void setupEditor(PCLCustomCardEffectEditor editor)
    {
        editor.registerDropdown(AttackEffects.keys(), EUIUtils.list(attackEffect), Enum::name, PGR.core.strings.cardEditor.attackEffect, false);
        super.setupEditor(editor);
    }

    public PField_AttackEffect setAttackEffect(AbstractGameAction.AttackEffect effect)
    {
        this.attackEffect = effect;
        return this;
    }
}
