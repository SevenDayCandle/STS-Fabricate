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
import pinacolada.blights.PCLBlightData;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public abstract class AbstractGlyphBlight extends PCLBlight {
    public final STSConfigItem<Integer> configOption;
    public final int ascensionRequirement;
    public final int ascensionStep;
    public final int baseAmount;
    public final int baseAmountStep;
    public int cacheMinimumLevel;

    public AbstractGlyphBlight(PCLBlightData data, STSConfigItem<Integer> configOption, int ascensionRequirement, int ascensionStep) {
        this(data, configOption, ascensionRequirement, ascensionStep, 1, 1);
    }

    public AbstractGlyphBlight(PCLBlightData data, STSConfigItem<Integer> configOption, int ascensionRequirement, int ascensionStep, int baseAmount, int baseAmountStep) {
        super(data);
        this.ascensionRequirement = ascensionRequirement;
        this.ascensionStep = Math.max(1, ascensionStep);
        this.baseAmount = baseAmount;
        this.baseAmountStep = baseAmountStep;
        this.configOption = configOption;
        this.counter = configOption.get();
        updateDescription();
    }

    public void addAmount(int amount) {
        this.counter += amount;
    }

    public String getAscensionTooltipDescription(int ascensionLevel) {
        return EUIUtils.format(blightData.strings.DESCRIPTION[2], description, ascensionLevel, cacheMinimumLevel);
    }

    public String getLockedTooltipDescription() {
        return EUIUtils.format(blightData.strings.DESCRIPTION[3], ascensionRequirement);
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

    public void reset() {
        this.counter = 0;
    }

    public void setAmount(int amount) {
        this.counter = amount;
    }
}