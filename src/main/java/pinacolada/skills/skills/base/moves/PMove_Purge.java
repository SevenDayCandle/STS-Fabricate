package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.PurgeFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Purge extends PMove_Select
{
    public static final PSkillData DATA = register(PMove_Purge.class, PCLEffectType.CardGroupFull).selfTarget();

    public PMove_Purge()
    {
        this(1, (PCLCardGroupHelper) null);
    }

    public PMove_Purge(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Purge(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
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
