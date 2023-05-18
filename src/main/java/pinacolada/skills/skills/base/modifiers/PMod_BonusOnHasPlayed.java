package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

@VisibleSkill
public class PMod_BonusOnHasPlayed extends PMod_BonusOnHas {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_BonusOnHasPlayed.class, PField_CardCategory.class)
            .selfTarget();

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
    public List<AbstractCard> getCardPile() {
        return fields.forced ? AbstractDungeon.actionManager.cardsPlayedThisCombat : AbstractDungeon.actionManager.cardsPlayedThisTurn;
    }
}
