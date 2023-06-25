package pinacolada.skills.skills;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

// Moves that utilize callbacks and set the PCLUseInfo with that callback data
public abstract class PCallbackMove<T extends PField> extends PMove<T> {
    public PCallbackMove(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PCallbackMove(PSkillData<T> data) {
        super(data);
    }

    public PCallbackMove(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PCallbackMove(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    public void use(PCLUseInfo info, PCLActions order) {
        use(info, order, __ -> {
        });
    }

    public abstract void use(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> callback);
}
