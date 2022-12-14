package pinacolada.actions.cardManipulation;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class ModifyAffinityLevel extends GenericCardSelection
{
    protected List<PCLAffinity> affinities;
    protected boolean relative;
    protected boolean resetLevels;
    protected Color flashColor = EUIColors.gold(1).cpy();
    protected int level;

    protected ModifyAffinityLevel(AbstractCard card, CardGroup group, int amount, List<PCLAffinity> affinities, int level, boolean relative, boolean resetLevels)
    {
        super(card, group, amount);

        this.affinities = affinities;
        this.level = level;
        this.relative = relative;
        this.resetLevels = resetLevels;
    }

    protected ModifyAffinityLevel(AbstractCard card, CardGroup group, int amount, List<PCLAffinity> affinities, int level, boolean relative)
    {
        this(card, group, amount, affinities, level, relative, false);
    }

    public ModifyAffinityLevel(CardGroup group, int amount, List<PCLAffinity> affinities, int level, boolean relative, boolean resetLevels)
    {
        this(null, group, amount, affinities, level, relative, resetLevels);
    }

    public ModifyAffinityLevel(CardGroup group, int amount, List<PCLAffinity> affinities, int level, boolean relative)
    {
        this(null, group, amount, affinities, level, relative);
    }

    public ModifyAffinityLevel(AbstractCard card, List<PCLAffinity> affinities, int level, boolean relative, boolean resetLevels)
    {
        this(card, null, 1, affinities, level, relative, resetLevels);
    }

    public ModifyAffinityLevel(AbstractCard card, List<PCLAffinity> affinities, int level, boolean relative)
    {
        this(card, null, 1, affinities, level, relative);
    }

    @Override
    protected boolean canSelect(AbstractCard card)
    {
        return super.canSelect(card) && card instanceof PCLCard;
    }

    @Override
    protected void selectCard(AbstractCard card)
    {
        super.selectCard(card);

        if (flashColor != null)
        {
            GameUtilities.flash(card, flashColor, true);
        }

        if (resetLevels)
        {
            GameUtilities.resetAffinityLevels(card);
        }

        for (PCLAffinity affinity : affinities)
        {
            GameUtilities.modifyAffinityLevel(card, affinity, level, relative);
        }
    }

    public ModifyAffinityLevel flash(Color flashColor)
    {
        this.flashColor = flashColor;

        return this;
    }
}
