package pinacolada.skills.fields;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

// TODO allow saving of PCLEffekseerEFX
public class PField_Attack extends PField {
    public AbstractGameAction.AttackEffect attackEffect = AbstractGameAction.AttackEffect.NONE;
    public Color vfxColor;
    public Color vfxTargetColor;

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Attack
                && attackEffect.equals(((PField_Attack) other).attackEffect)
                && vfxColor.equals(((PField_Attack) other).vfxColor)
                && vfxTargetColor.equals(((PField_Attack) other).vfxTargetColor);
    }

    @Override
    public PField_Attack makeCopy() {
        return new PField_Attack().setAttackEffect(attackEffect).setVFXColor(vfxColor != null ? vfxColor.cpy() : null, vfxTargetColor != null ? vfxTargetColor.cpy() : null);
    }

    public PField_Attack setAttackEffect(AbstractGameAction.AttackEffect effect) {
        this.attackEffect = effect != null ? effect : AbstractGameAction.AttackEffect.NONE;
        return this;
    }

    public PField_Attack setVFXColor(Color vfxColor, Color vfxTargetColor) {
        this.vfxColor = vfxColor;
        this.vfxTargetColor = vfxTargetColor;
        return this;
    }

    public PField_Attack setVFXColor(Color vfxColor) {
        this.vfxColor = vfxColor;
        return this;
    }

    public PField_Attack setVFXTargetColor(Color vfxColor) {
        this.vfxTargetColor = vfxColor;
        return this;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        editor.registerDropdown(PCLAttackVFX.keys()
                , EUIUtils.arrayList(attackEffect)
                , item -> {
                    if (item.size() > 0) {
                        attackEffect = item.get(0);
                    }
                }
                , item -> StringUtils.capitalize(item.toString().toLowerCase()),
                PGR.core.strings.cedit_attackEffect,
                true,
                false, true).setTooltip(PGR.core.strings.cedit_attackEffect, PGR.core.strings.cetut_attackEffect);
    }
}
