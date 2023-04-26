package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardGeneric;

import java.util.List;

@VisibleSkill
public class PCond_HavePlayedThis extends PCond_HaveCardThis
{
    public static final PSkillData<PField_CardGeneric> DATA = register(PCond_HavePlayedThis.class, PField_CardGeneric.class)
            .selfTarget();

    public PCond_HavePlayedThis()
    {
        this(1);
    }

    public PCond_HavePlayedThis(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PCond_HavePlayedThis(int amount)
    {
        super(DATA, amount);
    }

    @Override
    public List<AbstractCard> getCardPile()
    {
        return fields.forced ? AbstractDungeon.actionManager.cardsPlayedThisCombat : AbstractDungeon.actionManager.cardsPlayedThisTurn;
    }

    @Override
    public EUITooltip getActionTooltip()
    {
        return PGR.core.tooltips.play;
    }
}