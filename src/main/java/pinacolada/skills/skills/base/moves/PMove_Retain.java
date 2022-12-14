package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.interfaces.delegates.FuncT3;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.RetainFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;

public class PMove_Retain extends PMove_Select
{
    public static final PSkillData DATA = register(PMove_Retain.class, PCLEffectType.CardGroupFull).selfTarget();

    public PMove_Retain()
    {
        this(1, (PCLCardGroupHelper) null);
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
    public FuncT3<SelectFromPile, String, Integer, CardGroup[]> getAction()
    {
        return RetainFromPile::new;
    }

    @Override
    public String getSubText()
    {
        return useParent ? TEXT.actions.retain(getInheritedString()) :
                !groupTypes.isEmpty() ? TEXT.actions.retain(amount <= 0 ? TEXT.subjects.all : getAmountRawString(), !cardIDs.isEmpty() ? getCardIDOrString() : getFullCardString(getRawString(EFFECT_CHAR)))
                : TEXT.actions.retain(TEXT.subjects.thisObj);
    }
}
