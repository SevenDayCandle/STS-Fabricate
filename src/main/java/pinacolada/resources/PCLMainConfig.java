package pinacolada.resources;

import basemod.BaseMod;
import basemod.ModPanel;
import com.badlogic.gdx.math.Vector2;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.cards.AbstractCard;
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
import pinacolada.cards.base.PCLCard;
import pinacolada.utilities.GameUtilities;

import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

public class PCLMainConfig extends AbstractConfig {
    private static final String LAST_SEED_KEY = "TSDL";
    private static final String MOD_ID = "PCL";
    private static final String ASCENSIONGLYPH0 = PCLMainConfig.createFullID("AscensionGlyph0");
    private static final String ASCENSIONGLYPH1 = PCLMainConfig.createFullID("AscensionGlyph1");
    private static final String ASCENSIONGLYPH2 = PCLMainConfig.createFullID("AscensionGlyph2");
    private static final String ABBREVIATED_EFFECTS = PCLMainConfig.createFullID("AbbreviateEffects");
    private static final String CROP_CARD_PORTRAIT = PCLMainConfig.createFullID("UseCroppedPortrait");
    private static final String DAMAGE_FORMULA_POSITION = PCLMainConfig.createFullID("DamageFormulaPosition");
    private static final String DISPLAY_CARD_TAG_DESCRIPTION = PCLMainConfig.createFullID("DisplayCardTagDescription");
    private static final String DISPLAY_CARD_TAG_TEXT = PCLMainConfig.createFullID("DisplayCardTagText");
    private static final String ENABLE_CUSTOM_CARDS = PCLMainConfig.createFullID("EnableCustomCards");
    private static final String ENABLE_CUSTOM_EVENTS = PCLMainConfig.createFullID("EnableCustomEvents");
    private static final String ENABLE_CUSTOM_POTIONS = PCLMainConfig.createFullID("EnableCustomPotions");
    private static final String ENABLE_CUSTOM_RELICS = PCLMainConfig.createFullID("EnableCustomRelics");
    private static final String FABRICATE_POPUP = PCLMainConfig.createFullID("FabricatePopup");
    private static final String HIDE_TIP_DESCRIPTION = PCLMainConfig.createFullID("HideTipDescription");
    private static final String LAST_CSV_PATH = PCLMainConfig.createFullID("LastCSVPath");
    private static final String LAST_IMAGE_PATH = PCLMainConfig.createFullID("LastImagePath");
    private static final String LOW_VRAM = PCLMainConfig.createFullID("LowVRAM");
    private static final String MADNESS_REPLACEMENTS = PCLMainConfig.createFullID("MadnessReplacements");
    private static final String REMOVE_LINE_BREAKS = PCLMainConfig.createFullID("RemoveLineBreaks");
    private static final String REPLACE_CARDS_PCL = PCLMainConfig.createFullID("ReplaceCardsPCL");
    private static final String SHOW_ESTIMATED_DAMAGE = PCLMainConfig.createFullID("ShowEstimatedDamage");
    private static final String SHOW_FORMULA_DISPLAY = PCLMainConfig.createFullID("ShowFormulaDisplay");
    private static final String SHOW_IRRELEVANT_PROPERTIES = PCLMainConfig.createFullID("ShowIrrelevantProperties");
    private static final String TOUR_CARDATTRIBUTE = PCLMainConfig.createFullID("TourCardAttribute");
    private static final String TOUR_CARDPRIMARY = PCLMainConfig.createFullID("TourCardPrimary");
    private static final String TOUR_CHARSELECT = PCLMainConfig.createFullID("TourCharSelect");
    private static final String TOUR_EDITOREFFECT = PCLMainConfig.createFullID("TourEditorEffect");
    private static final String TOUR_EDITORFORM = PCLMainConfig.createFullID("TourEditorForm");
    private static final String TOUR_EDITORPOWER = PCLMainConfig.createFullID("TourEditorPower");
    private static final String TOUR_ITEMSCREEN = PCLMainConfig.createFullID("TourItemScreen");
    private static final String TOUR_LOADOUT = PCLMainConfig.createFullID("TourLoadout");
    private static final String TOUR_RELICPRIMARY = PCLMainConfig.createFullID("TourRelicPrimary");
    private static final String TOUR_SERIESSELECT = PCLMainConfig.createFullID("TourSeriesSelect");
    private static final String VANILLA_LIBRARY_SCREEN = PCLMainConfig.createFullID("VanillaLibraryScreen");
    public static final FilenameFilter JSON_FILTER = (dir, name) -> name.endsWith(".json");
    private static ExtraModSettingsPanel.Category pclCategory;
    private HashSet<String> tips = null;
    public STSConfigItem<Boolean> abbreviateEffects = new STSConfigItem<Boolean>(ABBREVIATED_EFFECTS, false);
    public STSConfigItem<Boolean> cropCardImages = new STSConfigItem<Boolean>(CROP_CARD_PORTRAIT, false);
    public STSConfigItem<Boolean> displayCardTagDescription = new STSConfigItem<Boolean>(DISPLAY_CARD_TAG_DESCRIPTION, false);
    public STSConfigItem<Boolean> enableCustomCards = new STSConfigItem<Boolean>(ENABLE_CUSTOM_CARDS, false);
    public STSConfigItem<Boolean> enableCustomEvents = new STSConfigItem<Boolean>(ENABLE_CUSTOM_EVENTS, false);
    public STSConfigItem<Boolean> enableCustomPotions = new STSConfigItem<Boolean>(ENABLE_CUSTOM_POTIONS, false);
    public STSConfigItem<Boolean> enableCustomRelics = new STSConfigItem<Boolean>(ENABLE_CUSTOM_RELICS, false);
    public STSConfigItem<Boolean> fabricatePopup = new STSConfigItem<Boolean>(FABRICATE_POPUP, false);
    public STSConfigItem<Boolean> lowVRAM = new STSConfigItem<Boolean>(LOW_VRAM, false);
    public STSConfigItem<Boolean> madnessReplacements = new STSConfigItem<Boolean>(MADNESS_REPLACEMENTS, false);
    public STSConfigItem<Boolean> removeLineBreaks = new STSConfigItem<Boolean>(REMOVE_LINE_BREAKS, false);
    public STSConfigItem<Boolean> replaceCardsPCL = new STSConfigItem<Boolean>(REPLACE_CARDS_PCL, false);
    public STSConfigItem<Boolean> showEstimatedDamage = new STSConfigItem<Boolean>(SHOW_ESTIMATED_DAMAGE, false);
    public STSConfigItem<Boolean> showFormulaDisplay = new STSConfigItem<Boolean>(SHOW_FORMULA_DISPLAY, false);
    public STSConfigItem<Boolean> showIrrelevantProperties = new STSConfigItem<Boolean>(SHOW_IRRELEVANT_PROPERTIES, false);
    public STSConfigItem<Boolean> vanillaLibraryScreen = new STSConfigItem<Boolean>(VANILLA_LIBRARY_SCREEN, false);
    public STSConfigItem<Boolean> tourCardAttribute = new STSConfigItem<Boolean>(TOUR_CARDATTRIBUTE, false);
    public STSConfigItem<Boolean> tourCardPrimary = new STSConfigItem<Boolean>(TOUR_CARDPRIMARY, false);
    public STSConfigItem<Boolean> tourCharSelect = new STSConfigItem<Boolean>(TOUR_CHARSELECT, false);
    public STSConfigItem<Boolean> tourEditorEffect = new STSConfigItem<Boolean>(TOUR_EDITOREFFECT, false);
    public STSConfigItem<Boolean> tourEditorForm = new STSConfigItem<Boolean>(TOUR_EDITORFORM, false);
    public STSConfigItem<Boolean> tourEditorPower = new STSConfigItem<Boolean>(TOUR_EDITORPOWER, false);
    public STSConfigItem<Boolean> tourItemScreen = new STSConfigItem<Boolean>(TOUR_ITEMSCREEN, false);
    public STSConfigItem<Boolean> tourLoadout = new STSConfigItem<Boolean>(TOUR_LOADOUT, false);
    public STSConfigItem<Boolean> tourRelicPrimary = new STSConfigItem<Boolean>(TOUR_RELICPRIMARY, false);
    public STSConfigItem<Boolean> tourSeriesSelect = new STSConfigItem<Boolean>(TOUR_SERIESSELECT, false);
    public STSConfigItem<Integer> ascensionGlyph0 = new STSConfigItem<Integer>(ASCENSIONGLYPH0, 0);
    public STSConfigItem<Integer> ascensionGlyph1 = new STSConfigItem<Integer>(ASCENSIONGLYPH1, 0);
    public STSConfigItem<Integer> ascensionGlyph2 = new STSConfigItem<Integer>(ASCENSIONGLYPH2, 0);
    public STSStringConfigItem lastCSVPath = new STSStringConfigItem(LAST_CSV_PATH, "");
    public STSStringConfigItem lastImagePath = new STSStringConfigItem(LAST_IMAGE_PATH, "");
    public STSStringConfigItem lastSeed = new STSStringConfigItem(LAST_SEED_KEY, "");
    public STSSerializedConfigItem<Vector2> damageFormulaPosition = new STSSerializedConfigItem<Vector2>(DAMAGE_FORMULA_POSITION, new Vector2(0.6f, 0.8f));

    public PCLMainConfig() {
        super(MOD_ID);
    }

    public static String createFullID(String name) {
        return PGR.BASE_PREFIX.toUpperCase(Locale.ROOT) + "-" + name;
    }

    public static String createFullID(String prefix, String name) {
        return prefix + "-" + name;
    }

    public boolean hideTipDescription(String id) {
        if (tips == null) {
            tips = new HashSet<>();

            if (config.has(HIDE_TIP_DESCRIPTION)) {
                Collections.addAll(tips, config.getString(HIDE_TIP_DESCRIPTION).split("\\|"));
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

        config.setString(HIDE_TIP_DESCRIPTION, EUIUtils.joinStrings("|", tips));

        if (flush) {
            save();
        }
    }

    public void initializeOptions() {
        panel = new ModPanel();
        settingsBlock = new BasemodSettingsPage();
        panel.addUIElement(settingsBlock);

        int yPos = BASE_OPTION_OFFSET_Y;

        yPos = addToggle(0, abbreviateEffects, PGR.core.strings.options_expandAbbreviatedEffects, yPos, PGR.core.strings.optionDesc_expandAbbreviatedEffects);
        yPos = addToggle(0, cropCardImages, PGR.core.strings.options_cropCardImages, yPos, PGR.core.strings.optionDesc_cropCardImages);
        yPos = addToggle(0, displayCardTagDescription, PGR.core.strings.options_displayCardTagDescription, yPos, PGR.core.strings.optionDesc_displayCardTagDescription);
        yPos = addToggle(0, removeLineBreaks, PGR.core.strings.options_removeLineBreaks, yPos, PGR.core.strings.optionDesc_removeLineBreaks);
        yPos = addToggle(0, vanillaLibraryScreen, PGR.core.strings.options_vanillaCustomRunMenu, yPos, PGR.core.strings.optionDesc_vanillaCustomRunMenu);
        yPos = addToggle(0, showEstimatedDamage, PGR.core.strings.options_showEstimatedDamage, yPos, PGR.core.strings.optionDesc_showEstimatedDamage);
        yPos = addToggle(0, showFormulaDisplay, PGR.core.strings.options_showFormulaDisplay, yPos, PGR.core.strings.optionDesc_showFormulaDisplay);
        yPos = addToggle(0, showIrrelevantProperties, PGR.core.strings.options_hideIrrelevantAffinities, yPos, PGR.core.strings.optionDesc_hideIrrelevantAffinities);
        yPos = addToggle(0, madnessReplacements, PGR.core.strings.options_madnessReplacements, yPos, PGR.core.strings.optionDesc_madnessReplacements);
        yPos = addToggle(0, lowVRAM, PGR.core.strings.options_lowVRAM, yPos, PGR.core.strings.optionDesc_lowVRAM);
        yPos = addToggle(0, fabricatePopup, PGR.core.strings.options_fabricatePopup, yPos, PGR.core.strings.optionDesc_fabricatePopup);
        yPos = addToggle(0, enableCustomCards, PGR.core.strings.options_enableCustomCards, yPos, PGR.core.strings.optionDesc_onlyNewRuns);
        yPos = addToggle(0, enableCustomRelics, PGR.core.strings.options_enableCustomRelics, yPos, PGR.core.strings.optionDesc_onlyNewRuns);
        yPos = addToggle(0, enableCustomPotions, PGR.core.strings.options_enableCustomPotions, yPos, PGR.core.strings.optionDesc_onlyNewRuns);

        BaseMod.registerModBadge(ImageMaster.loadImage("images/pcl/modBadge.png"), MOD_ID, "PinaColada", "", panel);

        makeModToggle(abbreviateEffects, PGR.core.strings.options_expandAbbreviatedEffects, PGR.core.strings.optionDesc_expandAbbreviatedEffects);
        makeModToggle(cropCardImages, PGR.core.strings.options_cropCardImages, PGR.core.strings.optionDesc_cropCardImages);
        makeModToggle(displayCardTagDescription, PGR.core.strings.options_displayCardTagDescription, PGR.core.strings.optionDesc_displayCardTagDescription);
        makeModToggle(removeLineBreaks, PGR.core.strings.options_removeLineBreaks, PGR.core.strings.optionDesc_removeLineBreaks);
        makeModToggle(showEstimatedDamage, PGR.core.strings.options_showEstimatedDamage, PGR.core.strings.optionDesc_showEstimatedDamage);
        makeModToggle(showFormulaDisplay, PGR.core.strings.options_showFormulaDisplay, PGR.core.strings.optionDesc_showFormulaDisplay);
        makeModToggle(showIrrelevantProperties, PGR.core.strings.options_hideIrrelevantAffinities, PGR.core.strings.optionDesc_hideIrrelevantAffinities);
        makeModToggle(fabricatePopup, PGR.core.strings.options_fabricatePopup, PGR.core.strings.optionDesc_fabricatePopup);

        EUIConfiguration.enableDescriptionIcons.addListener(val -> this.updateCardDescriptions());
        displayCardTagDescription.addListener(val -> this.updateCardDescriptions());
        abbreviateEffects.addListener(val -> this.updateCardDescriptions());
        removeLineBreaks.addListener(val -> this.updateCardDescriptions());
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
        ascensionGlyph2.addConfig(config);
        cropCardImages.addConfig(config);
        damageFormulaPosition.addConfig(config);
        displayCardTagDescription.addConfig(config);
        enableCustomCards.addConfig(config);
        enableCustomEvents.addConfig(config);
        enableCustomPotions.addConfig(config);
        enableCustomRelics.addConfig(config);
        fabricatePopup.addConfig(config);
        lowVRAM.addConfig(config);
        removeLineBreaks.addConfig(config);
        showEstimatedDamage.addConfig(config);
        showFormulaDisplay.addConfig(config);
        showIrrelevantProperties.addConfig(config);
        vanillaLibraryScreen.addConfig(config);
        lastCSVPath.addConfig(config);
        lastImagePath.addConfig(config);
        lastSeed.addConfig(config);
        madnessReplacements.addConfig(config);
        replaceCardsPCL.addConfig(config);
        tourCardAttribute.addConfig(config);
        tourCardPrimary.addConfig(config);
        tourCharSelect.addConfig(config);
        tourEditorEffect.addConfig(config);
        tourEditorForm.addConfig(config);
        tourEditorPower.addConfig(config);
        tourItemScreen.addConfig(config);
        tourLoadout.addConfig(config);
        tourRelicPrimary.addConfig(config);
        tourSeriesSelect.addConfig(config);
    }

    // Whenever this setting is updated, we need to force all cards everywhere to refresh their descriptions
    private void updateCardDescriptions() {
        EUIPreview.invalidate();
        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c instanceof PCLCard) {
                c.initializeDescription();
            }
        }

        if (GameUtilities.inGame()) {
            for (AbstractCard c : GameUtilities.getCardsInGame()) {
                if (c instanceof PCLCard) {
                    c.initializeDescription();
                }
            }
        }
    }
}