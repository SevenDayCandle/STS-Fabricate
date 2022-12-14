package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.stances.NeutralStance;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

public class PCond_InStance extends PCond
{

    public static final PSkillData DATA = register(PCond_InStance.class, PCLEffectType.Stance, 1, 1)
            .selfTarget();

    public PCond_InStance()
    {
        this((PCLStanceHelper) null);
    }

    public PCond_InStance(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_InStance(PCLStanceHelper... stance)
    {
        super(DATA, stance);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, boolean fromTrigger)
    {
        return alt ^ (stances.isEmpty() ? !GameUtilities.inStance(NeutralStance.STANCE_ID) : EUIUtils.any(stances, GameUtilities::inStance));
    }

    @Override
    public String getSampleText()
    {
        return PGR.core.tooltips.stance.title;
    }

    @Override
    public String getSubText()
    {
        String base = stances.isEmpty() ? TEXT.conditions.any(PGR.core.tooltips.stance.title) : getStanceString();
        return alt ? TEXT.conditions.not(base) : base;
    }
}
