package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField;

import java.util.ArrayList;
import java.util.List;

public abstract class PMod_Branch<T extends PField, U> extends PMod<T>
{

    public PMod_Branch(PSkillData<T> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMod_Branch(PSkillData<T> data)
    {
        super(data);
    }

    public PMod_Branch(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMod_Branch(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    public final void branch(PCLUseInfo info, Iterable<U> items)
    {
        if (this.childEffect instanceof PMultiBase)
        {
            List<? extends PSkill> effects = ((PMultiBase<?>) this.childEffect).getSubEffects();
            for (U c : items)
            {
                for (int i = 0; i < effects.size(); i++)
                {
                    if (matchesBranch(c, i, info))
                    {
                        this.childEffect.use(info, i);
                        break;
                    }
                }
            }
        }
        else
        {
            this.childEffect.use(info);
        }
    }

    protected String getEffectTexts(boolean addPeriod)
    {
        List<? extends PSkill> effects = ((PMultiBase<?>) this.childEffect).getSubEffects();
        ArrayList<String> effectTexts = new ArrayList<>();
        for (int i = 0; i < effects.size(); i++)
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

    public final int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount;
    }

    public abstract boolean matchesBranch(U c, int i, PCLUseInfo info);
}
