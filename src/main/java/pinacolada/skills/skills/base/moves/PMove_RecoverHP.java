package pinacolada.skills.skills.base.moves;

import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_RecoverHP extends PMove
{
    public static final PSkillData DATA = register(PMove_RecoverHP.class, PField_Empty.class);

    public PMove_RecoverHP()
    {
        this(1);
    }

    public PMove_RecoverHP(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_RecoverHP(int amount)
    {
        super(DATA, PCLCardTarget.Self, amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.actions.heal("X");
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().recoverHP(amount);
        super.use(info);
    }

    @Override
    public String getSubText()
    {
        return TEXT.actions.heal(getAmountRawString());
    }
}
