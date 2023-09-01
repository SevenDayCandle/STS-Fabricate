package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.DiscardFromPile;
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
public class PMod_DiscardPerCard extends PMod_Do {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_DiscardPerCard.class, PField_CardCategory.class)
            .noTarget()
            .setGroups(PCLCardGroupHelper.DrawPile, PCLCardGroupHelper.ExhaustPile, PCLCardGroupHelper.Hand);

    public PMod_DiscardPerCard(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_DiscardPerCard() {
        super(DATA);
    }

    public PMod_DiscardPerCard(int amount, PCLCardGroupHelper... groups) {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return DiscardFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.discard;
    }
}
