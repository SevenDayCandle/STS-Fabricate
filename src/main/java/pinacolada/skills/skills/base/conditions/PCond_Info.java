package pinacolada.skills.skills.base.conditions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.EUIColors;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.PCondWithoutCheck;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

public abstract class PCond_Info extends PCond
{
    public PCond_Info(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_Info(PSkillData data)
    {
        super(data, PCLCardTarget.None, 0);
    }

    public PCond_Info(PSkillData data, PSkill effect)
    {
        this(data);
        setChild(effect);
    }

    public PCond_Info(PSkillData data, PSkill... effect)
    {
        this(data);
        setChild(effect);
    }

    @Override
    public Color getConditionColor()
    {
        return GameUtilities.inBattle() && !conditionMetCache ? EUIColors.gold(0.6f) : Settings.GOLD_COLOR;
    }

    // The try activation should not be triggered when the condition is not actually being used from a card effect or a power (i.e. when both isUsing and fromTrigger are false)
    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return canActivate(info) && checkChild(info, isUsing, fromTrigger) && testTry(info, isUsing);
    }

    public final boolean checkChild(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return !(this.childEffect instanceof PCond) || this.childEffect instanceof PCondWithoutCheck || ((PCond) this.childEffect).checkCondition(info, isUsing, fromTrigger);
    }

    public boolean testTry(PCLUseInfo info, boolean isUsing)
    {
        if (!isUsing)
        {
            return true;
        }
        return tryActivate(info);
    }

    abstract public boolean canActivate(PCLUseInfo info);
    abstract public boolean tryActivate(PCLUseInfo info);
}
