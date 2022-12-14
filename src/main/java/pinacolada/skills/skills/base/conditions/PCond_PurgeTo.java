package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.PurgeFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PCond_PurgeTo extends PCond_DoTo
{
    public static final PSkillData DATA = register(PCond_PurgeTo.class, PCLEffectType.CardGroupFull)
            .selfTarget();

    public PCond_PurgeTo()
    {
        this(1, PCLCardGroupHelper.Hand);
    }

    public PCond_PurgeTo(PSkillSaveData content)
    {
        super(content);
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
    public FuncT3<SelectFromPile, String, Integer, CardGroup[]> getAction()
    {
        return PurgeFromPile::new;
    }
}
