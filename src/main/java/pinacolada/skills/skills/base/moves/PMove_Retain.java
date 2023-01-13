package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.RetainFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMove_Retain extends PMove_Select
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_Retain.class, PField_CardCategory.class).selfTarget();

    public PMove_Retain()
    {
        this(1);
    }

    public PMove_Retain(PSkillSaveData content)
    {
        super(content);
    }

    public PMove_Retain(int amount, PCLCardGroupHelper... h)
    {
        super(DATA, amount, h);
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.retain;
    }

    @Override
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return RetainFromPile::new;
    }

    @Override
    public String getSubText()
    {
        return useParent ? TEXT.actions.retain(getInheritedString()) :
                fields.hasGroups() ? TEXT.actions.retain(amount <= 0 ? TEXT.subjects.all : getAmountRawString(), fields.getFullCardString())
                : TEXT.actions.retain(TEXT.subjects.thisObj);
    }
}
