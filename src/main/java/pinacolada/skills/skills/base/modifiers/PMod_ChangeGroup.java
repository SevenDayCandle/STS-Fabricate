package pinacolada.skills.skills.base.modifiers;

import extendedui.interfaces.delegates.ActionT0;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public abstract class PMod_ChangeGroup extends PMod
{

    public PMod_ChangeGroup(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_ChangeGroup(PSkillData data)
    {
        super(data, PCLCardTarget.None, 1);
    }

    public PMod_ChangeGroup(PSkillData data, PCLCardGroupHelper... groups)
    {
        super(data, PCLCardTarget.None, 1, groups);
    }

    public abstract String getConditionSampleText();

    public String getConditionText()
    {
        return getConditionSampleText();
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.numIf(TEXT.subjects.from("X"), getConditionSampleText());
    }

    @Override
    public String getSubText()
    {
        return TEXT.conditions.numIf(getGroupString(), getConditionText());
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return TEXT.conditions.genericConditional(childEffect != null ? capital(childEffect.getText(false), addPeriod) : "", getSubText()) + PCLCoreStrings.period(addPeriod);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info, index));
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (isUsing && childEffect != null)
        {
            useImpl(info, () -> childEffect.use(info));
        }
    }

    public abstract boolean meetsCondition(PCLUseInfo info);

    protected void useImpl(PCLUseInfo info, ActionT0 callback)
    {
        if (meetsCondition(info))
        {
            if (this.childEffect instanceof PMultiBase)
            {
                for (PSkill ce : ((PMultiBase<?>) this.childEffect).getSubEffects())
                {
                    ce.setTemporaryGroups(this.groupTypes);
                }
            }
            else
            {
                this.childEffect.setTemporaryGroups(this.groupTypes);
            }
        }
        callback.invoke();
        if (this.childEffect instanceof PMultiBase)
        {
            for (PSkill ce : ((PMultiBase<?>) this.childEffect).getSubEffects())
            {
                ce.resetTemporaryGroups();
            }
        }
        else
        {
            this.childEffect.resetTemporaryGroups();
        }
    }
}
