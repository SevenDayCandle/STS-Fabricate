package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.PlayFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Play extends PMove_Select
{
    public static final PSkillData DATA = register(PMove_Play.class, PCLEffectType.CardGroupFull);

    public PMove_Play()
    {
        this(1, PCLCardTarget.None, (PCLCardGroupHelper) null);
    }

    public PMove_Play(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Play(int amount, PCLCardTarget target, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.play;
    }

    @Override
    public FuncT3<SelectFromPile, String, Integer, CardGroup[]> getAction()
    {
        return PlayFromPile::new;
    }
}
