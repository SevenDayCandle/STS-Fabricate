package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.interfaces.delegates.FuncT5;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.actions.pileSelection.DrawCards;
import pinacolada.actions.pileSelection.FetchFromPile;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.utilities.ListSelection;

import java.util.ArrayList;



@VisibleSkill
public class PMod_DrawPerCard extends PMod_Do
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMod_DrawPerCard.class, PField_CardCategory.class)
            .selfTarget()
            .setGroups(PCLCardGroupHelper.DrawPile);

    public PMod_DrawPerCard(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMod_DrawPerCard()
    {
        super(DATA);
    }

    public PMod_DrawPerCard(int amount)
    {
        super(DATA, PCLCardTarget.None, amount);
    }

    @Override
    protected PCLActionWithCallback<ArrayList<AbstractCard>> createPileAction(PCLUseInfo info)
    {
        return new DrawCards(amount);
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
