package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

@VisibleSkill
public class PMod_SelectPerCard extends PMod_Do {

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_SelectPerCard.class, PField_CardCategory.class)
            .selfTarget();

    public PMod_SelectPerCard(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_SelectPerCard() {
        super(DATA);
    }

    public PMod_SelectPerCard(int amount, PCLCardGroupHelper... groups) {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    public PMod_SelectPerCard(int amount, List<PCLCardGroupHelper> groups) {
        super(DATA, PCLCardTarget.None, amount, groups.toArray(new PCLCardGroupHelper[]{}));
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, PCLCardSelection, CardGroup[]> getAction() {
        return SelectFromPile::new;
    }

    @Override
    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.select;
    }

    @Override
    public String getThemString() {
        return EUIRM.strings.numNounPlace(getAmountRawOrAllString(), fields.getFullCardString(), TEXT.subjects_in(fields.getGroupString()));
    }
}
