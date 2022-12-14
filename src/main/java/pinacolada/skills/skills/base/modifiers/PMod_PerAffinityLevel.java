package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.utilities.GameUtilities;

import java.util.List;

import static pinacolada.skills.PSkill.PCLEffectType.Affinity;

public class PMod_PerAffinityLevel extends PMod
{

    public static final PSkillData DATA = register(PMod_PerAffinityLevel.class, Affinity)
            .pclOnly()
            .selfTarget();

    public PMod_PerAffinityLevel(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_PerAffinityLevel()
    {
        super(DATA);
    }

    public PMod_PerAffinityLevel(int amount, PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, amount, affinities);
    }

    public PMod_PerAffinityLevel(int amount, List<PCLAffinity> affinities)
    {
        super(DATA, PCLCardTarget.None, amount, affinities.toArray(new PCLAffinity[]{}));
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount * EUIUtils.sumInt(affinities, GameUtilities::getPCLAffinityLevel) / Math.max(1, this.amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", PGR.core.tooltips.level.title);
    }

    @Override
    public String getSubText()
    {
        return this.amount <= 1 ? EUIRM.strings.adjNoun(alt ? getAffinityLevelOrString() : getAffinityLevelAndString(), PGR.core.tooltips.level.title) :
                EUIRM.strings.numAdjNoun(getAmountRawString(), alt ? getAffinityLevelOrString() : getAffinityLevelAndString(), PGR.core.tooltips.level.title);
    }
}
