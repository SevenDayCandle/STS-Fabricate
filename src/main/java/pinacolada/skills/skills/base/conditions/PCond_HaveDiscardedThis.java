package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;

import java.util.List;

@VisibleSkill
public class PCond_HaveDiscardedThis extends PCond_HaveCardThis
{
    public static final PSkillData<PField_CardGeneric> DATA = register(PCond_HaveDiscardedThis.class, PField_CardGeneric.class)
            .selfTarget();

    public PCond_HaveDiscardedThis()
    {
        this(1);
    }

    public PCond_HaveDiscardedThis(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_HaveDiscardedThis(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public List<AbstractCard> getCardPile()
    {
        return fields.forced ? CombatManager.cardsDiscardedThisCombat() : CombatManager.cardsDiscardedThisTurn();
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.discard;
    }
}
