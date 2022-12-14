package pinacolada.skills.skills.base.modifiers;

import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import static pinacolada.skills.PSkill.PCLEffectType.General;

public class PMod_BonusOnStarter extends PMod_BonusOn
{

    public static final PSkillData DATA = register(PMod_BonusOnStarter.class, General).selfTarget();

    public PMod_BonusOnStarter(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusOnStarter()
    {
        this(0);
    }

    public PMod_BonusOnStarter(int amount)
    {
        super(DATA, amount);
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
