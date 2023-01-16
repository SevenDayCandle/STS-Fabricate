package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.PurgeFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;


public class PMod_PurgeBranch extends PMod_DoBranch
{

    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PurgeBranch.class, PField_CardCategory.class)
            .selfTarget();

    public PMod_PurgeBranch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PurgeBranch()
    {
        super(DATA);
    }

    public PMod_PurgeBranch(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.purge;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return PurgeFromPile::new;
    }
}
