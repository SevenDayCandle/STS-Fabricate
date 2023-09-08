package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.DiscardFromPile;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_Discard extends PMove_Select<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Discard.class, PField_CardCategory.class)
            .noTarget()
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.DrawPile, PCLCardGroupHelper.Hand);

    public PMove_Discard() {
        this(1);
    }

    public PMove_Discard(int amount, PCLCardGroupHelper... h) {
        super(DATA, amount, h);
    }

    public PMove_Discard(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_Discard(int amount, int extra, PCLCardGroupHelper... h) {
        super(DATA, amount, extra, h);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return DiscardFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.discard;
    }

    @Override
    public PCLCardGroupHelper getDestinationGroup() {
        return PCLCardGroupHelper.DiscardPile;
    }
}
