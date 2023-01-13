package pinacolada.skills.skills.base.conditions;

import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PCond_OnAllyTrigger extends PCond_Delegate
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnAllyTrigger.class, PField_CardCategory.class, 1, 1)
            .pclOnly()
            .selfTarget();

    public PCond_OnAllyTrigger()
    {
        super(DATA);
    }

    public PCond_OnAllyTrigger(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public boolean triggerOnAllyTrigger(PCLCard c, PCLCardAlly ally)
    {
        return triggerOnCard(c, ally);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.trigger;
    }
}
