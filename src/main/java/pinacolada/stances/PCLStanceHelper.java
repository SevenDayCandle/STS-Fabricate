package pinacolada.stances;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.stances.AbstractStance;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT0;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.*;
import java.util.stream.Collectors;

public class PCLStanceHelper implements TooltipProvider
{
    public static final Map<String, PCLStanceHelper> ALL = new HashMap<>();

    public static final PCLStanceHelper CalmStance = new PCLStanceHelper(com.megacrit.cardcrawl.stances.CalmStance.STANCE_ID, PGR.core.tooltips.calm, PCLAffinity.General, com.megacrit.cardcrawl.stances.CalmStance::new);
    public static final PCLStanceHelper DivinityStance = new PCLStanceHelper(com.megacrit.cardcrawl.stances.DivinityStance.STANCE_ID, PGR.core.tooltips.divinity, PCLAffinity.General, com.megacrit.cardcrawl.stances.DivinityStance::new);
    public static final PCLStanceHelper WrathStance = new PCLStanceHelper(com.megacrit.cardcrawl.stances.WrathStance.STANCE_ID, PGR.core.tooltips.wrath, PCLAffinity.General, com.megacrit.cardcrawl.stances.WrathStance::new);
    public final EUITooltip tooltip;
    public final PCLAffinity affinity;
    public final String ID;
    public final AbstractCard.CardColor[] allowedColors;
    protected final FuncT0<AbstractStance> constructor;
    public PCLStanceHelper(String stanceID, EUITooltip tooltip, PCLAffinity affinity, FuncT0<AbstractStance> constructor, AbstractCard.CardColor... allowedColors)
    {
        this.ID = stanceID;
        this.tooltip = tooltip;
        this.affinity = affinity;
        this.constructor = constructor;
        this.allowedColors = allowedColors;

        ALL.putIfAbsent(stanceID, this);
    }

    public static PCLStanceHelper get(String stanceID)
    {
        return ALL.get(stanceID);
    }

    public static PCLStanceHelper get(PCLAffinity affinity)
    {
        return EUIUtils.find(ALL.values(), h -> affinity.equals(h.affinity));
    }

    public static List<PCLStanceHelper> inGameValues(AbstractCard.CardColor targetColor)
    {
        return EUIUtils.filter(ALL.values(), s -> s.allowedColors == null || s.allowedColors.length == 0);
    }

    public static PCLStanceHelper randomHelper()
    {
        return GameUtilities.getRandomElement(inGameValues(AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : null));
    }

    public static AbstractStance randomStance()
    {
        return randomHelper().create();
    }

    public static Collection<PCLStanceHelper> values(AbstractCard.CardColor targetColor)
    {
        return targetColor == null ? values() : EUIUtils.filter(ALL.values(), s -> s.allowedColors == null || s.allowedColors.length == 0 || EUIUtils.any(s.allowedColors, t -> t == targetColor))
                .stream()
                .sorted((a, b) -> a.tooltip != null && b.tooltip != null ? StringUtils.compare(a.tooltip.title, b.tooltip.title) : 0).collect(Collectors.toList());
    }

    public static Collection<PCLStanceHelper> values()
    {
        return ALL.values().stream().sorted((a, b) -> StringUtils.compare(a.tooltip.title, b.tooltip.title)).collect(Collectors.toList());
    }

    public AbstractStance create()
    {
        if (constructor != null)
        {
            return constructor.invoke();
        }
        else
        {
            throw new RuntimeException("Do not create a PCLStanceHelper with a null constructor.");
        }
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return Collections.singletonList(tooltip);
    }














}
