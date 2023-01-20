package pinacolada.skills;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.EUIColors;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActionCond;
import pinacolada.skills.skills.base.primary.PLimit_Limited;
import pinacolada.skills.skills.base.primary.PLimit_SemiLimited;
import pinacolada.utilities.GameUtilities;

// TODO refactor to remove unnecessary elements
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
    public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
    {
        limitCache = checkLimit(makeInfo(m), false, false);
        super.refresh(m, c, limitCache & conditionMet);
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
        if (checkLimit(info, true, false) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    public void use(PCLUseInfo info, int index)
    {
        if (checkLimit(info, true, false) && childEffect != null)
        {
            childEffect.use(info, index);
        }
    }

    public void use(PCLUseInfo info, boolean isUsing)
    {
        if (checkLimit(info, isUsing, false) && childEffect != null)
        {
            childEffect.use(info);
        }
    }

    // The try activation should not be triggered when the condition is not actually being used from a card effect or a power (i.e. when both isUsing and fromTrigger are false)
    public boolean checkLimit(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return canActivate(info) && checkChild(info, isUsing, fromTrigger) && testTry(info, isUsing);
    }

    // TODO ActionConds should be changed to use tryPassParent
    @Deprecated
    public final boolean checkChild(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return !(this.childEffect instanceof PCond) || this.childEffect instanceof PActionCond || ((PCond<?>) this.childEffect).checkCondition(info, isUsing, fromTrigger);
    }

    public boolean testTry(PCLUseInfo info, boolean isUsing)
    {
        if (!isUsing)
        {
            return true;
        }
        return tryActivate(info);
    }

    public boolean tryPassParent(PCLUseInfo info)
    {
        return checkLimit(info, true, false);
    }

    abstract public boolean canActivate(PCLUseInfo info);
    abstract public boolean tryActivate(PCLUseInfo info);
}
