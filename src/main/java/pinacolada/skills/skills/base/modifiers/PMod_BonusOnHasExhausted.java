package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public class PMod_BonusOnHasExhausted extends PMod_BonusOnHas
{
    public static final PSkillData DATA = register(PMod_BonusOnHasExhausted.class, PCLEffectType.Card)
            .selfTarget();

    public PMod_BonusOnHasExhausted()
    {
        this(1, 1);
    }

    public PMod_BonusOnHasExhausted(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusOnHasExhausted(int amount)
    {
        super(DATA, amount, 1);
    }

    public PMod_BonusOnHasExhausted(int amount, int extra)
    {
        super(DATA, amount, extra);
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
