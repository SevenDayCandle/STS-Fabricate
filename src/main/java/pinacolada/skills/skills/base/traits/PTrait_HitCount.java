package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PDamageTrait;

@VisibleSkill
public class PTrait_HitCount extends PDamageTrait<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PTrait_HitCount.class, PField_Empty.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

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
    public String getSubDescText(PCLCardTarget perspective) {
        return EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_hits);
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
