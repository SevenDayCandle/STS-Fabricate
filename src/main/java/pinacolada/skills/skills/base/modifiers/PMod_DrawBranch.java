package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT4;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.pileSelection.DrawCards;
import pinacolada.actions.pileSelection.FetchFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

public class PMod_DrawBranch extends PMod_DoBranch
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_DrawBranch.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PMod_DrawBranch(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_DrawBranch()
    {
        super(DATA);
    }

    public PMod_DrawBranch(int amount)
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
    public FuncT4<SelectFromPile, String, AbstractCreature, Integer, CardGroup[]> getAction()
    {
        return FetchFromPile::new;
    }
}
