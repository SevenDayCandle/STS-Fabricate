package pinacolada.skills.skills.base.moves;

import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainMaxHP extends PMove_Gain
{
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainMaxHP.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX);

    public PMove_GainMaxHP()
    {
        this(1);
    }

    public PMove_GainMaxHP(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_GainMaxHP(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String gainText()
    {
        return PGR.core.tooltips.maxHP.title;
    }

    @Override
    public boolean isDetrimental()
    {
        return amount < 0;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        if (amount < 0)
        {
            info.source.decreaseMaxHealth(amount);
        }
        else
        {
            info.source.increaseMaxHp(amount, true);
        }
        super.use(info);
    }

    @Override
    public boolean isMetascaling()
    {
        return true;
    }
}
