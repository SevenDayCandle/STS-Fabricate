package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.misc.CombatStats;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public class PCond_HaveDiscarded extends PCond_Have
{
    public static final PSkillData DATA = register(PCond_HaveDiscarded.class, PCLEffectType.Card)
            .selfTarget();

    public PCond_HaveDiscarded()
    {
        this(1);
    }

    public PCond_HaveDiscarded(PSkillSaveData content)
    {
        super(content);
    }

    public PCond_HaveDiscarded(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public List<AbstractCard> getCardPile()
    {
        return CombatStats.cardsDiscardedThisTurn();
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.discard;
    }
}
