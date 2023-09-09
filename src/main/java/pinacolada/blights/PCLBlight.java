package pinacolada.blights;


import basemod.ReflectionHacks;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.FloatyEffect;
import extendedui.EUIGameUtils;
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

import java.util.ArrayList;
import java.util.List;

public abstract class PCLBlight extends AbstractBlight implements KeywordProvider {
    public final PCLBlightData blightData;
    public ArrayList<EUIKeywordTooltip> tips;
    public EUIKeywordTooltip mainTooltip;

    public PCLBlight(PCLBlightData data) {
        super(data.ID, data.strings.NAME, EUIUtils.EMPTY_STRING, "durian.png", true);
        this.blightData = data;
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

    protected FloatyEffect getFEffect() {
        return ReflectionHacks.getPrivate(this, AbstractBlight.class, "f_effect");
    }

    protected float getOffsetX() {
        return ReflectionHacks.getPrivate(this, AbstractBlight.class, "offsetX");
    }

    protected float getRotation() {
        return ReflectionHacks.getPrivate(this, AbstractBlight.class, "rotation");
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

        ModInfo info = EUIGameUtils.getModInfo(this);
        mainTooltip = info != null ? new EUIKeywordTooltip(name, description, info.ID) : new EUIKeywordTooltip(name, description);
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

    public void onEquip() {
        updateDescription();
    }

    protected void preSetup(PCLRelicData data) {

    }

    @Override
    public void render(SpriteBatch sb) {
        if (!Settings.hideRelics) {
            float xOffset = -64;
            float yOffset = -64;

            if (AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD && !isObtained) {
                FloatyEffect f_effect = getFEffect();
                xOffset += f_effect.x;
                yOffset += f_effect.y;
            }

            renderBlightImage(sb, Color.WHITE, xOffset, yOffset, 0.5f);
            renderCounter(sb, false);
            if (this.isDone) {
                renderFlash(sb, false);
            }
            if (this.hb.hovered && !this.isObtained && (!AbstractDungeon.isScreenUp || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.BOSS_REWARD || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.SHOP)) {
                this.renderTip(sb);
            }

            this.hb.render(sb);
        }
    }

    @Override
    public void render(SpriteBatch sb, boolean renderAmount, Color outlineColor) {
        renderBlightImage(sb,
                this.isSeen ? Color.WHITE : this.hb.hovered ? Settings.HALF_TRANSPARENT_BLACK_COLOR : Color.BLACK,
                -64f,
                -64f,
                AbstractDungeon.screen == AbstractDungeon.CurrentScreen.NEOW_UNLOCK ? MathUtils.cosDeg((float) (System.currentTimeMillis() / 5L % 360L)) : 0.5f);
        if (this.hb.hovered) {
            if (!this.isSeen) {
                PCLRelic.renderUnseenTip();
            }
            else {
                this.renderTip(sb);
            }
        }
        this.hb.render(sb);
    }

    public void renderBlightImage(SpriteBatch sb, Color color, float xOffset, float yOffset, float scaleMult) {
        sb.setColor(color);
        sb.draw(this.img, this.currentX + xOffset, this.currentY + yOffset, 64.0F, 64.0F, 128.0F, 128.0F, this.scale * scaleMult, this.scale * scaleMult, getRotation(), 0, 0, 128, 128, false, false);
    }

    @Override
    public void renderInTopPanel(SpriteBatch sb) {
        if (!Settings.hideRelics) {
            renderBlightImage(sb, Color.WHITE, getOffsetX() - 64f, -64f, 0.5f);
            this.renderCounter(sb, true);
            this.renderFlash(sb, true);
            this.hb.render(sb);
        }
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
