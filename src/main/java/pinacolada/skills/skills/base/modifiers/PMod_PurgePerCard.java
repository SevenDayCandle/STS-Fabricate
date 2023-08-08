package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.PurgeFromPile;
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
public class PMod_PurgePerCard extends PMod_Do {
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PurgePerCard.class, PField_CardCategory.class)
            .selfTarget();

    public PMod_PurgePerCard(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PurgePerCard() {
        super(DATA);
    }

    public PMod_PurgePerCard(int amount, PCLCardGroupHelper... groups) {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return PurgeFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.purge;
    }
}
