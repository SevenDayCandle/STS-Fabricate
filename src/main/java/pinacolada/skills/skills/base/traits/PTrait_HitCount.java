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
public class PTrait_HitCount extends PStatTrait<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PTrait_HitCount.class, PField_Not.class)
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
    public String getSubDescText(PCLCardTarget perspective, Object requestor) {
        return EUIRM.strings.numNoun(getAmountRawString(requestor), TEXT.subjects_hits);
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
        int actualAmount = refreshAmount(info);
        return fields.not ? actualAmount : amount + actualAmount;
    }
}
