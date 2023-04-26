package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.utilities.GameUtilities;

import java.util.List;

public class ModifyAffinityLevel extends GenericCardSelection {
    protected List<PCLAffinity> affinities;
    protected boolean relative;
    protected boolean resetLevels;
    protected Color flashColor = EUIColors.gold(1).cpy();
    protected int level;

    public ModifyAffinityLevel(AbstractCard card, List<PCLAffinity> affinities, int level, boolean relative, boolean resetLevels) {
        this(card, 1, affinities, level, relative, resetLevels);
    }

    protected ModifyAffinityLevel(AbstractCard card, int amount, List<PCLAffinity> affinities, int level, boolean relative, boolean resetLevels) {
        super(card, amount);

        this.affinities = affinities;
        this.level = level;
        this.relative = relative;
        this.resetLevels = resetLevels;
    }

    public ModifyAffinityLevel(AbstractCard card, List<PCLAffinity> affinities, int level, boolean relative) {
        this(card, 1, affinities, level, relative);
    }

    protected ModifyAffinityLevel(AbstractCard card, int amount, List<PCLAffinity> affinities, int level, boolean relative) {
        this(card, amount, affinities, level, relative, false);
    }

    @Override
    protected boolean canSelect(AbstractCard card) {
        return super.canSelect(card) && card instanceof PCLCard;
    }

    public ModifyAffinityLevel flash(Color flashColor) {
        this.flashColor = flashColor;

        return this;
    }

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (flashColor != null) {
            GameUtilities.flash(card, flashColor, true);
        }

        if (resetLevels) {
            GameUtilities.resetAffinityLevels(card);
        }

        for (PCLAffinity affinity : affinities) {
            GameUtilities.modifyAffinityLevel(card, affinity, level, relative);
        }
    }
}
