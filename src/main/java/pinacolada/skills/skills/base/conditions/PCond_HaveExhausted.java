package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

@VisibleSkill
public class PCond_HaveExhausted extends PCond_HaveCard
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_HaveExhausted.class, PField_CardCategory.class)
            .selfTarget();

    public PCond_HaveExhausted()
    {
        this(1);
    }

    public PCond_HaveExhausted(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_HaveExhausted(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public List<AbstractCard> getCardPile()
    {
        return fields.forced ? CombatManager.cardsExhaustedThisCombat() : CombatManager.cardsExhaustedThisTurn();
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.exhaust;
    }
}
