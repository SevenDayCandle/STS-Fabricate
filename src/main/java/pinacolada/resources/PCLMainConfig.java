package pinacolada.resources;

import basemod.BaseMod;
import basemod.ModPanel;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.configuration.STSConfigItem;
import extendedui.configuration.STSSerializedConfigItem;
import extendedui.configuration.STSStringConfigItem;
import extendedui.ui.settings.ModSettingsScreen;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardPreviews;
import pinacolada.utilities.GameUtilities;

import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

public class PCLMainConfig extends AbstractConfig {
    public static final FilenameFilter JSON_FILTER = (dir, name) -> name.endsWith(".json");
    private static final String LAST_SEED_KEY = "TSDL";
    private static final String MOD_ID = "PCL";
    private static final String CONFIG_ID = "PCLConfig";
    private static final String ASCENSIONGLYPH0 = PCLMainConfig.createFullID("AscensionGlyph0");
    private static final String ASCENSIONGLYPH1 = PCLMainConfig.createFullID("AscensionGlyph1");
    private static final String ASCENSIONGLYPH2 = PCLMainConfig.createFullID("AscensionGlyph2");
    private static final String CROP_CARD_PORTRAIT = PCLMainConfig.createFullID("UseCroppedPortrait");
    private static final String DAMAGE_FORMULA_POSITION = PCLMainConfig.createFullID("DamageFormulaPosition");
    private static final String DISPLAY_CARD_TAG_DESCRIPTION = PCLMainConfig.createFullID("DisplayCardTagDescription");
    private static final String DISPLAY_CARD_TAG_TEXT = PCLMainConfig.createFullID("DisplayCardTagText");
    private static final String EDITOR_FTUE_SCREEN = PCLMainConfig.createFullID("EditorFtueScreen");
    private static final String ENABLE_CUSTOM_CARDS = PCLMainConfig.createFullID("EnableCustomCards");
    private static final String ENABLE_CUSTOM_EVENTS = PCLMainConfig.createFullID("EnableCustomEvents");
    private static final String ENABLE_CUSTOM_POTIONS = PCLMainConfig.createFullID("EnableCustomPotions");
    private static final String ENABLE_CUSTOM_RELICS = PCLMainConfig.createFullID("EnableCustomRelics");
    private static final String EXPAND_ABBREVIATED_EFFECTS = PCLMainConfig.createFullID("ExpandAbbreviatedEffects");
    private static final String HIDE_TIP_DESCRIPTION = PCLMainConfig.createFullID("HideTipDescription");
    private static final String LAST_CSV_PATH = PCLMainConfig.createFullID("LastCSVPath");
    private static final String LAST_IMAGE_PATH = PCLMainConfig.createFullID("LastImagePath");
    private static final String MADNESS_REPLACEMENTS = PCLMainConfig.createFullID("MadnessReplacements");
    private static final String REMOVE_LINE_BREAKS = PCLMainConfig.createFullID("RemoveLineBreaks");
    private static final String REPLACE_CARDS_PCL = PCLMainConfig.createFullID("ReplaceCardsPCL");
    private static final String SHOW_ESTIMATED_DAMAGE = PCLMainConfig.createFullID("ShowEstimatedDamage");
    private static final String SHOW_FORMULA_DISPLAY = PCLMainConfig.createFullID("ShowFormulaDisplay");
    private static final String SHOW_IRRELEVANT_PROPERTIES = PCLMainConfig.createFullID("ShowIrrelevantProperties");
    private static final String VANILLA_LIBRARY_SCREEN = PCLMainConfig.createFullID("VanillaLibraryScreen");
    private static final String VANILLA_POWER_RENDER = PCLMainConfig.createFullID("VanillaPowerRender");
    private static ModSettingsScreen.Category pclCategory;

    public STSConfigItem<Boolean> cropCardImages = new STSConfigItem<Boolean>(CROP_CARD_PORTRAIT, false);
    public STSConfigItem<Boolean> displayCardTagDescription = new STSConfigItem<Boolean>(DISPLAY_CARD_TAG_DESCRIPTION, false);
    public STSConfigItem<Boolean> enableCustomCards = new STSConfigItem<Boolean>(ENABLE_CUSTOM_CARDS, false);
    public STSConfigItem<Boolean> enableCustomEvents = new STSConfigItem<Boolean>(ENABLE_CUSTOM_EVENTS, false);
    public STSConfigItem<Boolean> enableCustomPotions = new STSConfigItem<Boolean>(ENABLE_CUSTOM_POTIONS, false);
    public STSConfigItem<Boolean> enableCustomRelics = new STSConfigItem<Boolean>(ENABLE_CUSTOM_RELICS, false);
    public STSConfigItem<Boolean> expandAbbreviatedEffects = new STSConfigItem<Boolean>(EXPAND_ABBREVIATED_EFFECTS, false);
    public STSConfigItem<Boolean> madnessReplacements = new STSConfigItem<Boolean>(MADNESS_REPLACEMENTS, false);
    public STSConfigItem<Boolean> removeLineBreaks = new STSConfigItem<Boolean>(REMOVE_LINE_BREAKS, false);
    public STSConfigItem<Boolean> replaceCardsPCL = new STSConfigItem<Boolean>(REPLACE_CARDS_PCL, false);
    public STSConfigItem<Boolean> showEstimatedDamage = new STSConfigItem<Boolean>(SHOW_ESTIMATED_DAMAGE, false);
    public STSConfigItem<Boolean> showFormulaDisplay = new STSConfigItem<Boolean>(SHOW_FORMULA_DISPLAY, false);
    public STSConfigItem<Boolean> showIrrelevantProperties = new STSConfigItem<Boolean>(SHOW_IRRELEVANT_PROPERTIES, false);
    public STSConfigItem<Boolean> vanillaLibraryScreen = new STSConfigItem<Boolean>(VANILLA_LIBRARY_SCREEN, false);
    public STSConfigItem<Boolean> vanillaPowerRender = new STSConfigItem<Boolean>(VANILLA_POWER_RENDER, false);
    public STSConfigItem<Boolean> editorFtueScreen = new STSConfigItem<Boolean>(EDITOR_FTUE_SCREEN, false);
    public STSConfigItem<Integer> ascensionGlyph0 = new STSConfigItem<Integer>(ASCENSIONGLYPH0, 0);
    public STSConfigItem<Integer> ascensionGlyph1 = new STSConfigItem<Integer>(ASCENSIONGLYPH1, 0);
    public STSConfigItem<Integer> ascensionGlyph2 = new STSConfigItem<Integer>(ASCENSIONGLYPH2, 0);
    public STSStringConfigItem lastCSVPath = new STSStringConfigItem(LAST_CSV_PATH, "");
    public STSStringConfigItem lastImagePath = new STSStringConfigItem(LAST_IMAGE_PATH, "");
    public STSStringConfigItem lastSeed = new STSStringConfigItem(LAST_SEED_KEY, "");

    public STSSerializedConfigItem<Vector2> damageFormulaPosition = new STSSerializedConfigItem<Vector2>(DAMAGE_FORMULA_POSITION, new Vector2(0.6f, 0.8f));
    private HashSet<String> tips = null;

    public PCLMainConfig() {
        super(MOD_ID);
    }

    public static String createFullID(String name) {
        return PGR.BASE_PREFIX.toUpperCase(Locale.ROOT) + "-" + name;
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
        final ModPanel panel = new ModPanel();

        int yPos = BASE_OPTION_OFFSET_Y;

        yPos = addToggle(panel, cropCardImages, PGR.core.strings.options_cropCardImages, yPos, PGR.core.strings.optionDesc_cropCardImages);
        yPos = addToggle(panel, displayCardTagDescription, PGR.core.strings.options_displayCardTagDescription, yPos, PGR.core.strings.optionDesc_displayCardTagDescription);
        yPos = addToggle(panel, expandAbbreviatedEffects, PGR.core.strings.options_expandAbbreviatedEffects, yPos, PGR.core.strings.optionDesc_expandAbbreviatedEffects);
        yPos = addToggle(panel, removeLineBreaks, PGR.core.strings.options_removeLineBreaks, yPos, PGR.core.strings.optionDesc_removeLineBreaks);
        yPos = addToggle(panel, vanillaLibraryScreen, PGR.core.strings.options_vanillaCustomRunMenu, yPos, PGR.core.strings.optionDesc_vanillaCustomRunMenu);
        yPos = addToggle(panel, vanillaPowerRender, PGR.core.strings.options_vanillaPowerRender, yPos, PGR.core.strings.optionDesc_vanillaPowerRender);
        yPos = addToggle(panel, showEstimatedDamage, PGR.core.strings.options_showEstimatedDamage, yPos, PGR.core.strings.optionDesc_showEstimatedDamage);
        yPos = addToggle(panel, showFormulaDisplay, PGR.core.strings.options_showFormulaDisplay, yPos, PGR.core.strings.optionDesc_showFormulaDisplay);
        yPos = addToggle(panel, showIrrelevantProperties, PGR.core.strings.options_hideIrrelevantAffinities, yPos, PGR.core.strings.optionDesc_hideIrrelevantAffinities);
        yPos = addToggle(panel, madnessReplacements, PGR.core.strings.options_madnessReplacements, yPos, PGR.core.strings.optionDesc_madnessReplacements);
        yPos = addToggle(panel, enableCustomCards, PGR.core.strings.options_enableCustomCards, yPos, PGR.core.strings.optionDesc_onlyNewRuns);

        BaseMod.registerModBadge(ImageMaster.loadImage("images/pcl/modBadge.png"), MOD_ID, "PinaColada", "", panel);

        makeModToggle(cropCardImages, PGR.core.strings.options_cropCardImages, PGR.core.strings.optionDesc_cropCardImages);
        makeModToggle(displayCardTagDescription, PGR.core.strings.options_displayCardTagDescription, PGR.core.strings.optionDesc_displayCardTagDescription);
        makeModToggle(expandAbbreviatedEffects, PGR.core.strings.options_expandAbbreviatedEffects, PGR.core.strings.optionDesc_expandAbbreviatedEffects);
        makeModToggle(removeLineBreaks, PGR.core.strings.options_removeLineBreaks, PGR.core.strings.optionDesc_removeLineBreaks);
        makeModToggle(showEstimatedDamage, PGR.core.strings.options_showEstimatedDamage, PGR.core.strings.optionDesc_showEstimatedDamage);
        makeModToggle(showFormulaDisplay, PGR.core.strings.options_showFormulaDisplay, PGR.core.strings.optionDesc_showFormulaDisplay);
        makeModToggle(showIrrelevantProperties, PGR.core.strings.options_hideIrrelevantAffinities, PGR.core.strings.optionDesc_hideIrrelevantAffinities);

        EUIConfiguration.disableDescrptionIcons.addListener(val -> this.updateCardDescriptions());
        displayCardTagDescription.addListener(val -> this.updateCardDescriptions());
        expandAbbreviatedEffects.addListener(val -> this.updateCardDescriptions());
        removeLineBreaks.addListener(val -> this.updateCardDescriptions());
    }

    public void loadImpl() {
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
        expandAbbreviatedEffects.addConfig(config);
        removeLineBreaks.addConfig(config);
        showEstimatedDamage.addConfig(config);
        showFormulaDisplay.addConfig(config);
        showIrrelevantProperties.addConfig(config);
        vanillaLibraryScreen.addConfig(config);
        vanillaPowerRender.addConfig(config);
        lastCSVPath.addConfig(config);
        lastImagePath.addConfig(config);
        lastSeed.addConfig(config);
        madnessReplacements.addConfig(config);
        replaceCardsPCL.addConfig(config);
        editorFtueScreen.addConfig(config);
    }

    // Whenever this setting is updated, we need to force all cards everywhere to refresh their descriptions
    private void updateCardDescriptions() {
        PCLCardPreviews.invalidate();
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