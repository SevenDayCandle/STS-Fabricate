package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.CycleCards;
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
public class PMod_CyclePerCard extends PMod_Do {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_CyclePerCard.class, PField_CardCategory.class)
            .noTarget()
            .setGroups(PCLCardGroupHelper.Hand);

    public PMod_CyclePerCard(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_CyclePerCard() {
        super(DATA);
    }

    public PMod_CyclePerCard(int amount) {
        super(DATA, PCLCardTarget.None, amount, PCLCardGroupHelper.Hand);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return (s, c, i, o, g) -> new CycleCards(s, i, o);
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.cycle;
    }
}
