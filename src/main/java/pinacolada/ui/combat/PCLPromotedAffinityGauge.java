package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.ui.hitboxes.RelativeHitbox;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIColors;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.misc.PCLHotkeys;
import pinacolada.powers.special.RerollAffinityPower;
import pinacolada.resources.PGR;
import pinacolada.ui.common.AffinityKeywordButton;
import pinacolada.utilities.GameUtilities;

public class PCLPromotedAffinityGauge extends EUIBase
{

    public static final float ICON_SIZE = scale(48);
    public static final float LABEL_OFFSET = ICON_SIZE + scale(14);
    public static final int DEFAULT_REROLLS = 1;

    protected final AffinityKeywordButton currentAffinity;
    protected final AffinityKeywordButton nextAffinity;
    protected final PCLPlayerMeter parent;
    protected final float xOffset;
    protected final float yOffset;
    public RerollAffinityPower reroll;

    public PCLPromotedAffinityGauge(PCLPlayerMeter parent, float xOffset, float yOffset)
    {
        this.parent = parent;
        this.xOffset = xOffset;
        this.yOffset = yOffset;

        currentAffinity = new AffinityKeywordButton(new RelativeHitbox(parent.hb, ICON_SIZE, ICON_SIZE, xOffset, yOffset), PCLAffinity.General).setOptions(false, false).setLevel(1);
        nextAffinity = new AffinityKeywordButton(new RelativeHitbox(parent.hb, ICON_SIZE, ICON_SIZE, xOffset + 0.6f, yOffset), PCLAffinity.General).setOptions(false, false).setLevel(1);
    }

    public void addSkip(int amount)
    {
        if (reroll != null)
        {
            reroll.triggerCondition.pool.uses += 1;
            reroll.triggerCondition.refresh(false, true);
        }
    }

    public void advance(PCLAffinity... choices)
    {
        if (reroll != null)
        {
            reroll.advance(choices);
        }
    }

    public PCLAffinity get(int target)
    {
        return target == 0 ? currentAffinity.type : nextAffinity.type;
    }

    public AffinityKeywordButton getButton(int target)
    {
        return target == 0 ? currentAffinity : nextAffinity;
    }

    public void initialize()
    {
        randomizeCurrentAffinity();
        randomizeNextAffinity();
        reroll = new RerollAffinityPower(DEFAULT_REROLLS);
        reroll.setHitbox(new RelativeHitbox(parent.hb, ICON_SIZE, ICON_SIZE, xOffset + 1.2f, yOffset));
        currentAffinity.backgroundButton.setTooltip(new EUITooltip(reroll.mainTip.title, reroll.getCurrentDescription()));
        nextAffinity.backgroundButton.setTooltip(new EUITooltip(reroll.mainTip.title, reroll.getNextDescription()));
    }

    public void onStartOfTurn()
    {
        if (reroll != null)
        {
            reroll.atStartOfTurn();
        }
    }

    public PCLAffinity randomize(int target)
    {
        return target == 0 ? randomizeCurrentAffinity() : randomizeNextAffinity();
    }

    public PCLAffinity randomizeCurrentAffinity()
    {
        return setCurrentAffinity(GameUtilities.getRandomElement(PCLAffinity.getAvailableAffinities()));
    }

    public PCLAffinity randomizeNextAffinity()
    {
        return setNextAffinity(GameUtilities.getRandomElement(EUIUtils.filter(PCLAffinity.getAvailableAffinities(), a -> currentAffinity.type != a)));
    }

    public PCLAffinity set(PCLAffinity affinity, int target)
    {
        return target == 0 ? setCurrentAffinity(affinity) : setNextAffinity(affinity);
    }

    public PCLAffinity setCurrentAffinity(PCLAffinity affinity)
    {
        if (affinity == null)
        {
            return currentAffinity.type;
        }
        currentAffinity.setAffinity(affinity);
        return affinity;
    }

    public PCLAffinity setNextAffinity(PCLAffinity affinity)
    {
        if (affinity == null)
        {
            return nextAffinity.type;
        }
        nextAffinity.setAffinity(affinity);
        return affinity;
    }

    @Override
    public void updateImpl()
    {
        currentAffinity.tryUpdate();
        nextAffinity.tryUpdate();

        if (reroll != null)
        {
            reroll.update(0);
            if (currentAffinity.backgroundButton.hb.justHovered || nextAffinity.backgroundButton.hb.justHovered)
            {
                reroll.updateDescription();
            }
            if (PCLHotkeys.rerollCurrent.isJustPressed())
            {
                reroll.onClick();
            }
        }

        if (currentAffinity.backgroundButton.hb.hovered)
        {
            GameUtilities.highlightMatchingCards(currentAffinity.type == PCLAffinity.Star ? PCLAffinity.General : currentAffinity.type);
        }

        if (nextAffinity.backgroundButton.hb.hovered)
        {
            GameUtilities.highlightMatchingCards(nextAffinity.type == PCLAffinity.Star ? PCLAffinity.General : nextAffinity.type);
        }
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {

        currentAffinity.tryRender(sb);
        nextAffinity.tryRender(sb);
        if (reroll != null)
        {
            reroll.render(sb);
        }

        FontHelper.renderFontCentered(sb, FontHelper.powerAmountFont,
                PGR.core.strings.combat.current, currentAffinity.backgroundButton.hb.cX, currentAffinity.backgroundButton.hb.y + LABEL_OFFSET, EUIColors.cream(1f), 1f);
        FontHelper.renderFontCentered(sb, FontHelper.powerAmountFont,
                PGR.core.strings.combat.next, nextAffinity.backgroundButton.hb.cX, nextAffinity.backgroundButton.hb.y + LABEL_OFFSET, EUIColors.cream(1f), 1f);
    }

    public int size()
    {
        return 2;
    }
}
