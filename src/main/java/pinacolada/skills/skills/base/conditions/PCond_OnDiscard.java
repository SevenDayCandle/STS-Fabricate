package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_OnDiscard extends PCond_Delegate
{
    public static final PSkillData DATA = register(PCond_OnDiscard.class, PCLEffectType.Delegate, 1, 1)
            .selfTarget();

    public PCond_OnDiscard()
    {
        super(DATA);
    }

    public PCond_OnDiscard(PSkillSaveData content)
    {
        super(content);
    }

    @Override
    public boolean triggerOnDiscard(AbstractCard c)
    {
        return triggerOnCard(c);
    }

    @Override
    public EUITooltip getDelegateTooltip()
    {
        return PGR.core.tooltips.discard;
    }
}
