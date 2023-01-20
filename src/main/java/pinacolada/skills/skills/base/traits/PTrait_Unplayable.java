package pinacolada.skills.skills.base.traits;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PTrait_Unplayable extends PMove<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PTrait_Unplayable.class, PField_Empty.class, 1, 1);

    public PTrait_Unplayable()
    {
        this(1);
    }

    public PTrait_Unplayable(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PTrait_Unplayable(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    @Override
    public boolean canPlay(AbstractCard card, AbstractMonster m)
    {
        return card == sourceCard;
    }

    @Override
    public String getSampleText()
    {
        return PGR.core.tooltips.unplayable.title;
    }

    @Override
    public boolean isDetrimental()
    {
        return true;
    }

    @Override
    public void use(PCLUseInfo info)
    {

    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.unplayable.title;
    }
}
