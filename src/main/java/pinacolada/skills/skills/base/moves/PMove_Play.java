package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.PlayFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMove_Play extends PMove_Select
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Play.class, PField_CardCategory.class);

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
        super(DATA, target, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.play;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return PlayFromPile::new;
    }
}
