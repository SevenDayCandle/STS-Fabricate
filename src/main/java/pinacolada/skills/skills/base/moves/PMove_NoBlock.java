package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.powers.NoBlockPower;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
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
        return TEXT.actions.youCannotGain(PGR.core.tooltips.block.title);
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
        String base = TEXT.actions.youCannotGain(PGR.core.tooltips.block);
        return amount > 1 ? TEXT.conditions.forTurns(amount) + ", " + base : TEXT.subjects.thisTurn(base);
    }
}
