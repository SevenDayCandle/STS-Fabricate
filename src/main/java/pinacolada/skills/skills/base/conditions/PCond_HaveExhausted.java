package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public class PCond_HaveExhausted extends PCond_Have
{
    public static final PSkillData DATA = register(PCond_HaveExhausted.class, PCLEffectType.Card)
            .selfTarget();

    public PCond_HaveExhausted()
    {
        this(1);
    }

    public PCond_HaveExhausted(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_HaveExhausted(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public List<AbstractCard> getCardPile()
    {
        return CombatManager.cardsExhaustedThisTurn();
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.exhaust;
    }
}
