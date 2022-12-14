package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_OnCreate extends PCond_Delegate
{
    public static final PSkillData DATA = register(PCond_OnCreate.class, PCLEffectType.Delegate, 1, 1)
            .selfTarget();

    public PCond_OnCreate()
    {
        super(DATA);
    }

    public PCond_OnCreate(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public boolean triggerOnCreate(AbstractCard c, boolean startOfBattle)
    {
        return triggerOnCard(c);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.create;
    }
}
