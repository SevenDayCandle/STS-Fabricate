package pinacolada.skills.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.EUIColors;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.base.primary.PLimit_Limited;
import pinacolada.skills.skills.base.primary.PLimit_SemiLimited;
import pinacolada.utilities.GameUtilities;

public abstract class PLimit extends PPrimary<PField_Empty>
{
    protected boolean limitCache = false;

    public PLimit(PSkillData<PField_Empty> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PLimit(PSkillData<PField_Empty> data)
    {
        super(data, PCLCardTarget.None, 0);
    }

    public static PLimit_Limited limited()
    {
        return new PLimit_Limited();
    }

    public static PLimit_SemiLimited semiLimited()
    {
        return new PLimit_SemiLimited();
    }

    public PLimit setChild(PSkill<?> effect)
    {
        super.setChild(effect);
        return this;
    }

    public PLimit setChild(PSkill<?>... effects)
    {
        super.setChild(effects);
        return this;
    }

    @Override
    public void refresh(PCLUseInfo info, boolean conditionMet)
    {
        limitCache = canActivate(info);
        super.refresh(info, limitCache & conditionMet);
    }

    @Override
    public Color getConditionColor()
    {
        return GameUtilities.inBattle() && !limitCache ? EUIColors.gold(0.6f) : Settings.GOLD_COLOR;
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return getConditionRawString() + (childEffect != null ? ((childEffect instanceof PCond ? EFFECT_SEPARATOR : ": ") + childEffect.getText(addPeriod)) : PCLCoreStrings.period(addPeriod));
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (tryActivate(info) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (tryActivate(info) && childEffect != null)
        {
            childEffect.use(info, index);
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if ((isUsing ? tryActivate(info) : canActivate(info)) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    public boolean tryPassParent(PSkill<?> source, PCLUseInfo info)
    {
        return tryActivate(info);
    }

    abstract public boolean canActivate(PCLUseInfo info);
    abstract public boolean tryActivate(PCLUseInfo info);
}
