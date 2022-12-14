package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.actions.pileSelection.UpgradeFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Upgrade extends PMove_Select
{
    public static final PSkillData DATA = register(PMove_Upgrade.class, PCLEffectType.CardGroup).selfTarget();

    public PMove_Upgrade()
    {
        this(1, (PCLCardGroupHelper) null);
    }

    public PMove_Upgrade(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Upgrade(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.upgrade;
    }

    @Override
    public FuncT3<SelectFromPile, String, Integer, CardGroup[]> getAction()
    {
        return UpgradeFromPile::new;
    }
}
