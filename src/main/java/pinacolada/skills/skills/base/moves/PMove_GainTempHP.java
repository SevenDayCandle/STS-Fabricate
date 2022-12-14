package pinacolada.skills.skills.base.moves;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_GainTempHP extends PMove
{
    public static final PSkillData DATA = register(PMove_GainTempHP.class, PCLEffectType.General);

    public PMove_GainTempHP()
    {
        this(1);
    }

    public PMove_GainTempHP(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_GainTempHP(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.gainAmount("X", PGR.core.tooltips.tempHP.title);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().gainTemporaryHP(amount);
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.gainAmount(getAmountRawString(), PGR.core.tooltips.tempHP);
    }
}
