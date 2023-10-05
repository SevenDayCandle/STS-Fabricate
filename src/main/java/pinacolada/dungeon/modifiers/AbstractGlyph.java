package pinacolada.dungeon.modifiers;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.RunModStrings;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.STSConfigItem;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

public abstract class AbstractGlyph {
    public final String ID;
    public final STSConfigItem<Integer> configOption;
    public final int ascensionRequirement;
    public final int ascensionStep;
    public final int baseAmount;
    public final int baseAmountStep;
    public RunModStrings strings;

    public AbstractGlyph(String ID, STSConfigItem<Integer> configOption, int ascensionRequirement, int ascensionStep) {
        this(ID, configOption, ascensionRequirement, ascensionStep, 1, 1);
    }

    public AbstractGlyph(String ID, STSConfigItem<Integer> configOption, int ascensionRequirement, int ascensionStep, int baseAmount, int baseAmountStep) {
        this.ID = ID;
        this.configOption = configOption;
        this.ascensionRequirement = ascensionRequirement;
        this.ascensionStep = Math.max(1, ascensionStep);
        this.baseAmount = baseAmount;
        this.baseAmountStep = baseAmountStep;
        this.strings = PGR.getRunModStrings(ID);
    }

    public static String createFullID(Class<? extends AbstractGlyph> type) {
        return PGR.core.createID(type.getSimpleName());
    }

    // TODO call this in PCLDungeon
    public void atBattleStart(int counter) {
    }

    public String getDescription(int level) {
        return EUIUtils.format(getDescriptionBase(), baseAmountStep);
    }

    protected String getDescriptionBase() {
        return strings != null ? strings.DESCRIPTION : EUIUtils.EMPTY_STRING;
    }

    public String getDescriptionInGame(int counter) {
        return EUIUtils.format(getDescriptionBase(), GameUtilities.inGame() ? getPotency(counter) : baseAmount, baseAmountStep);
    }

    public Texture getImage() {
        return EUIRM.getTexture(PGR.getRunModImage(ID));
    }

    public String getLockedTooltipDescription() {
        return EUIUtils.format(PGR.core.strings.csel_unlocksAt, ascensionRequirement);
    }

    public int getPotency(int counter) {
        return baseAmount + counter * (AbstractDungeon.actNum - 1) * baseAmountStep;
    }

    public String getTitle() {
        return strings != null ? strings.NAME : EUIUtils.EMPTY_STRING;
    }
}