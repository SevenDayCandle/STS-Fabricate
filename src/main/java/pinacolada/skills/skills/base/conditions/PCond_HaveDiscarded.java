package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

public class PCond_HaveDiscarded extends PCond_Have
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_HaveDiscarded.class, PField_CardCategory.class)
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
        return CombatManager.cardsDiscardedThisTurn();
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.discard;
    }
}
