package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.ReshuffleFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Reshuffle extends PMove_Select
{
    public static final PSkillData DATA = register(PMove_Reshuffle.class, PCLEffectType.CardGroupFull)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.Hand, PCLCardGroupHelper.DiscardPile, PCLCardGroupHelper.ExhaustPile);

    public PMove_Reshuffle()
    {
        this(1, (PCLCardGroupHelper) null);
    }

    public PMove_Reshuffle(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Reshuffle(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.reshuffle;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return ReshuffleFromPile::new;
    }
}
