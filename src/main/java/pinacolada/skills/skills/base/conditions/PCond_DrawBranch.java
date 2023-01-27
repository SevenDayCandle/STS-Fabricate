package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.DrawCards;
import pinacolada.actions.pileSelection.FetchFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.misc.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

@VisibleSkill
public class PCond_DrawBranch extends PCond_DoBranch
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_DrawBranch.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PCond_DrawBranch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_DrawBranch()
    {
        super(DATA);
    }

    public PCond_DrawBranch(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    public void use(PCLUseInfo info)
    {
        getActions().add(new DrawCards(amount))
                .addCallback(cards -> {
                    if (this.childEffect != null)
                    {
                        info.setData(cards);
                        branch(info, cards);
                    }
                });
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.draw;
    }

    @Override
    public FuncT5<SelectFromPile, String, AbstractCreature, Integer, ListSelection<AbstractCard>, CardGroup[]> getAction()
    {
        return FetchFromPile::new;
    }
}
