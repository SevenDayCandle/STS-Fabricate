package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLAction;
import pinacolada.actions.piles.DrawCards;
import pinacolada.actions.piles.FetchFromPile;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.ArrayList;


@VisibleSkill
public class PMod_DrawPerCard extends PMod_Do {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_DrawPerCard.class, PField_CardCategory.class)
            .noTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PMod_DrawPerCard(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_DrawPerCard() {
        super(DATA);
    }

    public PMod_DrawPerCard(int amount) {
        super(DATA, PCLCardTarget.None, amount, PCLCardGroupHelper.DrawPile);
    }

    @Override
    protected PCLAction<ArrayList<AbstractCard>> createPileAction(PCLUseInfo info) {
        DrawCards action = new DrawCards(amount);
        if (isForced()) {
            action = action.setFilter(c -> fields.getFullCardFilter().invoke(c), false);
        }
        return action;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return FetchFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.draw;
    }

    @Override
    public String getAmountRawOrAllString() {
        return getAmountRawString();
    }

    @Override
    public String getMoveString(boolean addPeriod) {
        return TEXT.act_drawType(getAmountRawString(), fields.getFullCardString());
    }
}
