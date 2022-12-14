package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIUtils;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PMultiBase;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.ArrayList;
import java.util.List;

public abstract class PMod_Branch<T> extends PMod
{

    public PMod_Branch(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_Branch(PSkillData data)
    {
        super(data);
    }

    public PMod_Branch(PSkillData data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMod_Branch(PSkillData data, PCLCardTarget target, int amount, PCLCardGroupHelper... groups)
    {
        super(data, target, amount, groups);
    }

    public PMod_Branch(PSkillData data, PCLCardTarget target, int amount, PSkill effect)
    {
        super(data, target, amount, effect);
    }

    public PMod_Branch(PSkillData data, PCLCardTarget target, int amount, PSkill... effect)
    {
        super(data, target, amount, effect);
    }

    public PMod_Branch(PSkillData data, PCLCardTarget target, int amount, PCLAffinity... affinities)
    {
        super(data, target, amount, affinities);
    }

    public PMod_Branch(PSkillData data, PCLCardTarget target, int amount, PCLOrbHelper... orbs)
    {
        super(data, target, amount, orbs);
    }

    public PMod_Branch(PSkillData data, PCLCardTarget target, int amount, PCLPowerHelper... powerHelpers)
    {
        super(data, target, amount, powerHelpers);
    }

    public final void branch(PCLUseInfo info, Iterable<T> items)
    {
        if (this.childEffect instanceof PMultiBase)
        {
            List<? extends PSkill> effects = ((PMultiBase<?>) this.childEffect).getSubEffects();
            for (T c : items)
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

    public abstract boolean matchesBranch(T c, int i, PCLUseInfo info);
}
