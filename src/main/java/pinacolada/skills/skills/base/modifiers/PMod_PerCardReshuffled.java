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
public class PMod_PerCardReshuffled extends PMod_PerCardHas {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PerCardReshuffled.class, PField_CardCategory.class)
            .setOrigins(PCLCardSelection.Manual)
            .setDestinations(PCLCardSelection.Manual)
            .setGroups(PCLCardGroupHelper.DrawPile)
            .noTarget();

    public PMod_PerCardReshuffled() {
        this(1, 0);
    }

    public PMod_PerCardReshuffled(int amount) {
        this(amount, 0);
    }

    public PMod_PerCardReshuffled(int amount, int extra) {
        super(DATA, amount, extra, PCLCardGroupHelper.DrawPile);
    }

    public PMod_PerCardReshuffled(PSkillSaveData content) {
        super(DATA, content);
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
