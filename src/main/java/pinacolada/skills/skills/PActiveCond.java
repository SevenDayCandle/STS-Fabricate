package pinacolada.skills.skills;

import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

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

    public int getQualifierRange() {
        return fields.getQualiferRange();
    }

    public String getQualifierText(int i) {
        return fields.getQualifierText(i);
    }

    @Override
    public String getText(PCLCardTarget perspective, boolean addPeriod) {
        return capital(childEffect == null ? getSubText(perspective) : TEXT.cond_xToY(getSubText(perspective), childEffect.getText(perspective, false)), addPeriod) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (childEffect != null) {
            useImpl(info, order, (i) -> childEffect.use(info, order), (i) -> {
            });
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if (shouldPay) {
            use(info, order);
        }
        else if (childEffect != null) {
            childEffect.use(info, order);
        }
    }

    protected abstract PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail);
}
