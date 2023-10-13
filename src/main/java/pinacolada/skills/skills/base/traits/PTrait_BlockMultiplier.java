package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.fields.PField_Not;
import pinacolada.skills.skills.PBlockTrait;

@VisibleSkill
public class PTrait_BlockMultiplier extends PBlockTrait<PField_Not> {

    public static final PSkillData<PField_Not> DATA = register(PTrait_BlockMultiplier.class, PField_Not.class)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Power);

    public PTrait_BlockMultiplier() {
        this(1);
    }

    public PTrait_BlockMultiplier(int amount) {
        super(DATA, amount);
    }

    public PTrait_BlockMultiplier(PSkillSaveData content) {
        super(DATA, content);
    }

    public String getSampleAmount() {
        return "+X%";
    }

    @Override
    public String getSubDescText(PCLCardTarget perspective) {
        return EUIRM.strings.numNoun(getAmountRawString() + "%", PGR.core.tooltips.block);
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
    public float modifyBlockFirst(PCLUseInfo info, float amount) {
        return amount * ((fields.not ? 0f : 1f) + (this.amount / 100f));
    }
}
