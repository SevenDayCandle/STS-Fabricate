package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

@VisibleSkill
public class PCond_HaveLastDiscarded extends PCond_HaveLastCard {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_HaveLastDiscarded.class, PField_CardCategory.class)
            .setOrigins(PCLCardSelection.Manual)
            .setDestinations(PCLCardSelection.Manual)
            .setGroups(PCLCardGroupHelper.DiscardPile)
            .noTarget();

    public PCond_HaveLastDiscarded() {
        this(1);
    }

    public PCond_HaveLastDiscarded(int amount) {
        super(DATA, amount, PCLCardGroupHelper.DiscardPile);
    }

    public PCond_HaveLastDiscarded(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.discard;
    }

    @Override
    public List<AbstractCard> getCardPile(PCLUseInfo info, boolean isUsing) {
        return fields.forced ? CombatManager.cardsDiscardedThisCombat() : CombatManager.cardsDiscardedThisTurn();
    }
}
