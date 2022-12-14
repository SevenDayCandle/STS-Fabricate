package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_OnReshuffle extends PCond_Delegate
{
    public static final PSkillData DATA = register(PCond_OnReshuffle.class, PCLEffectType.Delegate, 1, 1)
            .selfTarget();

    public PCond_OnReshuffle()
    {
        super(DATA);
    }

    public PCond_OnReshuffle(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public boolean triggerOnReshuffle(AbstractCard c, CardGroup sourcePile)
    {
        return triggerOnCard(c);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.reshuffle;
    }
}
