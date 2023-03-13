package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMod_PerCreature extends PMod_Per<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PMod_PerCreature.class, PField_Empty.class);

    public PMod_PerCreature(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerCreature()
    {
        super(DATA);
    }

    public PMod_PerCreature(int amount)
    {
        super(DATA, PCLCardTarget.AllEnemy, amount);
    }

    public PMod_PerCreature(PCLCardTarget target, int amount)
    {
        super(DATA, target, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        return getTargetList(info).size();
    }

    @Override
    public String getSubText()
    {
        return TEXT.subjects_character;
    }

    @Override
    public String getConditionText()
    {
        switch (target)
        {
            case AllAlly:
            case RandomAlly:
            case SingleAlly:
            case Team:
                return TEXT.subjects_ally;
            case AllEnemy:
            case RandomEnemy:
            case Single:
                return TEXT.subjects_enemy;
            default:
                return TEXT.subjects_character;
        }
    }
}
