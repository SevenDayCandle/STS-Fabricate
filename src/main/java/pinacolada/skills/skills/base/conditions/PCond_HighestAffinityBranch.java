package pinacolada.skills.skills.base.conditions;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Affinity;

@VisibleSkill
public class PCond_HighestAffinityBranch extends PCond_Branch<PField_Affinity, PCLAffinity>
{
    public static final PSkillData<PField_Affinity> DATA = register(PCond_HighestAffinityBranch.class, PField_Affinity.class)
            .pclOnly()
            .setExtra(-1, DEFAULT_MAX)
            .selfTarget();

    public PCond_HighestAffinityBranch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_HighestAffinityBranch(PCLAffinity... affinities)
    {
        super(DATA, PCLCardTarget.None, 1);
        fields.setAffinity(affinities);
    }

    public String getQualifier(int i)
    {
        PCLAffinity affinity = i < fields.affinities.size() ? fields.affinities.get(i) : null;
        return affinity != null ? affinity.getLevelTooltip().getTitleOrIcon() : TEXT.subjects.other;
    }

    @Override
    public boolean matchesBranch(PCLAffinity c, int i, PCLUseInfo info)
    {
        return i < fields.affinities.size() ? fields.affinities.get(i) == c : EUIUtils.all(fields.affinities, af -> c != af);
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
        int max = EUIUtils.max(PCLAffinity.getAvailableAffinities(), CombatManager.playerSystem::getLevel);
        if (max > extra)
        {
            branch(info, EUIUtils.filter(PCLAffinity.getAvailableAffinities(), af -> CombatManager.playerSystem.getLevel(af) >= max));
        }
    }
}
