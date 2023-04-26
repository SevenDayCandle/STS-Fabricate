package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

@VisibleSkill
public class PMod_BonusOnHasDiscarded extends PMod_BonusOnHas {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_BonusOnHasDiscarded.class, PField_CardCategory.class)
            .selfTarget();

    public PMod_BonusOnHasDiscarded() {
        this(1, 1);
    }

    public PMod_BonusOnHasDiscarded(int amount, int extra) {
        super(DATA, amount, extra);
    }

    public PMod_BonusOnHasDiscarded(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_BonusOnHasDiscarded(int amount) {
        super(DATA, amount, 1);
    }

    @Override
    public List<AbstractCard> getCardPile() {
        return fields.forced ? CombatManager.cardsDiscardedThisCombat() : CombatManager.cardsDiscardedThisTurn();
    }

    @Override
    public EUITooltip getActionTooltip() {
        return PGR.core.tooltips.discard;
    }
}
