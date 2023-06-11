package pinacolada.skills.skills.base.traits;

import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PBlockTrait;

@VisibleSkill
public class PTrait_BlockCount extends PBlockTrait<PField_Empty> {

    public static final PSkillData<PField_Empty> DATA = register(PTrait_BlockCount.class, PField_Empty.class);

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
    public String getSubText() {
        if (isVerbose()) {
            return TEXT.act_hasAmount(getAmountRawString(), getSubDescText());
        }
        return super.getSubText();
    }

    @Override
    public String getSubDescText() {
        return TEXT.subjects_count(PGR.core.tooltips.block);
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
        return amount + this.amount;
    }
}
