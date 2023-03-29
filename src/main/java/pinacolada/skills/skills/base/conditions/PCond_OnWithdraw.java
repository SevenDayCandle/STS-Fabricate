package pinacolada.skills.skills.base.conditions;

import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.interfaces.subscribers.OnAllyWithdrawSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PDelegateCond;

@VisibleSkill
public class PCond_OnWithdraw extends PDelegateCond implements OnAllyWithdrawSubscriber
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_OnWithdraw.class, PField_CardCategory.class, 1, 1)
            .pclOnly()
            .selfTarget();

    public PCond_OnWithdraw()
    {
        super(DATA);
    }

    public PCond_OnWithdraw(PSkillSaveData content)
    {
        super(DATA, content);
    }

    @Override
    public void onAllyWithdraw(PCLCard returned, PCLCardAlly ally)
    {
        triggerOnCard(returned, ally);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.withdraw;
    }
}
