package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.ReshuffleFromPile;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;


@VisibleSkill
public class PMod_ReshufflePerCard extends PMod_Do {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_ReshufflePerCard.class, PField_CardCategory.class)
            .noTarget()
            .setGroups(PCLCardGroupHelper.DrawPile, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.Hand);

    public PMod_ReshufflePerCard(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_ReshufflePerCard() {
        super(DATA);
    }

    public PMod_ReshufflePerCard(int amount, PCLCardGroupHelper... groups) {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return ReshuffleFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.reshuffle;
    }

    @Override
    public String getMoveString(boolean addPeriod) {
        if (fields.destination == PCLCardSelection.Manual) {
            return super.getMoveString(addPeriod);
        }
        String cardString = isForced() ? fields.getFullCardString() : fields.getShortCardString();
        String dest = fields.getDestinationString(PCLCardGroupHelper.DrawPile.name);
        return fields.hasGroups() && !fields.isHandOnly() ? TEXT.act_zXFromYToZ(getActionTitle(), getAmountRawOrAllString(), cardString, fields.getGroupString(), dest)
                : TEXT.act_zXToY(getActionTitle(), getAmountRawOrAllString(), cardString, dest);
    }
}
