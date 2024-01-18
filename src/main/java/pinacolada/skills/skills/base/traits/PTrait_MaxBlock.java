package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PTrait_MaxBlock extends PTrait<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PTrait_MaxBlock.class, PField_Not.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_MaxBlock() {
        this(1);
    }

    public PTrait_MaxBlock(int amount) {
        super(DATA, amount);
    }

    public PTrait_MaxBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return TEXT.subjects_max(EUIRM.strings.numNoun(getAmountRawString(), PGR.core.tooltips.block));
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_max(PGR.core.tooltips.block.title);
    }

    @Override
    public float modifyBlockLast(PCLUseInfo info, float amount) {
        int actualAmount = refreshAmount(info);
        return Math.min(amount, actualAmount);
    }

    @Override
    public boolean shouldHideText() {
        return !fields.not && baseAmount == 0 && !hasParentType(PMod.class);
    }
}
