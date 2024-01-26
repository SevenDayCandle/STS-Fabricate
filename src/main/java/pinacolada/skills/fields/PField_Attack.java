package pinacolada.skills.fields;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.effects.EffekseerEFK;
import pinacolada.effects.PCLAttackVFX;
import pinacolada.resources.PGR;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

public class PField_Attack extends PField {
    public AbstractGameAction.AttackEffect attackEffect = AbstractGameAction.AttackEffect.NONE;
    public Color vfxColor;
    public Color vfxTargetColor;
    public EffekseerEFK effekseer;

    @Override
    public boolean equals(PField other) {
        return other instanceof PField_Attack
                && attackEffect.equals(((PField_Attack) other).attackEffect)
                && ((vfxColor != null && vfxColor.equals(((PField_Attack) other).vfxColor)) || ((PField_Attack) other).vfxColor == null)
                && ((vfxTargetColor != null && vfxTargetColor.equals(((PField_Attack) other).vfxTargetColor))|| ((PField_Attack) other).vfxTargetColor == null)
                && ((effekseer != null && effekseer.equals(((PField_Attack) other).effekseer))|| ((PField_Attack) other).effekseer == null);
    }

    @Override
    public PField_Attack makeCopy() {
        return new PField_Attack()
                .setAttackEffect(attackEffect).setVFXColor(vfxColor != null ? vfxColor.cpy() : null, vfxTargetColor != null ? vfxTargetColor.cpy() : null)
                .setEffekseer(effekseer);
    }

    public PField_Attack setAttackEffect(AbstractGameAction.AttackEffect effect) {
        this.attackEffect = effect != null ? effect : AbstractGameAction.AttackEffect.NONE;
        return this;
    }

    public PField_Attack setEffekseer(EffekseerEFK efk) {
        this.effekseer = efk;
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
                    if (!item.isEmpty()) {
                        attackEffect = item.get(0);
                    }
                }
                , item -> StringUtils.capitalize(item.toString().toLowerCase()),
                PGR.core.strings.cedit_attackEffect,
                true,
                false, true)
                .setShowClearForSingle(true)
                .setTooltip(PGR.core.strings.cedit_attackEffect, PGR.core.strings.cetut_attackEffect);
        editor.registerDropdown(EffekseerEFK.sortedValues()
                , EUIUtils.arrayList(effekseer)
                , item -> {
                    if (!item.isEmpty()) {
                        effekseer = item.get(0);
                    }
                }
                , item -> StringUtils.capitalize(item.toString().toLowerCase()),
                PGR.core.strings.cedit_effekseer,
                true,
                false, true)
                .setShowClearForSingle(true)
                .setTooltip(PGR.core.strings.cedit_effekseer, PGR.core.strings.cetut_effekseer);
    }
}
