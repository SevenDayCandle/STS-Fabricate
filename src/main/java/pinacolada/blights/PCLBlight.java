package pinacolada.blights;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.localization.BlightStrings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class PCLBlight extends AbstractBlight implements KeywordProvider {
    protected final int initialAmount;
    public final BlightStrings strings;
    public ArrayList<EUIKeywordTooltip> tips;
    public EUIKeywordTooltip mainTooltip;

    public PCLBlight(String id) {
        this(id, PGR.getBlightStrings(id), -1);
    }

    public PCLBlight(String id, BlightStrings strings, int amount) {
        super(id, strings.NAME, GameUtilities.EMPTY_STRING, "durian.png", true);

        this.img = EUIRM.getTexture(PGR.getBlightImage(id));
        this.outlineImg = EUIRM.getTexture(PGR.getBlightOutlineImage(id));
        this.initialAmount = amount;
        this.counter = amount;
        this.strings = strings;
        updateDescription();
    }

    public PCLBlight(String id, int amount) {
        this(id, PGR.getBlightStrings(id), amount);
    }

    public static String createFullID(Class<? extends PCLBlight> type) {
        return PGR.core.createID(type.getSimpleName());
    }

    protected String formatDescription(int index, Object... args) {
        return EUIUtils.format(strings.DESCRIPTION[index], args);
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tips;
    }

    public String getUpdatedDescription() {
        return formatDescription(0, counter);
    }

    public PCLBlight makeCopy() {
        try {
            return getClass().getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            EUIUtils.logError(this, e.getMessage());
            return null;
        }
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(this);
    }

    @Override
    protected void initializeTips() {
        if (tips == null) {
            tips = new ArrayList<>();
        }
        else {
            tips.clear();
        }

        mainTooltip = new EUIKeywordTooltip(name, description);
        tips.add(mainTooltip);
        EUITooltip.scanForTips(description, tips);
    }

    @Override
    public void updateDescription() {
        description = getUpdatedDescription();
        if (tips == null) {
            initializeTips();
        }
        if (tips.size() > 0) {
            tips.get(0).setDescription(description);
        }
    }
}
