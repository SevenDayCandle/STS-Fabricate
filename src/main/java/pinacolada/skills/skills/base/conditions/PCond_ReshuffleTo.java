package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
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
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PCond_ReshuffleTo extends PCond_DoToCard {
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_ReshuffleTo.class, PField_CardCategory.class)
            .selfTarget()
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.ExhaustPile, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.Hand);

    public PCond_ReshuffleTo() {
        this(1, PCLCardGroupHelper.Hand);
    }

    public PCond_ReshuffleTo(int amount, PCLCardGroupHelper... h) {
        super(DATA, PCLCardTarget.None, amount, h);
    }

    public PCond_ReshuffleTo(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSubText() {
        if (fields.destination == PCLCardSelection.Manual) {
            return super.getSubText();
        }
        String dest = fields.getDestinationString(PCLCardGroupHelper.DrawPile.name);
        return fields.hasGroups() && !fields.isHandOnly() ? TEXT.act_zXFromYToZ(getActionTitle(), getAmountRawOrAllString(), fields.getFullCardString(), fields.getGroupString(), dest)
                : TEXT.act_zXToY(getActionTitle(), getAmountRawOrAllString(), fields.getFullCardString(), dest);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction() {
        return ReshuffleFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.reshuffle;
    }
}
