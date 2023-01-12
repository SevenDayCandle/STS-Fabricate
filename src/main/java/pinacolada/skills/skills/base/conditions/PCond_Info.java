package pinacolada.skills.skills.base.conditions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.EUIColors;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActionCond;
import pinacolada.utilities.GameUtilities;

// TODO move out of PCond
public abstract class PCond_Info extends PCond<PField_Empty>
{
    public PCond_Info(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_Info(PSkillData<PField_Empty> data)
    {
        super(data, PCLCardTarget.None, 0);
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
        return !(this.childEffect instanceof PCond) || this.childEffect instanceof PActionCond || ((PCond) this.childEffect).checkCondition(info, isUsing, fromTrigger);
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
