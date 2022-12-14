package pinacolada.skills.skills.base.modifiers;

import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import static pinacolada.skills.PSkill.PCLEffectType.General;

public class PMod_ChangeGroupOnStarter extends PMod_ChangeGroup
{

    public static final PSkillData DATA = register(PMod_ChangeGroupOnStarter.class, General).selfTarget();

    public PMod_ChangeGroupOnStarter(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_ChangeGroupOnStarter()
    {
        this((PCLCardGroupHelper) null);
    }

    public PMod_ChangeGroupOnStarter(PCLCardGroupHelper... groups)
    {
        super(DATA, groups);
    }

    @Override
    public String getConditionSampleText()
    {
        return PGR.core.tooltips.starter.title;
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info)
    {
        return info.isStarter;
    }
}
