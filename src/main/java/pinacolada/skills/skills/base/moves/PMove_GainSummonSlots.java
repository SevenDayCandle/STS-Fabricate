package pinacolada.skills.skills.base.moves;

import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainSummonSlots extends PMove_Gain {
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainSummonSlots.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .noTarget();

    public PMove_GainSummonSlots() {
        this(1);
    }

    public PMove_GainSummonSlots(int amount) {
        super(DATA, amount);
    }

    public PMove_GainSummonSlots(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String gainText() {
        return plural(PGR.core.tooltips.summonSlot);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.summonSlot.title);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.gainSummonSlots(refreshAmount(info));
        super.use(info, order);
    }
}
