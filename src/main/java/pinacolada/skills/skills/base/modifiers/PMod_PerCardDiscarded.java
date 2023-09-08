package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.dungeon.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;


@VisibleSkill
public class PMod_PerCardDiscarded extends PMod_PerCardHas {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCardDiscarded.class, PField_CardCategory.class)
            .setOrigins(PCLCardSelection.Manual)
            .setDestinations(PCLCardSelection.Manual)
            .noTarget();

    public PMod_PerCardDiscarded() {
        this(1, 0);
    }

    public PMod_PerCardDiscarded(int amount, int extra) {
        super(DATA, amount, extra);
    }

    public PMod_PerCardDiscarded(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCardDiscarded(int amount) {
        super(DATA, amount, 0);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.discard;
    }

    @Override
    public List<AbstractCard> getCardPile() {
        return fields.forced ? CombatManager.cardsDiscardedThisCombat() : CombatManager.cardsDiscardedThisTurn();
    }
}
