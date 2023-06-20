package pinacolada.skills.skills;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

import java.util.ArrayList;

// Conds where the use check must happen in the use action
public abstract class PActiveCond<T extends PField> extends PCond<T> {
    public PActiveCond(PSkillData<T> data, PSkillSaveData content) {
        super(data, content);
    }

    public PActiveCond(PSkillData<T> data) {
        super(data);
    }

    public PActiveCond(PSkillData<T> data, PCLCardTarget target, int amount) {
        super(data, target, amount);
    }

    public PActiveCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra) {
        super(data, target, amount, extra);
    }

    public ArrayList<Integer> getQualifiers(PCLUseInfo info) {
        return fields.getQualifiers(info);
    }

    public String getQualifierText(int i) {
        return fields.getQualifierText(i);
    }

    public int getQualifierRange() {
        return fields.getQualiferRange();
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (childEffect != null) {
            useImpl(info, order, (i) -> childEffect.use(info, order), (i) -> {
            });
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean isUsing) {
        if (isUsing && childEffect != null) {
            useImpl(info, order, (i) -> childEffect.use(i, order), (i) -> {
            });
        }
    }

    protected abstract PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail);
}
