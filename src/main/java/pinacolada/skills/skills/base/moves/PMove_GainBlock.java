package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.utilities.ColoredString;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainBlock extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainBlock.class, PField_Empty.class);

    public PMove_GainBlock()
    {
        this(1);
    }

    public PMove_GainBlock(PSkillSaveData content)
    {
        super(DATA, content);
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
        return TEXT.actions.gainAmount(TEXT.subjects.x, PGR.core.tooltips.block.title);
    }

    @Override
    public String getSubText()
    {
        if (target == PCLCardTarget.None || (target == PCLCardTarget.Self && isFromCreature()))
        {
            return TEXT.actions.gainAmount(getAmountRawString(), PGR.core.tooltips.block);
        }
        return TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), PGR.core.tooltips.block);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (AbstractCreature c : getTargetList(info))
        {
            getActions().gainBlock(c, amount);
        }

        super.use(info);
    }
}
