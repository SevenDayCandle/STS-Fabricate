package pinacolada.skills.skills.special.modifiers;

import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.base.modifiers.PMod_BonusOn;

public class PMod_BonusOnMatch extends PMod_BonusOn<PField_Empty>
{

    public static final PSkillData<PField_Empty> DATA = register(PMod_BonusOnMatch.class, PField_Empty.class).selfTarget();

    public PMod_BonusOnMatch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_BonusOnMatch()
    {
        this(0);
    }

    public PMod_BonusOnMatch(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public String getSubText()
    {
        return PGR.core.tooltips.match.title;
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info)
    {
        return info.isMatch;
    }
}
