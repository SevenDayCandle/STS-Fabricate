package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.actions.pileSelection.SummonFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Summon extends PMove_Select
{
    public static final PSkillData DATA = register(PMove_Summon.class, PCLEffectType.CardGroupFull)
            .pclOnly();

    public PMove_Summon()
    {
        this(1, PCLCardTarget.SingleAlly, (PCLCardGroupHelper) null);
    }

    public PMove_Summon(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Summon(int amount, PCLCardTarget target, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.summon;
    }

    @Override
    public FuncT3<SelectFromPile, String, Integer, CardGroup[]> getAction()
    {
        return SummonFromPile::new;
    }
}
