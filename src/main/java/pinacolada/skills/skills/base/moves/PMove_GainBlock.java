package pinacolada.skills.skills.base.moves;

import extendedui.utilities.ColoredString;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_GainBlock extends PMove
{
    public static final PSkillData DATA = register(PMove_GainBlock.class, PCLEffectType.General);

    public PMove_GainBlock()
    {
        this(1);
    }

    public PMove_GainBlock(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_GainBlock(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_GainBlock(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public ColoredString getColoredValueString()
    {
        return getColoredValueString(Math.abs(baseAmount), Math.abs(amount));
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.gainAmount("X", PGR.core.tooltips.block.title);
    }

    @Override
    public String getSubText()
    {
        if (target == PCLCardTarget.Self)
        {
            return TEXT.actions.gainAmount(getAmountRawString(), PGR.core.tooltips.block);
        }
        return TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), PGR.core.tooltips.block);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().gainBlock(amount);
        super.use(info);
    }
}
