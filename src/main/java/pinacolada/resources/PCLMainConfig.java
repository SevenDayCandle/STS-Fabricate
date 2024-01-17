package pinacolada.resources;

import basemod.BaseMod;
import basemod.ModPanel;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.STSConfigItem;
import extendedui.configuration.STSSerializedConfigItem;
import extendedui.configuration.STSStringConfigItem;
import extendedui.ui.settings.BasemodSettingsPage;
import extendedui.ui.settings.ExtraModSettingsPanel;
import extendedui.ui.tooltips.EUIPreview;
import pinacolada.cards.base.PCLCardData;
import pinacolada.dungeon.PCLDungeon;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.utilities.GameUtilities;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

public class PCLMainConfig extends AbstractConfig {
    private static final String MOD_ID = "PCL";
    private static ExtraModSettingsPanel.Category pclCategory;
    private HashSet<String> tips = null;
    public STSConfigItem<Boolean> abbreviateEffects = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("AbbreviateEffects"), false);
    public STSConfigItem<Boolean> cropCardImages = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("UseCroppedPortrait"), false);
    public STSConfigItem<Boolean> displayCardTagDescription = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("DisplayCardTagDescription"), false);
    public STSConfigItem<Boolean> enableCustomAugments = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("EnableCustomAugments"), false);
    public STSConfigItem<Boolean> enableCustomBlights = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("EnableCustomBlights"), false);
    public STSConfigItem<Boolean> enableCustomCards = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("EnableCustomCards"), false);
    public STSConfigItem<Boolean> enableCustomPotions = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("EnableCustomPotions"), false);
    public STSConfigItem<Boolean> enableCustomRelics = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("EnableCustomRelics"), false);
    public STSConfigItem<Boolean> fabricatePopup = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("FabricatePopup"), false);
    public STSConfigItem<Boolean> lowVRAM = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("LowVRAM"), false);
    public STSConfigItem<Boolean> madnessReplacements = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("MadnessReplacements"), false);
    public STSConfigItem<Boolean> removeLineBreaks = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("RemoveLineBreaks"), false);
    public STSConfigItem<Boolean> replaceCardsPCL = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("ReplaceCardsPCL"), false);
    public STSConfigItem<Boolean> showCardTarget = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("ShowCardTarget"), false);
    public STSConfigItem<Boolean> showEstimatedDamage = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("ShowEstimatedDamage"), false);
    public STSConfigItem<Boolean> showFormulaDisplay = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("ShowFormulaDisplay"), false);
    public STSConfigItem<Boolean> showIrrelevantProperties = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("ShowIrrelevantProperties"), false);
    public STSConfigItem<Boolean> showUpgradeOnCardRewards = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("ShowUpgradeOnCardRewards"), false);
    public STSConfigItem<Boolean> vanillaLibraryScreen = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("VanillaLibraryScreen"), false);
    public STSConfigItem<Boolean> tourBlightPrimary = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourBlightPrimary"), false);
    public STSConfigItem<Boolean> tourCardAttribute = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourCardAttribute"), false);
    public STSConfigItem<Boolean> tourCardPrimary = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourCardPrimary"), false);
    public STSConfigItem<Boolean> tourCharSelect = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourCharSelect"), false);
    public STSConfigItem<Boolean> tourEditorEffect = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourEditorEffect"), false);
    public STSConfigItem<Boolean> tourEditorForm = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourEditorForm"), false);
    public STSConfigItem<Boolean> tourEditorPower = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourEditorPower"), false);
    public STSConfigItem<Boolean> tourItemScreen = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourItemScreen"), false);
    public STSConfigItem<Boolean> tourLoadout = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourLoadout"), false);
    public STSConfigItem<Boolean> tourPotionPrimary = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourPotionPrimary"), false);
    public STSConfigItem<Boolean> tourPowerPrimary = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourPowerPrimary"), false);
    public STSConfigItem<Boolean> tourRelicPrimary = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourRelicPrimary"), false);
    public STSConfigItem<Boolean> tourSeriesSelect = new STSConfigItem<Boolean>(PCLMainConfig.createFullID("TourSeriesSelect"), false);
    public STSConfigItem<Integer> ascensionGlyph0 = new STSConfigItem<Integer>(PCLMainConfig.createFullID("AscensionGlyph0"), 0);
    public STSConfigItem<Integer> ascensionGlyph1 = new STSConfigItem<Integer>(PCLMainConfig.createFullID("AscensionGlyph1"), 0);
    public STSConfigItem<Integer> augmentChance = new STSConfigItem<Integer>(PCLMainConfig.createFullID("AugmentChance"), PCLDungeon.DEFAULT_AUGMENT_CHANCE);
    public STSStringConfigItem lastCSVPath = new STSStringConfigItem(PCLMainConfig.createFullID("LastCSVPath"), "");
    public STSStringConfigItem lastImagePath = new STSStringConfigItem(PCLMainConfig.createFullID("LastImagePath"), "");
    public STSSerializedConfigItem<Vector2> damageFormulaPosition = new STSSerializedConfigItem<Vector2>(PCLMainConfig.createFullID("DamageFormulaPosition"), new Vector2(0.6f, 0.8f));

    public PCLMainConfig() {
        super(MOD_ID);
    }

    public static String createFullID(String name) {
        return PGR.BASE_PREFIX.toUpperCase(Locale.ROOT) + "-" + name;
    }

    public static String createFullID(String prefix, String name) {
        return prefix + "-" + name;
    }

    // Whenever this setting is updated, we need to force all cards and powers everywhere to refresh their descriptions
    public static void updateAllDescriptions() {
        PCLCustomPowerSlot.refreshTooltips();
        PCLCardData.invalidateTempCards();
        for (AbstractCard c : CardLibrary.getAllCards()) {
            c.initializeDescription();
        }

        if (GameUtilities.inGame()) {
            for (AbstractCard c : GameUtilities.getCardsInGame()) {
                c.initializeDescription();
            }
        }
    }

    public boolean hideTipDescription(String id) {
        if (tips == null) {
            tips = new HashSet<>();

            if (config.has(PCLMainConfig.createFullID("HideTipDescription"))) {
                Collections.addAll(tips, config.getString(PCLMainConfig.createFullID("HideTipDescription")).split("\\|"));
            }
        }

        return tips.contains(id);
    }

    public void hideTipDescription(String id, boolean value, boolean flush) {
        if (tips == null) {
            tips = new HashSet<>();
        }

        if (value) {
            if (id != null) {
                tips.add(id);
            }
        }
        else {
            tips.remove(id);
        }

        config.setString(PCLMainConfig.createFullID("HideTipDescription"), EUIUtils.joinStrings("|", tips));

        if (flush) {
            save();
        }
    }

    public void initializeOptions() {
        panel = new ModPanel();
        settingsBlock = new BasemodSettingsPage();
        panel.addUIElement(settingsBlock);

        float yPos = BASE_OPTION_OFFSET_Y * Settings.scale;
        yPos = addToggle(0, abbreviateEffects, PGR.core.strings.options_expandAbbreviatedEffects, yPos, PGR.core.strings.optionDesc_expandAbbreviatedEffects);
        yPos = addToggle(0, cropCardImages, PGR.core.strings.options_cropCardImages, yPos, PGR.core.strings.optionDesc_cropCardImages);
        yPos = addToggle(0, displayCardTagDescription, PGR.core.strings.options_displayCardTagDescription, yPos, PGR.core.strings.optionDesc_displayCardTagDescription);
        yPos = addToggle(0, removeLineBreaks, PGR.core.strings.options_removeLineBreaks, yPos, PGR.core.strings.optionDesc_removeLineBreaks);
        yPos = addToggle(0, showCardTarget, PGR.core.strings.options_showCardTarget, yPos, PGR.core.strings.optionDesc_showCardTarget);
        yPos = addToggle(0, showEstimatedDamage, PGR.core.strings.options_showEstimatedDamage, yPos, PGR.core.strings.optionDesc_showEstimatedDamage);
        yPos = addToggle(0, showFormulaDisplay, PGR.core.strings.options_showFormulaDisplay, yPos, PGR.core.strings.optionDesc_showFormulaDisplay);
        yPos = addToggle(0, showIrrelevantProperties, PGR.core.strings.options_hideIrrelevantAffinities, yPos, PGR.core.strings.optionDesc_hideIrrelevantAffinities);
        yPos = addToggle(0, showUpgradeOnCardRewards, PGR.core.strings.options_showUpgradeToggle, yPos, PGR.core.strings.optionDesc_showUpgradeToggle);
        yPos = addToggle(0, lowVRAM, PGR.core.strings.options_lowVRAM, yPos, PGR.core.strings.optionDesc_lowVRAM);
        yPos = addToggle(0, fabricatePopup, PGR.core.strings.options_fabricatePopup, yPos, PGR.core.strings.optionDesc_fabricatePopup);

        yPos = BASE_OPTION_OFFSET_Y * Settings.scale;
        yPos = addToggle(1, enableCustomCards, PGR.core.strings.options_enableCustomCards, yPos, PGR.core.strings.optionDesc_onlyNewRuns);
        yPos = addToggle(1, enableCustomRelics, PGR.core.strings.options_enableCustomRelics, yPos, PGR.core.strings.optionDesc_onlyNewRuns);
        yPos = addToggle(1, enableCustomPotions, PGR.core.strings.options_enableCustomPotions, yPos, PGR.core.strings.optionDesc_onlyNewRuns);
        yPos = addToggle(1, enableCustomBlights, PGR.core.strings.options_enableCustomBlights, yPos, PGR.core.strings.optionDesc_onlyNewRuns);
        yPos = addToggle(1, enableCustomAugments, PGR.core.strings.options_enableAugments, yPos, PGR.core.strings.optionDesc_enableAugments + EUIUtils.LEGACY_DOUBLE_SPLIT_LINE + PGR.core.strings.optionDesc_onlyNewRuns);
        yPos = addToggle(1, madnessReplacements, PGR.core.strings.options_madnessReplacements, yPos, PGR.core.strings.optionDesc_madnessReplacements);
        yPos = addToggle(1, vanillaLibraryScreen, PGR.core.strings.options_vanillaCustomRunMenu, yPos, PGR.core.strings.optionDesc_vanillaCustomRunMenu);

        BaseMod.registerModBadge(ImageMaster.loadImage("images/pcl/modBadge.png"), MOD_ID, "PinaColada", "", panel);

        makeModToggle(abbreviateEffects, PGR.core.strings.options_expandAbbreviatedEffects, PGR.core.strings.optionDesc_expandAbbreviatedEffects);
        makeModToggle(cropCardImages, PGR.core.strings.options_cropCardImages, PGR.core.strings.optionDesc_cropCardImages);
        makeModToggle(displayCardTagDescription, PGR.core.strings.options_displayCardTagDescription, PGR.core.strings.optionDesc_displayCardTagDescription);
        makeModToggle(removeLineBreaks, PGR.core.strings.options_removeLineBreaks, PGR.core.strings.optionDesc_removeLineBreaks);
        makeModToggle(showCardTarget, PGR.core.strings.options_showCardTarget, PGR.core.strings.optionDesc_showCardTarget);
        makeModToggle(showEstimatedDamage, PGR.core.strings.options_showEstimatedDamage, PGR.core.strings.optionDesc_showEstimatedDamage);
        makeModToggle(showFormulaDisplay, PGR.core.strings.options_showFormulaDisplay, PGR.core.strings.optionDesc_showFormulaDisplay);
        makeModToggle(showIrrelevantProperties, PGR.core.strings.options_hideIrrelevantAffinities, PGR.core.strings.optionDesc_hideIrrelevantAffinities);
        makeModToggle(showUpgradeOnCardRewards, PGR.core.strings.options_showUpgradeToggle, PGR.core.strings.optionDesc_showUpgradeToggle);
        makeModToggle(lowVRAM, PGR.core.strings.options_lowVRAM, PGR.core.strings.optionDesc_lowVRAM);
        makeModToggle(fabricatePopup, PGR.core.strings.options_fabricatePopup, PGR.core.strings.optionDesc_fabricatePopup);

        EUIConfiguration.enableDescriptionIcons.addListener(val -> updateAllDescriptions());
        displayCardTagDescription.addListener(val -> updateAllDescriptions());
        abbreviateEffects.addListener(val -> updateAllDescriptions());
        removeLineBreaks.addListener(val -> updateAllDescriptions());
    }

    public void load(int slot) {
        load();
    }

    public void load() {
        try {
            config = new SpireConfig(MOD_ID, MOD_ID);
            loadImpl();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadImpl() {
        abbreviateEffects.addConfig(config);
        ascensionGlyph0.addConfig(config);
        ascensionGlyph1.addConfig(config);
        augmentChance.addConfig(config);
        cropCardImages.addConfig(config);
        damageFormulaPosition.addConfig(config);
        displayCardTagDescription.addConfig(config);
        enableCustomAugments.addConfig(config);
        enableCustomBlights.addConfig(config);
        enableCustomCards.addConfig(config);
        enableCustomPotions.addConfig(config);
        enableCustomRelics.addConfig(config);
        fabricatePopup.addConfig(config);
        lowVRAM.addConfig(config);
        removeLineBreaks.addConfig(config);
        showCardTarget.addConfig(config);
        showEstimatedDamage.addConfig(config);
        showFormulaDisplay.addConfig(config);
        showIrrelevantProperties.addConfig(config);
        showUpgradeOnCardRewards.addConfig(config);
        vanillaLibraryScreen.addConfig(config);
        lastCSVPath.addConfig(config);
        lastImagePath.addConfig(config);
        madnessReplacements.addConfig(config);
        replaceCardsPCL.addConfig(config);
        tourBlightPrimary.addConfig(config);
        tourCardAttribute.addConfig(config);
        tourCardPrimary.addConfig(config);
        tourCharSelect.addConfig(config);
        tourEditorEffect.addConfig(config);
        tourEditorForm.addConfig(config);
        tourEditorPower.addConfig(config);
        tourItemScreen.addConfig(config);
        tourLoadout.addConfig(config);
        tourPotionPrimary.addConfig(config);
        tourPowerPrimary.addConfig(config);
        tourRelicPrimary.addConfig(config);
        tourSeriesSelect.addConfig(config);
    }
}