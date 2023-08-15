package pinacolada.blights.common;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import pinacolada.blights.PCLBlight;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public abstract class AbstractGlyphBlight extends PCLBlight {
    public static final String ID = createFullID(AbstractGlyphBlight.class);
    public static final float RENDER_SCALE = 0.8f;

    public final STSConfigItem<Integer> configOption;
    public final int ascensionRequirement;
    public final int ascensionStep;
    public final int baseAmount;
    public final int baseAmountStep;
    public int cacheMinimumLevel;

    public AbstractGlyphBlight(String ID, STSConfigItem<Integer> configOption, int ascensionRequirement, int ascensionStep) {
        this(ID, configOption, ascensionRequirement, ascensionStep, 1, 1);
    }

    public AbstractGlyphBlight(String ID, STSConfigItem<Integer> configOption, int ascensionRequirement, int ascensionStep, int baseAmount, int baseAmountStep) {
        super(ID);
        this.outlineImg = EUIRM.getTexture(PGR.getBlightOutlineImage(ID));
        this.ascensionRequirement = ascensionRequirement;
        this.ascensionStep = Math.max(1, ascensionStep);
        this.baseAmount = baseAmount;
        this.baseAmountStep = baseAmountStep;
        this.configOption = configOption;
        this.counter = configOption.get();
        this.scale = RENDER_SCALE; // Because they end up looking larger in game than in the character select screen
        updateDescription();
    }

    public void addAmount(int amount) {
        this.counter += amount;
    }

    public String getAscensionTooltipDescription(int ascensionLevel) {
        return EUIUtils.format(strings.DESCRIPTION[2], description, ascensionLevel, cacheMinimumLevel);
    }

    public String getLockedTooltipDescription() {
        return EUIUtils.format(strings.DESCRIPTION[3], ascensionRequirement);
    }

    public int getMinimumLevel(int ascensionLevel) {
        cacheMinimumLevel = 0; //Math.max(0,(ascensionLevel - ascensionRequirement + ascensionStep) / ascensionStep);
        return cacheMinimumLevel;
    }

    public int getPotency() {
        return baseAmount + this.counter * (AbstractDungeon.actNum - 1) * baseAmountStep;
    }

    public String getUpdatedDescription() {
        return formatDescription(0, GameUtilities.inGame() ? getPotency() : baseAmount, baseAmountStep);
    }

    @Override
    public void renderOutline(Color c, SpriteBatch sb, boolean inTopPanel) {
    }

    @Override
    public void renderOutline(SpriteBatch sb, boolean inTopPanel) {
    }

    public void reset() {
        this.counter = 0;
    }

    public void setAmount(int amount) {
        this.counter = amount;
    }

    // Screw outlines

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
        updateDescription();
    }
}