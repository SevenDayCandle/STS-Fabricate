package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.PlayFromPile;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PMove_Play extends PMove_Select<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Play.class, PField_CardCategory.class)
            .setExtra(0, DEFAULT_MAX);

    public PMove_Play()
    {
        this(1, PCLCardTarget.None);
    }

    public PMove_Play(PSkillSaveData content)
    {
        super(DATA, content);
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
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return PlayFromPile::new;
    }
}
