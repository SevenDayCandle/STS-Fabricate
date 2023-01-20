package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;

import java.util.List;

@VisibleSkill
public class PCond_HavePlayed extends PCond_Have
{
    public static final PSkillData<PField_CardCategory> DATA = register(PCond_HavePlayed.class, PField_CardCategory.class)
            .selfTarget();

    public PCond_HavePlayed()
    {
        this(1);
    }

    public PCond_HavePlayed(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_HavePlayed(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public List<AbstractCard> getCardPile()
    {
        return AbstractDungeon.actionManager.cardsPlayedThisTurn;
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.play;
    }
}
