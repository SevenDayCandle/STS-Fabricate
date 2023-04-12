package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMod_BonusPerDamage extends PMod_BonusPer<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PMod_BonusPerDamage.class, PField_Empty.class).selfTarget();

    public PMod_BonusPerDamage(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_BonusPerDamage()
    {
        this(0);
    }

    public PMod_BonusPerDamage(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubText()
    {
        return TEXT.subjects_damage;
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        return sourceCard != null ? sourceCard.damage : 0;
    }
}
