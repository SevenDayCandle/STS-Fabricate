package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainTempHP extends PMove<PField_Empty>
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainTempHP.class, PField_Empty.class);

    public PMove_GainTempHP()
    {
        this(1);
    }

    public PMove_GainTempHP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_GainTempHP(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMove_GainTempHP(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.gainAmount(TEXT.subjects.x, PGR.core.tooltips.tempHP.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (AbstractCreature c : getTargetList(info))
        {
            getActions().gainTemporaryHP(c, c, amount);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        if (isSelfOnlyTarget())
        {
            return TEXT.actions.gainAmount(getAmountRawString(), PGR.core.tooltips.tempHP);
        }
        return TEXT.actions.giveTargetAmount(getTargetString(), getAmountRawString(), PGR.core.tooltips.tempHP);
    }
}
