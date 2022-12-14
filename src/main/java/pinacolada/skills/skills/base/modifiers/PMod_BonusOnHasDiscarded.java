package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.misc.CombatStats;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

import java.util.List;

public class PMod_BonusOnHasDiscarded extends PMod_BonusOnHas
{
    public static final PSkillData DATA = register(PMod_BonusOnHasDiscarded.class, PCLEffectType.Card)
            .selfTarget();

    public PMod_BonusOnHasDiscarded()
    {
        this(1, 1);
    }

    public PMod_BonusOnHasDiscarded(PSkillSaveData content)
    {
        super(content);
    }

    public PMod_BonusOnHasDiscarded(int amount)
    {
        super(DATA, amount, 1);
    }

    public PMod_BonusOnHasDiscarded(int amount, int extra)
    {
        super(DATA, amount, extra);
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
