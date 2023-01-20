package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

import java.util.List;

@VisibleSkill
public class PMod_SelectPerCard extends PMod_Do
{

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_SelectPerCard.class, PField_CardCategory.class)
            .selfTarget();

    public PMod_SelectPerCard(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_SelectPerCard()
    {
        super(DATA);
    }

    public PMod_SelectPerCard(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    public PMod_SelectPerCard(int amount, List<PCLCardGroupHelper> groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups.toArray(new PCLCardGroupHelper[]{}));
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.select;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return SelectFromPile::new;
    }

    @Override
    public String getText(boolean addPeriod)
    {
        return childEffect != null && childEffect.useParent ? childEffect.getText(addPeriod) : super.getText(addPeriod);
    }

    @Override
    public String getParentString()
    {
        return EUIRM.strings.numNounPlace(amount <= 0 ? TEXT.subjects.all : getAmountRawString(), fields.getFullCardString(), TEXT.subjects.in(fields.getGroupString()));
    }
}
