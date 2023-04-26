package pinacolada.skills.skills.special.traits;

import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

public class PTrait_HP extends PTrait<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PTrait_HP.class, PField_Empty.class);

    public PTrait_HP() {
        this(1);
    }

    public PTrait_HP(int amount) {
        super(DATA, amount);
    }

    public PTrait_HP(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubDescText() {
        return PGR.core.tooltips.hp.title;
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.hp.title;
    }

    @Override
    public float modifyHeal(PCLUseInfo info, float amount) {
        return amount + this.amount;
    }
}
