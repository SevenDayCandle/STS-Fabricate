package pinacolada.skills.skills.base.conditions;

import extendedui.ui.tooltips.EUITooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_OnSummon extends PCond_Delegate
{
    public static final PSkillData DATA = register(PCond_OnSummon.class, PCLEffectType.Delegate, 1, 1)
            .pclOnly()
            .selfTarget();

    public PCond_OnSummon()
    {
        super(DATA);
    }

    public PCond_OnSummon(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public boolean triggerOnAllySummon(PCLCard c, PCLCardAlly ally)
    {
        return triggerOnCard(c, ally);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.summon;
    }
}
