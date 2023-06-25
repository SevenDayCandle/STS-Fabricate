package pinacolada.skills.skills.base.moves;

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
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PMove_Reshuffle extends PMove_Select<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Reshuffle.class, PField_CardCategory.class)
            .selfTarget()
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.Hand, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.ExhaustPile);

    public PMove_Reshuffle() {
        this(1);
    }

    public PMove_Reshuffle(int amount, PCLCardGroupHelper... h) {
        super(DATA, amount, h);
    }

    public PMove_Reshuffle(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_Reshuffle(int amount, int extra, PCLCardGroupHelper... h) {
        super(DATA, amount, extra, h);
    }

    @Override
    public String getSubText() {
        if (fields.destination == PCLCardSelection.Manual) {
            return super.getSubText();
        }
        String dest = fields.getDestinationString(PCLCardGroupHelper.DrawPile.name);
        return useParent ? TEXT.act_zToX(getActionTitle(), getInheritedThemString(), dest) :
                fields.isHandOnly() ? TEXT.act_zXToY(getActionTitle(), getAmountRawOrAllString(), fields.getFullCardString(), dest) :
                        fields.hasGroups() ? TEXT.act_zXFromYToZ(getActionTitle(), getAmountRawOrAllString(), fields.getFullCardString(), fields.getGroupString(), dest)
                                : TEXT.act_zToX(getActionTitle(), TEXT.subjects_thisCard, dest);
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
