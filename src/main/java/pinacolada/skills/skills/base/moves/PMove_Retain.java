package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.piles.RetainCards;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PMove_Retain extends PMove_Select<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA =
            register(PMove_Retain.class, PField_CardCategory.class)
                    .setGroups(PCLCardGroupHelper.Hand)
                    .selfTarget()
                    .setExtra(0, DEFAULT_MAX);

    public PMove_Retain()
    {
        this(1);
    }

    public PMove_Retain(PSkillSaveData content)
    {
        super(DATA, content);
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
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return RetainCards::new;
    }

    @Override
    public String getSubText()
    {
        return useParent ? TEXT.act_retain(getInheritedString()) :
                fields.hasGroups() ? TEXT.act_retain(getAmountRawOrAllString(), fields.getFullCardString())
                : TEXT.act_retain(TEXT.subjects_thisCard);
    }
}
