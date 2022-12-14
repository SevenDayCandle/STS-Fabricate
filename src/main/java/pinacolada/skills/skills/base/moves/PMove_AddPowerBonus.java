package pinacolada.skills.skills.base.moves;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_AddPowerBonus extends PMove
{
    public static final PSkillData DATA = register(PMove_AddPowerBonus.class, PCLEffectType.Power, -999, 999);

    public PMove_AddPowerBonus()
    {
        this(1);
    }

    public PMove_AddPowerBonus(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_AddPowerBonus(int amount, PCLPowerHelper... powers)
    {
        super(DATA, PCLCardTarget.None, amount, powers);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.objectGainsBonus("X", "Y", TEXT.subjects.effectBonus);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        for (PCLPowerHelper power : powers)
        {
            getActions().addPowerEffectEnemyBonus(power.ID, amount);
        }
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.objectGainsBonus(getPowerString(), (amount > 0 ? ("+ " + getAmountRawString()) : getAmountRawString()), TEXT.subjects.effectBonus);
    }
}
