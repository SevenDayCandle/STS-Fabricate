package pinacolada.blights;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.relics.PCLRelic;
import pinacolada.relics.PCLRelicData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class PCLBlight extends AbstractBlight implements KeywordProvider {
    public static final float RENDER_SCALE = 0.8f;
    public final PCLBlightData blightData;
    public ArrayList<EUIKeywordTooltip> tips;
    public EUIKeywordTooltip mainTooltip;

    public PCLBlight(PCLBlightData data) {
        super(data.ID, data.strings.NAME, GameUtilities.EMPTY_STRING, "durian.png", true);
        this.blightData = data;
        this.scale = RENDER_SCALE; // Because they end up looking larger in game than in the character select screen
        setupImages();
        updateDescription();
    }

    public static String createFullID(Class<? extends PCLBlight> type) {
        return PGR.core.createID(type.getSimpleName());
    }

    protected static PCLBlightData register(Class<? extends PCLBlight> type) {
        return register(type, PGR.core);
    }

    protected static PCLBlightData register(Class<? extends PCLBlight> type, PCLResources<?, ?, ?, ?> resources) {
        return registerBlightData(new PCLBlightData(type, resources));
    }

    protected static <T extends PCLBlightData> T registerBlightData(T cardData) {
        return PCLBlightData.registerData(cardData);
    }

    protected String formatDescription(int index, Object... args) {
        return EUIUtils.format(blightData.strings.DESCRIPTION[index], args);
    }

    @Override
    public List<EUIKeywordTooltip> getTips() {
        return tips;
    }

    @Override
    public List<EUIKeywordTooltip> getTipsForFilters() {
        return tips.subList(1, tips.size());
    }

    public String getUpdatedDescription() {
        return formatDescription(0, counter);
    }

    @Override
    public void initializeTips() {
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

    public void loadImage(String path) {
        Texture t = EUIRM.getTexture(path, true, false);
        if (t == null) {
            path = PCLCoreImages.CardAffinity.unknown.path();
            t = EUIRM.getTexture(path, true, false);
        }
        this.img = t;
        this.outlineImg = t;
    }

    public PCLBlight makeCopy() {
        try {
            return blightData.create();
        }
        catch (Exception e) {
            return null;
        }
    }

    protected void preSetup(PCLRelicData data) {

    }

    // TODO add outlines
    @Override
    public void renderOutline(Color c, SpriteBatch sb, boolean inTopPanel) {
    }

    @Override
    public void renderOutline(SpriteBatch sb, boolean inTopPanel) {
    }

    @Override
    public void renderTip(SpriteBatch sb) {
        EUITooltip.queueTooltips(this);
    }

    public void setupImages() {
        loadImage(blightData.imagePath);
    }

    public void update() {
        super.update();
        if (this.isDone) {
            if (this.hb.hovered && AbstractDungeon.topPanel.potionUi.isHidden) {
                this.scale = Settings.scale;
            }
            else {
                this.scale = MathHelper.scaleLerpSnap(RENDER_SCALE, Settings.scale);
            }
        }
        // TODO move elsewhere
        if (this.hb.justHovered) {
            updateDescription();
        }
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
