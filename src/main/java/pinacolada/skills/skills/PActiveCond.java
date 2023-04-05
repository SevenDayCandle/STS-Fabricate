package pinacolada.skills.skills;

import extendedui.interfaces.delegates.ActionT0;
import pinacolada.actions.PCLAction;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

// Conds where the use check must happen in the use action
public abstract class PActiveCond<T extends PField> extends PCond<T>
{
    public PActiveCond(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PActiveCond(PSkillData<T> data)
    {
        super(data);
    }

    public PActiveCond(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PActiveCond(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info), () -> {});
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info, index), () -> {});
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (isUsing && childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info), () -> {});
        }
    }

    protected abstract PCLAction<?> useImpl(PCLUseInfo info, ActionT0 onComplete, ActionT0 onFail);
}
