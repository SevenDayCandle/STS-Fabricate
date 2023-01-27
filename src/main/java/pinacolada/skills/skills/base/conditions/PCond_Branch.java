package pinacolada.skills.skills.base.conditions;

import extendedui.EUIUtils;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

import java.util.ArrayList;

public abstract class PCond_Branch<T extends PField, U> extends PCond<T>
{

    public PCond_Branch(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PCond_Branch(PSkillData<T> data)
    {
        super(data);
    }

    public PCond_Branch(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PCond_Branch(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    public final void branch(PCLUseInfo info, Iterable<U> items)
    {
        for (U c : items)
        {
            for (int i = 0; i < getEffectCount(); i++)
            {
                if (matchesBranch(c, i, info))
                {
                    this.childEffect.use(info, i);
                    break;
                }
            }
        }
    }

    // Obtain the number of total leaf nodes stemming out of this branch
    protected final int getEffectCount()
    {
        PSkill<?> current = this.childEffect;
        while (current != null)
        {
            if (current instanceof PMultiBase<?>)
            {
                return ((PMultiBase<?>) current).getSubEffects().size();
            }
            current = current.getChild();
        }
        return 1;
    }

    // TODO highlight matching branches with green
    protected String getEffectTexts(boolean addPeriod)
    {
        ArrayList<String> effectTexts = new ArrayList<>();
        for (int i = 0; i < getEffectCount(); i++)
        {
            effectTexts.add(getQualifier(i) + " -> " + this.childEffect.getText(i, addPeriod));
        }
        return EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, effectTexts);
    }

    public abstract String getQualifier(int i);

    @Override
    public String getText(boolean addPeriod)
    {
        if (this.childEffect instanceof PMultiBase)
        {
            return getSubText() + ": | " + getEffectTexts(addPeriod);
        }
        return getSubText();
    }

    public final void use(PCLUseInfo info, int index)
    {
        use(info);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return false;
    }

    public abstract boolean matchesBranch(U c, int i, PCLUseInfo info);
}
