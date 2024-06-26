package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.ScoutCards;
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
public class PMove_Scout extends PMove_DoCard<PField_CardCategory> {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Scout.class, PField_CardCategory.class)
            .noTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PMove_Scout() {
        this(1);
    }

    public PMove_Scout(int amount) {
        super(DATA, amount, PCLCardGroupHelper.DrawPile);
    }

    public PMove_Scout(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return (s, c, i, o, g) -> new ScoutCards(s, i);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.scout;
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return useParent ? EUIRM.strings.verbNoun(getActionTitle(), getInheritedThemString())
                : EUIRM.strings.verbNumNoun(getActionTitle(), getAmountRawString(requestor), fields.getFullCardString(requestor));
    }
}
