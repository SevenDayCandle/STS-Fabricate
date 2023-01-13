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
import pinacolada.skills.fields.PField_Affinity;
import pinacolada.utilities.GameUtilities;

public class PMod_PerAffinityLevel extends PMod<PField_Affinity>
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
        super(DATA, PCLCardTarget.None, amount);
        fields.setAffinity(affinities);
    }

    @Override
    public int getModifiedAmount(PSkill be, PCLUseInfo info)
    {
        return be.baseAmount * EUIUtils.sumInt(fields.affinities, GameUtilities::getPCLAffinityLevel) / Math.max(1, this.amount);
    }

    @Override
    public String getSampleText()
    {
        return TEXT.conditions.per("X", PGR.core.tooltips.level.title);
    }

    @Override
    public String getSubText()
    {
        return this.amount <= 1 ? EUIRM.strings.adjNoun(fields.getAffinityLevelAndOrString(), PGR.core.tooltips.level.title) :
                EUIRM.strings.numAdjNoun(getAmountRawString(), fields.getAffinityLevelAndOrString(), PGR.core.tooltips.level.title);
    }
}
