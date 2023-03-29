package pinacolada.skills.skills.base.conditions;

import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.interfaces.subscribers.OnAllyTriggerSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCond;

@VisibleSkill
public class PCond_OnAllyTrigger extends PDelegateCond implements OnAllyTriggerSubscriber
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
    public void onAllyTrigger(PCLCard card, PCLCardAlly ally)
    {
        triggerOnCard(card, ally);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.trigger;
    }
}
