package pinacolada.skills.skills.base.traits;

import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PBlockTrait;

@VisibleSkill
public class PTrait_Block extends PBlockTrait<PField_Empty> {

    public static final PSkillData<PField_Empty> DATA = register(PTrait_Block.class, PField_Empty.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_Block() {
        this(1);
    }

    public PTrait_Block(int amount) {
        super(DATA, amount);
    }

    public PTrait_Block(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubText() {
        if (isVerbose()) {
            return TEXT.act_gainAmount(getAmountRawString(), getSubDescText());
        }
        return super.getSubText();
    }

    @Override
    public String getSubDescText() {
        return PGR.core.tooltips.block.getTitleOrIcon();
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.block.title;
    }

    @Override
    public boolean isDetrimental() {
        return amount < 0;
    }

    @Override
    public float modifyBlock(PCLUseInfo info, float amount) {
        return amount + this.amount;
    }
}
