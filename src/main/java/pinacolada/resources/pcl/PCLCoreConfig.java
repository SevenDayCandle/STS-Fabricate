package pinacolada.resources.pcl;

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
import pinacolada.resources.AbstractConfig;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

public class PCLCoreConfig extends AbstractConfig
{
    public static final FilenameFilter JSON_FILTER = (dir, name) -> name.endsWith(".json");

    private static final String LAST_SEED_KEY = "TSDL";
    private static final String MOD_ID = "PCL";
    private static final String CONFIG_ID = "PCLConfig";
    private static final String ASCENSIONGLYPH0 = PCLCoreConfig.createFullID("AscensionGlyph0");
    private static final String ASCENSIONGLYPH1 = PCLCoreConfig.createFullID("AscensionGlyph1");
    private static final String ASCENSIONGLYPH2 = PCLCoreConfig.createFullID("AscensionGlyph2");
    private static final String CROP_CARD_PORTRAIT = PCLCoreConfig.createFullID("UseCroppedPortrait");
    private static final String DAMAGE_FORMULA_POSITION = PCLCoreConfig.createFullID("DamageFormulaPosition");
    private static final String DISPLAY_CARD_TAG_DESCRIPTION = PCLCoreConfig.createFullID("DisplayCardTagDescription");
    private static final String DISPLAY_CARD_TAG_TEXT = PCLCoreConfig.createFullID("DisplayCardTagText");
    private static final String ENABLE_EVENTS_FOR_OTHER_CHARACTERS = PCLCoreConfig.createFullID("EnableEventsForOtherCharacters");
    private static final String ENABLE_RELICS_FOR_OTHER_CHARACTERS = PCLCoreConfig.createFullID("EnableRelicsForOtherCharacters");
    private static final String HIDE_IRRELEVANT_AFFINITIES = PCLCoreConfig.createFullID("HideIrrelevantAffinities");
    private static final String HIDE_TIP_DESCRIPTION = PCLCoreConfig.createFullID("HideTipDescription");
    private static final String LAST_CSV_PATH = PCLCoreConfig.createFullID("LastCSVPath");
    private static final String LAST_IMAGE_PATH = PCLCoreConfig.createFullID("LastImagePath");
    private static final String REPLACE_CARDS_PCL = PCLCoreConfig.createFullID("ReplaceCards");
    private static final String SHOW_FORMULA_DISPLAY = PCLCoreConfig.createFullID("ShowFormulaDisplay");
    private static final String SIMPLE_MODE = PCLCoreConfig.createFullID("SimpleMode");
    private static final String SIMPLE_MODE_FTUE_SEEN = PCLCoreConfig.createFullID("SimpleModeFtueSeen");
    private static ModSettingsScreen.Category pclCategory;
    public STSConfigItem<Boolean> cropCardImages = new STSConfigItem<Boolean>(CROP_CARD_PORTRAIT, false);
    public STSConfigItem<Boolean> displayCardTagDescription = new STSConfigItem<Boolean>(DISPLAY_CARD_TAG_DESCRIPTION, false);
    public STSConfigItem<Boolean> enableEventsForOtherCharacters = new STSConfigItem<Boolean>(ENABLE_EVENTS_FOR_OTHER_CHARACTERS, false);
    public STSConfigItem<Boolean> enableRelicsForOtherCharacters = new STSConfigItem<Boolean>(ENABLE_RELICS_FOR_OTHER_CHARACTERS, false);
    public STSConfigItem<Boolean> hideIrrelevantAffinities = new STSConfigItem<Boolean>(HIDE_IRRELEVANT_AFFINITIES, true);
    public STSConfigItem<Boolean> replaceCardsPCL = new STSConfigItem<Boolean>(REPLACE_CARDS_PCL, false);
    public STSConfigItem<Boolean> showFormulaDisplay = new STSConfigItem<Boolean>(SHOW_FORMULA_DISPLAY, false);
    public STSConfigItem<Boolean> simpleMode = new STSConfigItem<Boolean>(SIMPLE_MODE, true);
    public STSConfigItem<Boolean> simpleModeFtueSeen = new STSConfigItem<Boolean>(SIMPLE_MODE_FTUE_SEEN, false);
    public STSConfigItem<Integer> ascensionGlyph0 = new STSConfigItem<Integer>(ASCENSIONGLYPH0, 0);
    public STSConfigItem<Integer> ascensionGlyph1 = new STSConfigItem<Integer>(ASCENSIONGLYPH1, 0);
    public STSConfigItem<Integer> ascensionGlyph2 = new STSConfigItem<Integer>(ASCENSIONGLYPH2, 0);
    public STSStringConfigItem lastCSVPath = new STSStringConfigItem(LAST_CSV_PATH, "");
    public STSStringConfigItem lastImagePath = new STSStringConfigItem(LAST_IMAGE_PATH, "");
    public STSStringConfigItem lastSeed = new STSStringConfigItem(LAST_SEED_KEY, "");

    public STSSerializedConfigItem<Vector2> damageFormulaPosition = new STSSerializedConfigItem<Vector2>(DAMAGE_FORMULA_POSITION, new Vector2(0.6f, 0.8f));
    private HashSet<String> tips = null;

    public static String createFullID(String name)
    {
        return PGR.BASE_PREFIX.toUpperCase(Locale.ROOT) + "-" + name;
    }

    public PCLCoreConfig()
    {
        super(MOD_ID);
    }

    public boolean hideTipDescription(String id)
    {
        if (tips == null)
        {
            tips = new HashSet<>();

            if (config.has(HIDE_TIP_DESCRIPTION))
            {
                Collections.addAll(tips, config.getString(HIDE_TIP_DESCRIPTION).split("\\|"));
            }
        }

        return tips.contains(id);
    }

    public void hideTipDescription(String id, boolean value, boolean flush)
    {
        if (tips == null)
        {
            tips = new HashSet<>();
        }

        if (value)
        {
            if (id != null)
            {
                tips.add(id);
            }
        }
        else
        {
            tips.remove(id);
        }

        config.setString(HIDE_TIP_DESCRIPTION, EUIUtils.joinStrings("|", tips));

        if (flush)
        {
            save();
        }
    }

    public void initializeOptions()
    {
        final ModPanel panel = new ModPanel();

        int yPos = BASE_OPTION_OFFSET_Y;

        yPos = addToggle(panel, cropCardImages, PGR.core.strings.options_cropCardImages, yPos);
        yPos = addToggle(panel, displayCardTagDescription, PGR.core.strings.options_displayCardTagDescription, yPos);
        yPos = addToggle(panel, enableEventsForOtherCharacters, PGR.core.strings.options_enableEventsForOtherCharacters, yPos);
        yPos = addToggle(panel, enableRelicsForOtherCharacters, PGR.core.strings.options_enableRelicsForOtherCharacters, yPos);
        yPos = addToggle(panel, showFormulaDisplay, PGR.core.strings.options_showFormulaDisplay, yPos);
        yPos = addToggle(panel, hideIrrelevantAffinities, PGR.core.strings.options_hideIrrelevantAffinities, yPos);
        yPos = addToggle(panel, replaceCardsPCL, PGR.core.strings.options_replaceCards, yPos);

        BaseMod.registerModBadge(ImageMaster.loadImage("images/pcl/modBadge.png"), MOD_ID, "PinaColada", "", panel);

        addModToggle(cropCardImages, PGR.core.strings.options_cropCardImages);
        addModToggle(displayCardTagDescription, PGR.core.strings.options_displayCardTagDescription);
        addModToggle(showFormulaDisplay,  PGR.core.strings.options_showFormulaDisplay);
        addModToggle(hideIrrelevantAffinities,  PGR.core.strings.options_hideIrrelevantAffinities);

        EUIConfiguration.disableDescrptionIcons.addListener(val -> this.updateCardDescriptions());
        displayCardTagDescription.addListener(val -> this.updateCardDescriptions());
    }


    public void loadImpl()
    {
        ascensionGlyph0.addConfig(config);
        ascensionGlyph1.addConfig(config);
        ascensionGlyph2.addConfig(config);
        cropCardImages.addConfig(config);
        damageFormulaPosition.addConfig(config);
        displayCardTagDescription.addConfig(config);
        enableEventsForOtherCharacters.addConfig(config);
        enableRelicsForOtherCharacters.addConfig(config);
        showFormulaDisplay.addConfig(config);
        hideIrrelevantAffinities.addConfig(config);
        lastCSVPath.addConfig(config);
        lastImagePath.addConfig(config);
        lastSeed.addConfig(config);
        replaceCardsPCL.addConfig(config);
        simpleMode.addConfig(config);
        simpleModeFtueSeen.addConfig(config);
    }

    // Whenever this setting is updated, we need to force all cards everywhere to refresh their descriptions
    private void updateCardDescriptions()
    {
        PCLCardPreviews.invalidate();
        for (AbstractCard c : CardLibrary.getAllCards())
        {
            if (c instanceof PCLCard)
            {
                c.initializeDescription();
            }
        }

        if (GameUtilities.inGame())
        {
            for (AbstractCard c : GameUtilities.getCardsInGame())
            {
                if (c instanceof PCLCard)
                {
                    c.initializeDescription();
                }
            }
        }
    }
}