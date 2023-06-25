package pinacolada.skills.skills.base.traits;

import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.PTrait;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PTrait_Unplayable extends PTrait<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PTrait_Unplayable.class, PField_Empty.class, 1, 1);

    public PTrait_Unplayable() {
        this(1);
    }

    public PTrait_Unplayable(int amount) {
        super(DATA, amount);
    }

    public PTrait_Unplayable(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public boolean canPlay(PCLUseInfo info) {
        return info == null || info.card == sourceCard;
    }

    @Override
    public boolean isDetrimental() {
        return true;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return PGR.core.tooltips.unplayable.title;
    }

    @Override
    public String getSubText() {
        return PGR.core.tooltips.unplayable.title;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {

    }

    @Override
    public String getSubDescText() {
        return PGR.core.tooltips.unplayable.title;
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.unplayable.title;
    }
}
