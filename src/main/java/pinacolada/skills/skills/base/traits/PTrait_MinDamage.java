package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PDamageTrait;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PTrait_MinDamage extends PDamageTrait<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PTrait_MinDamage.class, PField_Not.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_MinDamage() {
        this(1);
    }

    public PTrait_MinDamage(int amount) {
        super(DATA, amount);
    }

    public PTrait_MinDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return getSubSampleText();
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return TEXT.subjects_min(EUIRM.strings.numNoun(getAmountRawString(), getDamageString(perspective)));
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_min(TEXT.subjects_damage);
    }

    @Override
    public float modifyDamageGiveLast(PCLUseInfo info, float amount) {
        int actualAmount = refreshAmount(info);
        return Math.max(amount, actualAmount);
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        fields.setupEditor(editor);
    }

    @Override
    public String wrapTextAmountSelf(int input) {
        return String.valueOf(input);
    }
}
