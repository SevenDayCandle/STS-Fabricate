package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PStatTrait;

@VisibleSkill
public class PTrait_BlockCount extends PStatTrait<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PTrait_BlockCount.class, PField_Not.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_BlockCount() {
        this(1);
    }

    public PTrait_BlockCount(int amount) {
        super(DATA, amount);
    }

    public PTrait_BlockCount(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return EUIRM.strings.numNoun(getAmountRawString(), TEXT.subjects_count(PGR.core.tooltips.block));
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_count(PGR.core.tooltips.block.title);
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }

    @Override
    public float modifyRightCount(PCLUseInfo info, float amount) {
        int actualAmount = refreshAmount(info);
        return fields.not ? actualAmount : amount + actualAmount;
    }
}
