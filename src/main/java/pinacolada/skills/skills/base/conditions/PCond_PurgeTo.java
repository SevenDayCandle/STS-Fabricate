package pinacolada.skills.skills.base.conditions;

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

public class PCond_PurgeTo extends PCond_DoTo
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_PurgeTo.class, PField_CardCategory.class)
            .selfTarget();

    public PCond_PurgeTo()
    {
        this(1, PCLCardGroupHelper.Hand);
    }

    public PCond_PurgeTo(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_PurgeTo(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, PCLCardTarget.None, amount, h);
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
