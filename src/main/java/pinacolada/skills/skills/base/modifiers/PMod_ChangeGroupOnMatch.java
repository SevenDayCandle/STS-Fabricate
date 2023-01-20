package pinacolada.skills.skills.base.modifiers;

import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;

@VisibleSkill
public class PMod_ChangeGroupOnMatch extends PMod_ChangeGroup
{

    public static final PSkillData<PField_CardGeneric> DATA = register(PMod_ChangeGroupOnMatch.class, PField_CardGeneric.class).selfTarget();

    public PMod_ChangeGroupOnMatch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_ChangeGroupOnMatch()
    {
        super(DATA);
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
