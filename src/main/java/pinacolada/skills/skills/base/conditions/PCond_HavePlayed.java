package pinacolada.skills.skills.base.conditions;

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

import java.util.List;

@VisibleSkill
public class PCond_HavePlayed extends PCond_HaveCard {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_HavePlayed.class, PField_CardCategory.class)
            .setOrigins(PCLCardSelection.Manual)
            .setDestinations(PCLCardSelection.Manual)
            .setGroups(PCLCardGroupHelper.Hand)
            .noTarget();

    public PCond_HavePlayed() {
        this(1);
    }

    public PCond_HavePlayed(int amount) {
        super(DATA, amount, PCLCardGroupHelper.Hand);
    }

    public PCond_HavePlayed(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.play;
    }

    @Override
    public List<AbstractCard> getCardPile(PCLUseInfo info, boolean isUsing) {
        List<AbstractCard> pile = fields.forced ? AbstractDungeon.actionManager.cardsPlayedThisCombat : AbstractDungeon.actionManager.cardsPlayedThisTurn;
        // This check should not count this card if it has just been played
        AbstractCard lastCard = CombatManager.getLastCardPlayed();
        if (isUsing && lastCard == info.card && !pile.isEmpty() && pile.get(pile.size() - 1) == lastCard) {
            return pile.subList(0, pile.size() - 1);
        }
        return pile;
    }
}
