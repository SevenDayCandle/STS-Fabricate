package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Affinity;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerAffinityLevel extends PMod_Per<PField_Affinity>
{
    public static final PSkillData<PField_Affinity> DATA = register(PMod_PerAffinityLevel.class, PField_Affinity.class)
            .pclOnly()
            .selfTarget();

    public PMod_PerAffinityLevel(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PerAffinityLevel()
    {
        super(DATA);
    }

    public PMod_PerAffinityLevel(int amount, PCLAffinity... affinities)
    {
        super(DATA, amount);
        fields.setAffinity(affinities);
    }

    @Override
    public int getMultiplier(PCLUseInfo info)
    {
        return EUIUtils.sumInt(fields.affinities, GameUtilities::getPCLAffinityLevel);
    }

    @Override
    public String getSubText()
    {
        return EUIRM.strings.adjNoun(fields.getAffinityLevelAndOrString(), PGR.core.tooltips.level.title);
    }

    @Override
    public String getSubSampleText()
    {
        return PGR.core.tooltips.level.title;
    }
}
