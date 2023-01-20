package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.stances.NeutralStance;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Stance;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PCond_InStance extends PCond<PField_Stance>
{

    public static final PSkillData<PField_Stance> DATA = register(PCond_InStance.class, PField_Stance.class, 1, 1)
            .selfTarget();

    public PCond_InStance()
    {
        super(DATA);
    }

    public PCond_InStance(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_InStance(PCLStanceHelper... stance)
    {
        super(DATA);
        fields.setStance(stance);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return fields.random ^ (fields.stances.isEmpty() ? !GameUtilities.inStance(NeutralStance.STANCE_ID) : EUIUtils.any(fields.stances, GameUtilities::inStance));
    }

    @Override
    public String getSampleText()
    {
        return PGR.core.tooltips.stance.title;
    }

    @Override
    public String getSubText()
    {
        String base = fields.getAnyStanceString();
        return fields.random ? TEXT.conditions.not(base) : base;
    }
}
