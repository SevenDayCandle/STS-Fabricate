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
public class PMod_BonusOnHasReshuffled extends PMod_BonusOnHas {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_BonusOnHasReshuffled.class, PField_CardCategory.class)
            .setOrigins(PCLCardSelection.Manual)
            .setDestinations(PCLCardSelection.Manual)
            .setGroups(PCLCardGroupHelper.DrawPile)
            .noTarget();

    public PMod_BonusOnHasReshuffled() {
        this(1, 1);
    }

    public PMod_BonusOnHasReshuffled(int amount, int extra) {
        super(DATA, amount, extra);
    }

    public PMod_BonusOnHasReshuffled(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_BonusOnHasReshuffled(int amount) {
        super(DATA, amount, 1);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.reshuffle;
    }

    @Override
    public List<AbstractCard> getCardPile(PCLUseInfo info, boolean isUsing) {
        return fields.forced ? CombatManager.cardsReshuffledThisCombat() : CombatManager.cardsReshuffledThisTurn();
    }
}
