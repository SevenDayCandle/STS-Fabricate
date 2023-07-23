package pinacolada.skills.skills.base.traits;

import extendedui.EUIRM;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PBlockTrait;

@VisibleSkill
public class PTrait_BlockMultiplier extends PBlockTrait<PField_Empty> {

    public static final PSkillData<PField_Empty> DATA = register(PTrait_BlockMultiplier.class, PField_Empty.class)
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
    public String getSubText(PCLCardTarget perspective) {
        if (isVerbose()) {
            return TEXT.act_gainAmount(getAmountRawString() + "%", getSubDescText());
        }
        return EUIRM.strings.numNoun(getAmountRawString() + "%", getSubDescText());
    }

    @Override
    public String getSubDescText() {
        return PGR.core.tooltips.block.toString();
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
        return amount * (1f + (this.amount / 100f));
    }
}
