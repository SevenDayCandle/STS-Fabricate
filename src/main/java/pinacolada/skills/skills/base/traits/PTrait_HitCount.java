package pinacolada.skills.skills.base.traits;

import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDamageTrait;

@VisibleSkill
public class PTrait_HitCount extends PDamageTrait<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PTrait_HitCount.class, PField_Empty.class);

    public PTrait_HitCount() {
        this(1);
    }

    public PTrait_HitCount(int amount) {
        super(DATA, amount);
    }

    public PTrait_HitCount(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubText() {
        if (PGR.config.expandAbbreviatedEffects.get()) {
            return TEXT.act_hasAmount(getAmountRawString(), getSubDescText());
        }
        return super.getSubText();
    }

    @Override
    public String getSubDescText() {
        return TEXT.subjects_hits;
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_hits;
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }

    @Override
    public float modifyHitCount(PCLUseInfo info, float amount) {
        return amount + this.amount;
    }
}
