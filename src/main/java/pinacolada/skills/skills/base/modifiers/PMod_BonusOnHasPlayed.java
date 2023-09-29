package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
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

import java.util.ArrayList;
import java.util.List;

@VisibleSkill
public class PMod_BonusOnHasPlayed extends PMod_BonusOnHas {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_BonusOnHasPlayed.class, PField_CardCategory.class)
            .setOrigins(PCLCardSelection.Manual)
            .setDestinations(PCLCardSelection.Manual)
            .setGroups(PCLCardGroupHelper.Hand)
            .noTarget();

    public PMod_BonusOnHasPlayed() {
        this(1, 1);
    }

    public PMod_BonusOnHasPlayed(int amount, int extra) {
        super(DATA, amount, extra);
    }

    public PMod_BonusOnHasPlayed(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_BonusOnHasPlayed(int amount) {
        super(DATA, amount, 1);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.play;
    }

    @Override
    public List<AbstractCard> getCardPile(PCLUseInfo info, boolean isUsing) {
        List<AbstractCard> pile = fields.forced ? AbstractDungeon.actionManager.cardsPlayedThisCombat : AbstractDungeon.actionManager.cardsPlayedThisTurn;
        // This check should not count this card if it has just been played
        if (isUsing && CombatManager.lastCardPlayed == info.card && pile.size() > 0 && pile.get(pile.size() - 1) == CombatManager.lastCardPlayed) {
            return pile.subList(0, pile.size() - 1);
        }
        return pile;
    }
}
