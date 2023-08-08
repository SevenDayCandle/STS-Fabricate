package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.ExhaustFromPile;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

@VisibleSkill
public class PMove_Exhaust extends PMove_Select<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Exhaust.class, PField_CardCategory.class)
            .selfTarget()
            .setExtra(0, DEFAULT_MAX)
            .setGroups(PCLCardGroupHelper.DrawPile, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.Hand);

    public PMove_Exhaust() {
        this(1);
    }

    public PMove_Exhaust(int amount, PCLCardGroupHelper... h) {
        super(DATA, amount, h);
    }

    public PMove_Exhaust(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_Exhaust(int amount, int extra, PCLCardGroupHelper... h) {
        super(DATA, amount, extra, h);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return ExhaustFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.exhaust;
    }
}
