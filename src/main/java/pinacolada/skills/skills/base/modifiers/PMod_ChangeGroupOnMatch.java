package pinacolada.skills.skills.base.modifiers;

import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import static pinacolada.skills.PSkill.PCLEffectType.General;

public class PMod_ChangeGroupOnMatch extends PMod_ChangeGroup
{

    public static final PSkillData DATA = register(PMod_ChangeGroupOnMatch.class, General).selfTarget();

    public PMod_ChangeGroupOnMatch(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_ChangeGroupOnMatch()
    {
        this((PCLCardGroupHelper) null);
    }

    public PMod_ChangeGroupOnMatch(PCLCardGroupHelper... groups)
    {
        super(DATA, groups);
    }

    @Override
    public String getConditionSampleText()
    {
        return PGR.core.tooltips.match.title;
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info)
    {
        return info.isMatch;
    }
}
