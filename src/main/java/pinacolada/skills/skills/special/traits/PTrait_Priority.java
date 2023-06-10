package pinacolada.skills.skills.special.traits;

import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PTrait;

public class PTrait_Priority extends PTrait<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PTrait_Priority.class, PField_Empty.class);

    public PTrait_Priority() {
        this(1);
    }

    public PTrait_Priority(int amount) {
        super(DATA, amount);
    }

    public PTrait_Priority(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubDescText() {
        return PGR.core.tooltips.timing.title;
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.timing.title;
    }

    @Override
    public float modifyMagicNumber(PCLUseInfo info, float amount) {
        return amount + this.amount;
    }
}
