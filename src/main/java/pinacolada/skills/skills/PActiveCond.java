package pinacolada.skills.skills;

import extendedui.EUIUtils;
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

    @Override
    public ArrayList<Integer> getQualifiers(PCLUseInfo info, boolean conditionPassed) {
        return EUIUtils.arrayList(conditionPassed ? 0 : 1);
    }

    public int getQualifierRange() {
        return fields.getQualiferRange();
    }

    public String getQualifierText(int i) {
        return fields.getQualifierText(i);
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        return capital(childEffect == null ? getSubText(perspective, requestor) : TEXT.cond_xToY(getSubText(perspective, requestor), childEffect.getText(perspective, requestor, false)), addPeriod) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        conditionMetCache = checkCondition(info, false, null);
        if (conditionMetCache && childEffect != null) {
            useImpl(info, order, (i) -> childEffect.use(info, order), (i) -> {});
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

    public void useFromBranch(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        conditionMetCache = checkCondition(info, false, null);
        if (conditionMetCache) {
            useImpl(info, order, onComplete, onFail);
        }
        else {
            onFail.invoke(info);
        }
    }

    protected abstract PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail);
}
