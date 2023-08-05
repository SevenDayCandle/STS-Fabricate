package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDamageTrait;

@VisibleSkill
public class PTrait_DamageMultiplier extends PDamageTrait<PField_Empty> {

    public static final PSkillData<PField_Empty> DATA = register(PTrait_DamageMultiplier.class, PField_Empty.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_DamageMultiplier() {
        this(1);
    }

    public PTrait_DamageMultiplier(int amount) {
        super(DATA, amount);
    }

    public PTrait_DamageMultiplier(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleAmount() {
        return "+X%";
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return EUIRM.strings.numNoun(getAmountRawString() + "%", getAttackTooltip().getTitleOrIcon());
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_damage;
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }

    @Override
    public float modifyDamageGiveFirst(PCLUseInfo info, float amount) {
        return amount * (1 + (this.amount / 100f));
    }
}
