package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.misc.CombatStats;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import static pinacolada.skills.PSkill.PCLEffectType.Affinity;

public class PMod_HighestAffinityBranch extends PMod_Branch<PCLAffinity>
{

    public static final PSkillData DATA = register(PMod_ScryBranch.class, Affinity)
            .setColors(PGR.Enums.Cards.THE_DECIDER)
            .setExtra(-1, DEFAULT_MAX)
            .selfTarget();

    public PMod_HighestAffinityBranch(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_HighestAffinityBranch(PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, 1, affinities);
    }

    public String getQualifier(int i)
    {
        PCLAffinity affinity = i < affinities.size() ? affinities.get(i) : null;
        return affinity != null ? affinity.getLevelTooltip().getTitleOrIcon() : TEXT.subjects.other;
    }

    @Override
    public boolean matchesBranch(PCLAffinity c, int i, PCLUseInfo info)
    {
        return i < affinities.size() ? affinities.get(i) == c : EUIUtils.all(affinities, af -> c != af);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.ifYourHighest(PGR.core.tooltips.affinityGeneral.title);
    }

    @Override
    public String getSubText()
    {
        String base = TEXT.conditions.ifYourHighest(EUIRM.strings.adjNoun(PGR.core.tooltips.level.title, PGR.core.tooltips.affinityGeneral.title));
        return extra > 0 ? base + " (" + TEXT.subjects.min(extra) + ")" : base;
    }

    @Override
    public void use(PCLUseInfo info)
    {
        int max = EUIUtils.max(PCLAffinity.getAvailableAffinities(), CombatStats.playerSystem::getLevel);
        if (max > extra)
        {
            branch(info, EUIUtils.filter(PCLAffinity.getAvailableAffinities(), af -> CombatStats.playerSystem.getLevel(af) >= max));
        }
    }
}
