package pinacolada.blights;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.localization.BlightStrings;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.TooltipProvider;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class PCLBlight extends AbstractBlight implements TooltipProvider
{
    public final BlightStrings strings;
    protected final int initialAmount;
    public ArrayList<EUITooltip> tips;
    public EUITooltip mainTooltip;

    public PCLBlight(String id)
    {
        this(id, PGR.getBlightStrings(id), -1);
    }

    public PCLBlight(String id, int amount)
    {
        this(id, PGR.getBlightStrings(id), amount);
    }

    public PCLBlight(String id, BlightStrings strings, int amount)
    {
        super(id, strings.NAME, GameUtilities.EMPTY_STRING, "durian.png", true);

        this.img = EUIRM.getTexture(PGR.getBlightImage(id));
        this.outlineImg = EUIRM.getTexture(PGR.getBlightOutlineImage(id));
        this.initialAmount = amount;
        this.counter = amount;
        this.strings = strings;
        updateDescription();
    }

    public static String createFullID(Class<? extends PCLBlight> type)
    {
        return PGR.core.createID(type.getSimpleName());
    }

    protected String formatDescription(int index, Object... args)
    {
        return EUIUtils.format(strings.DESCRIPTION[index], args);
    }

    @Override
    public List<EUITooltip> getTips()
    {
        return tips;
    }

    public String getUpdatedDescription()
    {
        return formatDescription(0, counter);
    }

    public PCLBlight makeCopy()
    {
        try
        {
            return getClass().getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            EUIUtils.logError(this, e.getMessage());
            return null;
        }
    }

    @Override
    public void renderTip(SpriteBatch sb)
    {
        EUITooltip.queueTooltips(this);
    }

    @Override
    protected void initializeTips()
    {
        if (tips == null)
        {
            tips = new ArrayList<>();
        }
        else
        {
            tips.clear();
        }

        mainTooltip = new EUITooltip(name, description);
        tips.add(mainTooltip);
        EUIGameUtils.scanForTips(description, tips);
    }

    @Override
    public void updateDescription()
    {
        description = getUpdatedDescription();
        if (tips == null)
        {
            initializeTips();
        }
        if (tips.size() > 0)
        {
            tips.get(0).setDescriptions(description);
        }
    }
}
