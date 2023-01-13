package pinacolada.skills.skills.base.modifiers;

import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Affinity;
import pinacolada.utilities.GameUtilities;

public class PMod_BonusPerAffinityLevel extends PMod_BonusPer<PField_Affinity>
{

    public static final PSkillData<PField_Affinity> DATA = register(PMod_BonusPerAffinityLevel.class, PField_Affinity.class).selfTarget();

    public PMod_BonusPerAffinityLevel(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusPerAffinityLevel()
    {
        this(0);
    }

    public PMod_BonusPerAffinityLevel(int amount, PCLAffinity... affinities)
    {
        super(DATA, amount);
        fields.setAffinity(affinities);
    }


    @Override
    public String getConditionSampleText()
    {
        return PGR.core.tooltips.level.title;
    }

    @Override
    public String getConditionText()
    {
        return EUIRM.strings.adjNoun(fields.getAffinityLevelAndString(), PGR.core.tooltips.level.title);
    }

    @Override
    public int multiplier(PCLUseInfo info)
    {
        return EUIUtils.sumInt(fields.affinities, GameUtilities::getPCLAffinityLevel);
    }
}
