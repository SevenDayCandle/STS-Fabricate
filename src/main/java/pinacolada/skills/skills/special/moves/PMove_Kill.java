package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.DieAction;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PMove_Kill extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_Kill.class, PField_Empty.class, 1, 1);

    public PMove_Kill() {
        this(1);
    }

    public PMove_Kill(int amount) {
        super(DATA, PCLCardTarget.Single, amount);
    }

    public PMove_Kill(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_kill(TEXT.subjects_x);
    }

    @Override
    public String getSubText() {
        return TEXT.act_kill(getTargetString());
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        for (AbstractCreature target : getTargetList(info)) {
            order.add(new DieAction(target));
        }
        super.use(info, order);
    }
}
