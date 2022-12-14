package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.Affinity;

public class PMod_BonusPerAffinityLevel extends PMod_BonusPer
{

    public static final PSkillData DATA = register(PMod_BonusPerAffinityLevel.class, Affinity).selfTarget();

    public PMod_BonusPerAffinityLevel(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusPerAffinityLevel()
    {
        this(0, (PCLAffinity) null);
    }

    public PMod_BonusPerAffinityLevel(int amount, PCLAffinity... affinities)
    {
        super(DATA, amount, affinities);
    }

    public PMod_BonusPerAffinityLevel(int amount, List<PCLAffinity> affinities)
    {
        super(DATA, amount, affinities.toArray(new PCLAffinity[]{}));
    }

    @Override
    public String getConditionSampleText()
    {
        return PGR.core.tooltips.level.title;
    }

    @Override
    public String getConditionText()
    {
        return EUIRM.strings.adjNoun(getAffinityLevelAndString(), PGR.core.tooltips.level.title);
    }

    @Override
    public int multiplier(PCLUseInfo info)
    {
        return EUIUtils.sumInt(affinities, GameUtilities::getPCLAffinityLevel);
    }
}
