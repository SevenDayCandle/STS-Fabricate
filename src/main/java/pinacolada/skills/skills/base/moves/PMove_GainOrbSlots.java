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
public class PMove_GainOrbSlots extends PMove_Gain {
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainOrbSlots.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .noTarget();

    public PMove_GainOrbSlots() {
        this(1);
    }

    public PMove_GainOrbSlots(int amount) {
        super(DATA, amount);
    }

    public PMove_GainOrbSlots(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String gainText() {
        return PGR.core.tooltips.orbSlot.getTitleOrIcon();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.orbSlot.title);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.gainOrbSlots(amount);
        super.use(info, order);
    }
}
