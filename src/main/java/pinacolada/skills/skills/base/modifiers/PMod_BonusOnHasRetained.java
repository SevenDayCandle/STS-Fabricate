package pinacolada.skills.skills.base.modifiers;

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
public class PMod_BonusOnHasRetained extends PMod_BonusOnHas {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_BonusOnHasRetained.class, PField_CardCategory.class)
            .setOrigins(PCLCardSelection.Manual)
            .setDestinations(PCLCardSelection.Manual)
            .setGroups(PCLCardGroupHelper.Hand)
            .noTarget();

    public PMod_BonusOnHasRetained() {
        this(1, 1);
    }

    public PMod_BonusOnHasRetained(int amount, int extra) {
        super(DATA, amount, extra);
    }

    public PMod_BonusOnHasRetained(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_BonusOnHasRetained(int amount) {
        super(DATA, amount, 1);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.retain;
    }

    @Override
    public List<AbstractCard> getCardPile(PCLUseInfo info, boolean isUsing) {
        return fields.forced ? CombatManager.cardsRetainedThisCombat() : CombatManager.cardsRetainedThisTurn();
    }
}
