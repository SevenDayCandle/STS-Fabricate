package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PStatTrait;

@VisibleSkill
public class PTrait_DamageMultiplier extends PStatTrait<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PTrait_DamageMultiplier.class, PField_Not.class)
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
        return EUIRM.strings.numNoun(getAmountRawString() + "%", getDamageString(perspective));
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
        return amount * ((fields.not ? 0f : 1f) + (refreshAmount(info) / 100f));
    }
}
