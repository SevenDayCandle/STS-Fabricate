package pinacolada.skills.skills.base.moves;

import extendedui.utilities.ColoredString;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public abstract class PMove_Gain extends PMove<PField_Empty>
{
    public PMove_Gain(PSkillData<PField_Empty> data, PSkillSaveData content)
    {
        super(data, content);
    }

    public PMove_Gain(PSkillData<PField_Empty> data, int amount)
    {
        super(data, PCLCardTarget.Self, amount);
    }

    public abstract String gainText();

    @Override
    public ColoredString getColoredValueString()
    {
        return getColoredValueString(Math.abs(baseAmount), Math.abs(amount));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_gainAmount(TEXT.subjects_x, gainText());
    }

    @Override
    public String getSubText()
    {
        return amount < 0 ? TEXT.act_loseAmount(getAmountRawString(), gainText()) : TEXT.act_gainAmount(getAmountRawString(), gainText());
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }
}
