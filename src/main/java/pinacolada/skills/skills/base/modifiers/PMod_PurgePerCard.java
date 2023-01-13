package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.PurgeFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;



public class PMod_PurgePerCard extends PMod_Do
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_PurgePerCard.class, PField_CardCategory.class)
            .selfTarget();

    public PMod_PurgePerCard(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_PurgePerCard()
    {
        super(DATA);
    }

    public PMod_PurgePerCard(int amount, PCLCardGroupHelper... groups)
    {
        super(DATA, PCLCardTarget.None, amount, groups);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.purge;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return PurgeFromPile::new;
    }
}
