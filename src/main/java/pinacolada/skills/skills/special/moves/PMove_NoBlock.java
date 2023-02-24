package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.powers.NoBlockPower;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

public class PMove_NoBlock extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_NoBlock.class, PField_Empty.class);

    public PMove_NoBlock()
    {
        this(1);
    }

    public PMove_NoBlock(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_NoBlock(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.act_youCannotGain(PGR.core.tooltips.block.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().applyPower(info.source, new NoBlockPower(info.source, amount, false));
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        String base = TEXT.act_youCannotGain(PGR.core.tooltips.block);
        return amount > 1 ? TEXT.cond_forTurns(amount) + ", " + base : TEXT.subjects_thisTurn(base);
    }
}
