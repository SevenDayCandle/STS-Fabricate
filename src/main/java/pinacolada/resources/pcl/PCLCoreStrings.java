package pinacolada.resources.pcl;

import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.text.EUISmartText;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PCLStrings;
import pinacolada.resources.PGR;

import java.util.List;
import java.util.StringJoiner;

public class PCLCoreStrings extends PCLStrings {
    private final UIStrings actions = getUIStrings("Actions");
    private final UIStrings cedit = getUIStrings("CardEditor");
    private final UIStrings cetut = getUIStrings("CardEditorTutorial");
    private final UIStrings cardPile = getUIStrings("CardPile");
    private final UIStrings cardtype = getUIStrings("CardType");
    private final UIStrings charselect = getUIStrings("CharacterSelect");
    private final UIStrings combat = getUIStrings("Combat");
    private final UIStrings conditions = getUIStrings("Conditions");
    private final UIStrings gridselection = getUIStrings("GridSelection");
    private final UIStrings hotkeys = getUIStrings("Hotkeys");
    private final UIStrings loadout = getUIStrings("Loadout");
    private final UIStrings menu = getUIStrings("Menu");
    private final UIStrings misc = getUIStrings("Misc");
    private final UIStrings options = getUIStrings("Options");
    private final UIStrings optionDesc = getUIStrings("OptionDescriptions");
    private final UIStrings rewards = getUIStrings("Rewards");
    private final UIStrings seriesui = getUIStrings("SeriesUI");
    private final UIStrings scp = getUIStrings("SingleCardPopupButtons");
    private final UIStrings subjects = getUIStrings("Subjects");
    private final UIStrings tutorial = getUIStrings("Tutorial");
    public final String grid_discard = DiscardAction.TEXT[0];
    public final String grid_exhaust = ExhaustAction.TEXT[0];
    public final String grid_cycle = GamblingChipAction.TEXT[1];
    public final String grid_chooseOneCard = CardRewardScreen.TEXT[1];
    // Card Editor
    public final String cedit_attributes = cedit.TEXT[0];
    public final String cedit_effects = cedit.TEXT[1];
    public final String cedit_tags = cedit.TEXT[2];
    public final String cedit_value = cedit.TEXT[3];
    public final String cedit_newCard = cedit.TEXT[4];
    public final String cedit_damage = cedit.TEXT[5];
    public final String cedit_block = cedit.TEXT[6];
    public final String cedit_magicNumber = cedit.TEXT[7];
    public final String cedit_secondaryNumber = cedit.TEXT[8];
    public final String cedit_hitCount = cedit.TEXT[9];
    public final String cedit_upgrades = cedit.TEXT[10];
    public final String cedit_cardTarget = cedit.TEXT[11];
    public final String cedit_attackType = cedit.TEXT[12];
    public final String cedit_attackEffect = cedit.TEXT[13];
    public final String cedit_condition = cedit.TEXT[14];
    public final String cedit_primary = cedit.TEXT[15];
    public final String cedit_effect = cedit.TEXT[16];
    public final String cedit_effectX = cedit.TEXT[17];
    public final String cedit_powerX = cedit.TEXT[18];
    public final String cedit_modifier = cedit.TEXT[19];
    public final String cedit_trigger = cedit.TEXT[20];
    public final String cedit_multiEffect = cedit.TEXT[21];
    public final String cedit_multiCondition = cedit.TEXT[22];
    public final String cedit_branchCondition = cedit.TEXT[23];
    public final String cedit_powers = cedit.TEXT[24];
    public final String cedit_maxUpgrades = cedit.TEXT[25];
    public final String cedit_flags = cedit.TEXT[26];
    public final String cedit_editForm = cedit.TEXT[27];
    public final String cedit_turnDelay = cedit.TEXT[28];
    public final String cedit_extraValue = cedit.TEXT[29];
    public final String cedit_maxCopies = cedit.TEXT[30];
    public final String cedit_loadImage = cedit.TEXT[31];
    public final String cedit_loadFile = cedit.TEXT[32];
    public final String cedit_paste = cedit.TEXT[33];
    public final String cedit_addForm = cedit.TEXT[34];
    public final String cedit_removeForm = cedit.TEXT[35];
    public final String cedit_undo = cedit.TEXT[36];
    public final String cedit_custom = cedit.TEXT[37];
    public final String cedit_branchUpgrade = cedit.TEXT[38];
    public final String cedit_duplicate = cedit.TEXT[39];
    public final String cedit_delete = cedit.TEXT[40];
    public final String cedit_reloadCards = cedit.TEXT[41];
    public final String cedit_confirmDeletion = cedit.TEXT[42];
    public final String cedit_confirmDeletionDesc = cedit.TEXT[43];
    public final String cedit_customCards = cedit.TEXT[44];
    public final String cedit_addToEffect = cedit.TEXT[45];
    public final String cedit_primaryInfo = cedit.TEXT[46];
    public final String cedit_primaryInfoDesc = cedit.TEXT[47];
    public final String cedit_choices = cedit.TEXT[48];
    public final String cedit_not = cedit.TEXT[49];
    public final String cedit_or = cedit.TEXT[50];
    public final String cedit_idSuffix = cedit.TEXT[51];
    public final String cedit_idSuffixWarning = cedit.TEXT[52];
    public final String cedit_duplicateToColor = cedit.TEXT[53];
    public final String cedit_duplicateToColorDesc = cedit.TEXT[54];
    public final String cedit_useParent = cedit.TEXT[55];
    public final String cedit_loadFromCard = cedit.TEXT[56];
    public final String cedit_openFolder = cedit.TEXT[57];
    public final String cedit_exportCSV = cedit.TEXT[58];
    public final String cedit_random = cedit.TEXT[59];
    public final String cedit_required = cedit.TEXT[60];
    public final String cedit_origins = cedit.TEXT[61];
    public final String cedit_enable = cedit.TEXT[62];
    public final String cedit_pile = cedit.TEXT[63];
    public final String cedit_combat = cedit.TEXT[64];
    public final String cedit_exact = cedit.TEXT[65];
    public final String cedit_landingSound = cedit.TEXT[66];
    // Card Editor Tutorial
    public final String cetut_selector1 = cetut.TEXT[0];
    public final String cetut_selector2 = cetut.TEXT[1];
    public final String cetut_selectorReload = cetut.TEXT[2];
    public final String cetut_primaryForm = cetut.TEXT[3];
    public final String cetut_primaryImage = cetut.TEXT[4];
    public final String cetut_primaryFlags = cetut.TEXT[5];
    public final String cetut_attrTags1 = cetut.TEXT[6];
    public final String cetut_attrTags2 = cetut.TEXT[7];
    public final String cetut_attrAffinity = cetut.TEXT[8];
    public final String cetut_effectCondition = cetut.TEXT[9];
    public final String cetut_effectEffect = cetut.TEXT[10];
    public final String cetut_effectModifier = cetut.TEXT[11];
    public final String cetut_effectPrimary = cetut.TEXT[12];
    public final String cetut_effectTurnDelay = cetut.TEXT[13];
    public final String cetut_effectChoices = cetut.TEXT[14];
    public final String cetut_effectMultiCondition = cetut.TEXT[15];
    public final String cetut_effectBranchCondition = cetut.TEXT[16];
    public final String cetut_imageSelect = cetut.TEXT[17];
    public final String cetut_imageCrop = cetut.TEXT[18];
    public final String cetut_maxUpgrades = cetut.TEXT[19];
    public final String cetut_maxCopies = cetut.TEXT[20];
    public final String cetut_idSuffix = cetut.TEXT[21];
    public final String cetut_nameLanguage = cetut.TEXT[22];
    public final String cetut_rarity = cetut.TEXT[23];
    public final String cetut_type = cetut.TEXT[24];
    public final String cetut_cardTarget = cetut.TEXT[25];
    public final String cetut_amount = cetut.TEXT[26];
    public final String cetut_attackType = cetut.TEXT[27];
    public final String cetut_attackEffect = cetut.TEXT[28];
    public final String cetut_hitCount = cetut.TEXT[29];
    public final String cetut_blockCount = cetut.TEXT[30];
    public final String cetut_loadFromCardScreen = cetut.TEXT[31];
    public final String cetut_branchUpgrade = cetut.TEXT[32];
    public final String cetut_useParent = cetut.TEXT[33];
    public final String cetut_required1 = cetut.TEXT[34];
    public final String cetut_required2 = cetut.TEXT[35];
    public final String cetut_passive = cetut.TEXT[36];
    public final String cetut_when = cetut.TEXT[37];
    public final String cetut_bonus = cetut.TEXT[38];
    public final String cetut_nodeTutorial = cetut.TEXT[39];
    public final String cetut_blankPrimary = cetut.TEXT[40];
    public final String cetut_blankProxy = cetut.TEXT[41];
    public final String cetut_relicRarity = cetut.TEXT[42];
    public final String cetut_landingSound = cetut.TEXT[43];
    public final String cetut_addToEffect = cetut.TEXT[44];
    public final String cetut_primaryWarning = cetut.TEXT[45];
    public final String cetut_corruptedWarning = cetut.TEXT[46];
    public final String cetut_undo = cetut.TEXT[47];
    public final String cetut_openFolder = cetut.TEXT[48];
    public final String cetut_topBarTutorial = cetut.TEXT[49];
    // Card Pile
    public final String cpile_hand = cardPile.TEXT[0];
    public final String cpile_deck = cardPile.TEXT[1];
    public final String cpile_pile = cardPile.TEXT[2];
    public final String cpile_manual = cardPile.TEXT[3];
    public final String cpile_top = cardPile.TEXT[4];
    public final String cpile_bottom = cardPile.TEXT[5];
    public final String cpile_random = cardPile.TEXT[6];
    // Card Type
    public final String ctype_none = cardtype.TEXT[0];
    public final String ctype_allAlly = cardtype.TEXT[1];
    public final String ctype_allCharacter = cardtype.TEXT[2];
    public final String ctype_allEnemy = cardtype.TEXT[3];
    public final String ctype_any = cardtype.TEXT[4];
    public final String ctype_randomAlly = cardtype.TEXT[5];
    public final String ctype_randomEnemy = cardtype.TEXT[6];
    public final String ctype_self = cardtype.TEXT[7];
    public final String ctype_singleAlly = cardtype.TEXT[8];
    public final String ctype_singleTarget = cardtype.TEXT[9];
    public final String ctype_team = cardtype.TEXT[10];
    public final String ctype_legendary = cardtype.TEXT[11];
    public final String ctype_secretRare = cardtype.TEXT[12];
    public final String ctype_general = cardtype.TEXT[13];
    public final String ctype_tagAll = cardtype.TEXT[14];
    public final String ctype_tagAoE = cardtype.TEXT[15];
    public final String ctype_tagRandom = cardtype.TEXT[16];
    public final String ctype_turnStartFirst = cardtype.TEXT[17];
    public final String ctype_turnStartLast = cardtype.TEXT[18];
    public final String ctype_turnEndFirst = cardtype.TEXT[19];
    public final String ctype_turnEndLast = cardtype.TEXT[20];
    // Character
    public final String csel_leftText = charselect.TEXT[0];  // Starting Cards:
    public final String csel_deckEditor = charselect.TEXT[1];
    public final String csel_deckEditorInfo = charselect.TEXT[2];
    public final String csel_seriesEditor = charselect.TEXT[3];
    public final String csel_seriesEditorInfo = charselect.TEXT[4];
    public final String csel_resetTutorial = charselect.TEXT[5];
    public final String csel_resetTutorialInfo = charselect.TEXT[6];
    public final String csel_resetTutorialConfirm = charselect.TEXT[7];
    public final String csel_charTutorial = charselect.TEXT[8];
    public final String csel_charTutorialInfo = charselect.TEXT[9];
    public final String csel_ascensionGlyph = charselect.TEXT[10];
    // Combat
    public final String combat_current = combat.TEXT[0];
    public final String combat_next = combat.TEXT[1];
    public final String combat_uses = combat.TEXT[2];
    public final String combat_rerolls = combat.TEXT[3];
    public final String combat_controlPile = combat.TEXT[4];
    public final String combat_controlPileDescription = combat.TEXT[5];
    public final String combat_count = combat.TEXT[6];
    public final String combat_effect = combat.TEXT[7];
    public final String combat_nextLevelEffect = combat.TEXT[8];
    public final String combat_active = combat.TEXT[9];
    public final String combat_inactive = combat.TEXT[10];
    public final String combat_disabled = combat.TEXT[11];
    public final String combat_dodged = combat.TEXT[12];
    public final String combat_afterlifeMet = combat.TEXT[13];
    public final String combat_afterlifeRequirement = combat.TEXT[14];
    public final String combat_rightClickRetarget = combat.TEXT[15];
    // Grid
    public final String grid_chooseCards = gridselection.TEXT[0];
    public final String grid_cardsInPile = gridselection.TEXT[1];
    public final String grid_scry = gridselection.TEXT[2];
    // Hotkeys
    public final String hotkeys_controlPileChange = hotkeys.TEXT[0];
    public final String hotkeys_controlPileSelect = hotkeys.TEXT[1];
    public final String hotkeys_rerollCurrent = hotkeys.TEXT[2];
    public final String hotkeys_toggleFormulaDisplay = hotkeys.TEXT[3];
    public final String hotkeys_viewAugments = hotkeys.TEXT[4];
    // Loadout
    public final String loadout_deckHeader = loadout.TEXT[0];
    public final String loadout_relicHeader = loadout.TEXT[1];
    public final String loadout_attributesHeader = loadout.TEXT[2];
    public final String loadout_add = loadout.TEXT[3];
    public final String loadout_decrease = loadout.TEXT[4];
    public final String loadout_remove = loadout.TEXT[5];
    public final String loadout_change = loadout.TEXT[6];
    public final String loadout_maxHP = loadout.TEXT[7];
    public final String loadout_gold = loadout.TEXT[8];
    public final String loadout_potionSlot = loadout.TEXT[9];
    public final String loadout_orbSlot = loadout.TEXT[10];
    public final String loadout_cardDraw = loadout.TEXT[11];
    public final String loadout_cardDrawDesc = loadout.TEXT[12];
    public final String loadout_energy = loadout.TEXT[13];
    public final String loadout_energyDesc = loadout.TEXT[14];
    public final String loadout_hindranceValue = loadout.TEXT[15];
    public final String loadout_hindranceDescription = loadout.TEXT[16];
    public final String loadout_cardsCount = loadout.TEXT[17];
    public final String loadout_totalValue = loadout.TEXT[18];
    public final String loadout_invalidLoadout = loadout.TEXT[19];
    public final String loadout_invalidLoadoutDescLimit = loadout.TEXT[20];
    public final String loadout_invalidLoadoutDescSeen = loadout.TEXT[21];
    public final String loadout_invalidLoadoutDescNotEnough = loadout.TEXT[22];
    public final String loadout_invalidLoadoutDescLocked = loadout.TEXT[23];
    public final String loadout_unsavedChanges = loadout.TEXT[24];
    public final String loadout_reset = loadout.TEXT[25];
    public final String loadout_copyTo = loadout.TEXT[26];
    public final String loadout_copyFrom = loadout.TEXT[27];
    public final String loadout_export = loadout.TEXT[28];
    public final String loadout_tutorialCard = loadout.TEXT[29];
    public final String loadout_tutorialValue = loadout.TEXT[30];
    public final String loadout_tutorialRelic = loadout.TEXT[31];
    public final String loadout_tutorialAttributes = loadout.TEXT[32];
    public final String loadout_tutorialRequired = loadout.TEXT[33];
    // Menu
    public final String menu_editor = menu.TEXT[0];
    public final String menu_card = menu.TEXT[1];
    public final String menu_cardDesc = menu.TEXT[2];
    public final String menu_comingsoon = menu.TEXT[3];
    public final String menu_relic = menu.TEXT[4];
    public final String menu_relicDesc = menu.TEXT[5];
    // Misc
    public final String misc_viewAugments = misc.TEXT[0];
    public final String misc_viewAugmentsDescription = misc.TEXT[1];
    public final String misc_viewAugmentsNone = misc.TEXT[2];
    public final String misc_customCards = misc.TEXT[3];
    public final String misc_customCardsDesc = misc.TEXT[4];
    public final String misc_fabricate = misc.TEXT[5];
    public final String misc_leftClick = misc.TEXT[6];
    public final String misc_rightClick = misc.TEXT[7];
    public final String misc_tempPowerPrefix = misc.TEXT[8];
    public final String misc_requirement = misc.TEXT[9];
    public final String misc_edit = misc.TEXT[10];
    public final String misc_customRelics = misc.TEXT[11];
    public final String misc_customRelicsDesc = misc.TEXT[12];
    // Options
    public final String options_cropCardImages = options.TEXT[0];
    public final String options_displayCardTagDescription = options.TEXT[1];
    public final String options_expandAbbreviatedEffects = options.TEXT[2];
    public final String options_removeLineBreaks = options.TEXT[3];
    public final String options_vanillaCustomRunMenu = options.TEXT[4];
    public final String options_showEstimatedDamage = options.TEXT[5];
    public final String options_showFormulaDisplay = options.TEXT[6];
    public final String options_hideIrrelevantAffinities = options.TEXT[7];
    public final String options_enableCustomCards = options.TEXT[8];
    public final String options_enableCustomRelics = options.TEXT[9];
    public final String options_enableCustomPotions = options.TEXT[10];
    public final String options_enableCustomEvents = options.TEXT[11];
    public final String options_madnessReplacements = options.TEXT[12];
    public final String options_vanillaPowerRender = options.TEXT[13];
    public final String options_alwaysPCLCard = options.TEXT[14];
    // Option Descriptions
    public final String optionDesc_cropCardImages = optionDesc.TEXT[0];
    public final String optionDesc_displayCardTagDescription = optionDesc.TEXT[1];
    public final String optionDesc_expandAbbreviatedEffects = optionDesc.TEXT[2];
    public final String optionDesc_removeLineBreaks = optionDesc.TEXT[3];
    public final String optionDesc_vanillaCustomRunMenu = optionDesc.TEXT[4];
    public final String optionDesc_showEstimatedDamage = optionDesc.TEXT[5];
    public final String optionDesc_showFormulaDisplay = optionDesc.TEXT[6];
    public final String optionDesc_hideIrrelevantAffinities = optionDesc.TEXT[7];
    public final String optionDesc_onlyNewRuns = optionDesc.TEXT[8];
    public final String optionDesc_madnessReplacements = optionDesc.TEXT[9];
    public final String optionDesc_vanillaPowerRender = optionDesc.TEXT[10];
    public final String optionDesc_alwaysPCLCard = optionDesc.TEXT[11];
    // Reward
    public final String rewards_rewardBreak = rewards.TEXT[0];
    public final String rewards_breakDescription = rewards.TEXT[1];
    public final String rewards_reroll = rewards.TEXT[2];
    public final String rewards_rerollDescription = rewards.TEXT[3];
    public final String rewards_maxhpbonusF1 = rewards.TEXT[4];
    public final String rewards_goldbonusF1 = rewards.TEXT[5];
    public final String rewards_commonUpgrade = rewards.TEXT[6];
    public final String rewards_rightClickPreview = rewards.TEXT[7];
    public final String rewards_potionSlot = rewards.TEXT[8];
    public final String rewards_orbSlot = rewards.TEXT[9];
    // Series
    public final String sui_seriesUI = seriesui.TEXT[0];
    public final String sui_affinities = seriesui.TEXT[1];
    public final String sui_core = seriesui.TEXT[2];
    public final String sui_selected = seriesui.TEXT[3];
    public final String sui_unlocked = seriesui.TEXT[4];
    public final String sui_removeFromPool = seriesui.TEXT[5];
    public final String sui_addToPool = seriesui.TEXT[6];
    public final String sui_viewPool = seriesui.TEXT[7];
    public final String sui_totalCards = seriesui.TEXT[8];
    public final String sui_instructions1 = seriesui.TEXT[9];
    public final String sui_instructions2 = seriesui.TEXT[10];
    public final String sui_viewPoolInstructions = seriesui.TEXT[11];
    public final String sui_characterCards = seriesui.TEXT[12];
    public final String sui_selectAll = seriesui.TEXT[13];
    public final String sui_selectAllDesc = seriesui.TEXT[14];
    public final String sui_deselectAll = seriesui.TEXT[15];
    public final String sui_deselectDesc = seriesui.TEXT[16];
    public final String sui_selectRandom = seriesui.TEXT[17];
    public final String sui_showCardPool = seriesui.TEXT[18];
    public final String sui_coreInstructions = seriesui.TEXT[19];
    public final String sui_totalInstructions = seriesui.TEXT[20];
    // Single View Card Popup
    public final String scp_variant = scp.TEXT[0];
    public final String scp_changeVariant = scp.TEXT[1];
    public final String scp_changeVariantTooltipPermanent = scp.TEXT[2];
    public final String scp_changeVariantTooltipAlways = scp.TEXT[3];
    public final String scp_currentCopies = scp.TEXT[4];
    public final String scp_maxCopies = scp.TEXT[5];
    public final String scp_artAuthor = scp.TEXT[6];
    public final String scp_emptyAugment = scp.TEXT[7];
    public final String scp_viewAugments = scp.TEXT[8];
    public final String scp_viewTooltips = scp.TEXT[9];
    public final String scp_clickToSlot = scp.TEXT[10];
    public final String scp_clickToRemove = scp.TEXT[11];
    public final String scp_cannotRemove = scp.TEXT[12];
    // Subjects
    public final String subjects_allyN = subjects.TEXT[0];
    public final String subjects_cardN = subjects.TEXT[1];
    public final String subjects_characterN = subjects.TEXT[2];
    public final String subjects_enemyN = subjects.TEXT[3];
    public final String subjects_relicN = subjects.TEXT[4];
    public final String subjects_cost = subjects.TEXT[5];
    public final String subjects_infinite = subjects.TEXT[6];
    public final String subjects_maxX = subjects.TEXT[7];
    public final String subjects_minX = subjects.TEXT[8];
    public final String subjects_other = subjects.TEXT[9];
    public final String subjects_xCount = subjects.TEXT[10];
    public final String subjects_allX = subjects.TEXT[11];
    public final String subjects_all = subjects.TEXT[12];
    public final String subjects_ally = subjects.TEXT[13];
    public final String subjects_any = subjects.TEXT[14];
    public final String subjects_anyX = subjects.TEXT[15];
    public final String subjects_anyone = subjects.TEXT[16];
    public final String subjects_attacks = subjects.TEXT[17];
    public final String subjects_bonus = subjects.TEXT[18];
    public final String subjects_card = subjects.TEXT[19];
    public final String subjects_character = subjects.TEXT[20];
    public final String subjects_copiesOfX = subjects.TEXT[21];
    public final String subjects_damage = subjects.TEXT[22];
    public final String subjects_effectBonus = subjects.TEXT[23];
    public final String subjects_enemy = subjects.TEXT[24];
    public final String subjects_everyone = subjects.TEXT[25];
    public final String subjects_fromX = subjects.TEXT[26];
    public final String subjects_hits = subjects.TEXT[27];
    public final String subjects_inX = subjects.TEXT[28];
    public final String subjects_ofX = subjects.TEXT[29];
    public final String subjects_permanentlyX = subjects.TEXT[30];
    public final String subjects_playingXWithY = subjects.TEXT[31];
    public final String subjects_randomX = subjects.TEXT[32];
    public final String subjects_randomlyX = subjects.TEXT[33];
    public final String subjects_relic = subjects.TEXT[34];
    public final String subjects_shuffleYourDeck = subjects.TEXT[35];
    public final String subjects_bottomOfX = subjects.TEXT[36];
    public final String subjects_leftmostX = subjects.TEXT[37];
    public final String subjects_rightmostX = subjects.TEXT[38];
    public final String subjects_target = subjects.TEXT[39];
    public final String subjects_topOfX = subjects.TEXT[40];
    public final String subjects_theirX = subjects.TEXT[41];
    public final String subjects_themX = subjects.TEXT[42];
    public final String subjects_theyX = subjects.TEXT[43];
    public final String subjects_thisCard = subjects.TEXT[44];
    public final String subjects_this = subjects.TEXT[45];
    public final String subjects_unblockedX = subjects.TEXT[46];
    public final String subjects_x = subjects.TEXT[47];
    public final String subjects_you = subjects.TEXT[48];
    public final String subjects_yourFirstX = subjects.TEXT[49];
    public final String subjects_yourX = subjects.TEXT[50];
    public final String subjects_xOfY = subjects.TEXT[51];
    public final String subjects_xOnY = subjects.TEXT[52];
    public final String subjects_xThisCombat = subjects.TEXT[53];
    public final String subjects_xThisTurn = subjects.TEXT[54];
    public final String subjects_xTimes = subjects.TEXT[55];
    public final String subjects_xWithY = subjects.TEXT[56];
    public final String subjects_xCost = subjects.TEXT[57];
    // Tutorial
    public final String tutorial_learnMore = tutorial.TEXT[0];
    public final String tutorial_tagTutorial = tutorial.TEXT[1];
    public final String tutorial_affinityTutorial = tutorial.TEXT[2];
    public final String tutorial_summonTutorial1 = tutorial.TEXT[3];
    public final String tutorial_summonTutorial2 = tutorial.TEXT[4];
    public final String tutorial_summonTutorial3 = tutorial.TEXT[5];
    public final String tutorial_summonTutorial4 = tutorial.TEXT[6];
    public final String tutorial_summonTutorial5 = tutorial.TEXT[7];
    public final String tutorial_summonTutorial6 = tutorial.TEXT[8];
    public final String tutorial_summonTutorial7 = tutorial.TEXT[9];
    public final String tutorial_augmentTutorial1 = tutorial.TEXT[10];
    public final String tutorial_augmentTutorial2 = tutorial.TEXT[11];
    public final String tutorial_tutorialReplay = tutorial.TEXT[12];
    public final String tutorial_tutorialNextStep = tutorial.TEXT[13];
    public final String tutorial_tutorialComplete = tutorial.TEXT[14];
    public final String tutorial_tutorialStepHeader = tutorial.TEXT[15];
    public final String tutorial_tutorialCompleteHeader = tutorial.TEXT[16];

    public PCLCoreStrings(PCLResources<?, ?, ?, ?> resources) {
        super(resources);
    }

    public static String colorString(String colorHex, Object item) {
        return "{#" + colorHex + ":" + item + "}";
    }

    public static String headerString(String title, Object desc) {
        return "{#p:" + title + "}: " + desc;
    }

    public static String joinWith(FuncT2<String, String, String> strFunc, List<String> values) {
        if (values.size() == 0) {
            return "";
        }
        if (values.size() == 1) {
            return values.get(0);
        }
        StringJoiner sj = new StringJoiner(", ");

        int i = 0;
        for (i = 0; i < values.size() - 1; i++) {
            sj.add(values.get(i));
        }

        return strFunc.invoke(sj.toString(), values.get(i));
    }

    public static String joinWith(FuncT2<String, String, String> strFunc, String... values) {
        if (values.length == 0) {
            return "";
        }
        if (values.length == 1) {
            return values[0];
        }
        StringJoiner sj = new StringJoiner(", ");
        int var4 = values.length;

        int i = 0;
        for (i = 0; i < values.length - 1; i++) {
            sj.add(values[i]);
        }

        return strFunc.invoke(sj.toString(), values[i]);
    }

    public static <T> String joinWith(FuncT2<String, String, String> strFunc, FuncT1<String, T> valueToStr, List<T> values) {
        if (values.size() == 0) {
            return "";
        }
        if (values.size() == 1) {
            return valueToStr.invoke(values.get(0));
        }
        StringJoiner sj = new StringJoiner(", ");

        int i = 0;
        for (i = 0; i < values.size() - 1; i++) {
            sj.add(valueToStr.invoke(values.get(i)));
        }

        return strFunc.invoke(sj.toString(), valueToStr.invoke(values.get(i)));
    }

    @SafeVarargs
    public static <T> String joinWith(FuncT2<String, String, String> strFunc, FuncT1<String, T> valueToStr, T... values) {
        if (values.length == 0) {
            return "";
        }
        if (values.length == 1) {
            return valueToStr.invoke(values[0]);
        }
        StringJoiner sj = new StringJoiner(", ");
        int var4 = values.length;

        int i = 0;
        for (i = 0; i < values.length - 1; i++) {
            sj.add(valueToStr.invoke(values[i]));
        }

        return strFunc.invoke(sj.toString(), valueToStr.invoke(values[i]));
    }

    public static String joinWithAnd(List<String> values) {
        return joinWith(PGR.core.strings::cond_xAndY, values);
    }

    public static <T> String joinWithAnd(FuncT1<String, T> valueToStr, List<T> values) {
        return joinWith(PGR.core.strings::cond_xAndY, valueToStr, values);
    }

    public static String joinWithAnd(String... values) {
        return joinWith(PGR.core.strings::cond_xAndY, values);
    }

    @SafeVarargs
    public static <T> String joinWithAnd(FuncT1<String, T> valueToStr, T... values) {
        return joinWith(PGR.core.strings::cond_xAndY, valueToStr, values);
    }

    public static String joinWithOr(List<String> values) {
        return joinWith(PGR.core.strings::cond_or, values);
    }

    public static <T> String joinWithOr(FuncT1<String, T> valueToStr, List<T> values) {
        return joinWith(PGR.core.strings::cond_or, valueToStr, values);
    }

    public static String joinWithOr(String... values) {
        return joinWith(PGR.core.strings::cond_or, values);
    }

    @SafeVarargs
    public static <T> String joinWithOr(FuncT1<String, T> valueToStr, T... values) {
        return joinWith(PGR.core.strings::cond_or, valueToStr, values);
    }

    public static String leftClick(Object desc) {
        return "{#o:" + PGR.core.strings.misc_leftClick + "}: " + desc;
    }

    public static String past(Object obj) {
        return obj instanceof EUIKeywordTooltip ? ((EUIKeywordTooltip) obj).past() : EUIRM.strings.past(obj);
    }

    public static String period(boolean addPeriod) {
        return addPeriod ? LocalizedStrings.PERIOD : "";
    }

    public static String plural(EUIKeywordTooltip tip, Object evaluated) {
        return plural(tip.plural(), evaluated);
    }

    public static String plural(String tip, Object evaluated) {
        return evaluated instanceof Integer ? pluralEvaluated(tip, evaluated) : EUIUtils.format(tip, evaluated);
    }

    public static String pluralEvaluated(String tip, Object evaluated) {
        return EUISmartText.parseLogicString(EUIUtils.format(tip, evaluated));
    }

    public static String pluralForce(String tip) {
        return EUISmartText.parseLogicString(EUIUtils.format(tip, 2));
    }

    public static String present(Object obj) {
        return obj instanceof EUIKeywordTooltip ? ((EUIKeywordTooltip) obj).present() : EUIRM.strings.present(obj);
    }

    public static String rightClick(Object desc) {
        return "{#o:" + PGR.core.strings.misc_rightClick + "}: " + desc;
    }

    public static String singularForce(String tip) {
        return EUISmartText.parseLogicString(EUIUtils.format(tip, 1));
    }

    private String actFmt(int index, Object... objects) {
        return EUIUtils.format(actions.TEXT[index], objects);
    }

    private String actFmt(int index) {
        return actions.TEXT[index];
    }

    //@Formatter: Off

    // Actions functions
    public final String act_activate(Object desc1) {
        return actFmt(0, desc1);
    }
    public final String act_addToPile(Object desc1, Object desc2, Object pile) {
        return actFmt(1, desc1, desc2, pile);
    }
    public final String act_applyToTarget(Object power, Object target) {
        return actFmt(2, power, target);
    }
    public final String act_applyXToTarget(Object amount, Object power, Object target) {
        return actFmt(3, amount, power, target);
    }
    public final String act_applyX(Object amount, Object power) {
        return actFmt(4, amount, power);
    }
    public final String act_choose(Object amount) {
        return actFmt(5, amount);
    }
    public final String act_costs(Object amount) {
        return actFmt(6, amount);
    }
    public final String act_deal(Object amount, Object damage) {
        return actFmt(7, amount, damage);
    }
    public final String act_dealTo(Object amount, Object damage, Object target) {
        return actFmt(8, amount, damage, target);
    }
    public final String act_deals(Object power) {
        return actFmt(9, power);
    }
    public final String act_enterStance(Object stance) {
        return actFmt(10, stance);
    }
    public final String act_exitStance() {
        return actFmt(11);
    }
    public final String act_gainAmount(Object amount, Object power) {
        return actFmt(12, amount, power);
    }
    public final String act_gain(Object power) {
        return actFmt(13, power);
    }
    public final String act_giveTarget(Object target, Object power) {
        return actFmt(14, target, power);
    }
    public final String act_giveTargetAmount(Object target, Object amount, Object power) {
        return actFmt(15, target, amount, power);
    }
    public final String act_has(Object amount) {
        return actFmt(16, amount);
    }
    public final String act_hasAmount(Object amount, Object power) {
        return actFmt(17, amount, power);
    }
    public final String act_haveObject(Object amount, Object power) {
        return actFmt(18, amount, power);
    }
    public final String act_increaseBy(Object power, Object amount) {
        return actFmt(19, power, amount);
    }
    public final String act_increasePropertyBy(Object property, Object target, Object amount) {
        return actFmt(20, property, target, amount);
    }
    public final String act_increasePropertyFromBy(Object property, Object target, Object source, Object amount) {
        return actFmt(21, property, target, source, amount);
    }
    public final String act_lose(Object power) {
        return actFmt(22, power);
    }
    public final String act_loseAmount(Object amount, Object power) {
        return actFmt(23, amount, power);
    }
    public final String act_move(Object amount, Object target) {
        return actFmt(24, amount, target);
    }
    public final String act_moveTo(Object amount, Object dest, Object target) {
        return actFmt(25, amount, dest, target);
    }
    public final String act_obtain(Object card) {
        return actFmt(26, card);
    }
    public final String act_obtainAmount(Object amount, Object card) {
        return actFmt(27, amount, card);
    }
    public final String act_pay(Object amount, Object power) {
        return actFmt(28, amount, power);
    }
    public final String act_reduceBy(Object power, Object amount) {
        return actFmt(29, power, amount);
    }
    public final String act_reducePropertyBy(Object property, Object target, Object amount) {
        return actFmt(30, property, target, amount);
    }
    public final String act_reducePropertyFromBy(Object property, Object target, Object source, Object amount) {
        return actFmt(31, property, target, source, amount);
    }
    public final String act_remove(Object item) {
        return actFmt(32, item);
    }
    public final String act_removeFrom(Object item, Object target) {
        return actFmt(33, item, target);
    }
    public final String act_removeFromPlace(Object item, Object target, Object place) {
        return actFmt(34, item, target, place);
    }
    public final String act_removeInPlace(Object item, Object target, Object place) {
        return actFmt(35, item, target, place);
    }
    public final String act_repeat(Object times) {
        return actFmt(36, times);
    }
    public final String act_select(Object amount) {
        return actFmt(37, amount);
    }
    public final String act_setOf(Object item, Object target, Object affinity6) {
        return actFmt(38, item, target, affinity6);
    }
    public final String act_setOfFrom(Object item, Object target, Object place, Object affinity6) {
        return actFmt(39, item, target, place, affinity6);
    }
    public final String act_setTo(Object item, Object affinity) {
        return actFmt(40, item, affinity);
    }
    public final String act_setTheLast(Object amount, Object item, Object affinity) {
        return actFmt(41, amount, item, affinity);
    }
    public final String act_stealFrom(Object amount, Object item, Object target) {
        return actFmt(42, amount, item, target);
    }
    public final String act_stealX(Object amount, Object power) {
        return actFmt(43, amount, power);
    }
    public final String act_stun(Object target) {
        return actFmt(44, target);
    }
    public final String act_takeDamage(Object amount) {
        return actFmt(45, amount);
    }
    public final String act_transform(Object subject, Object target) {
        return actFmt(46, subject, target);
    }
    public final String act_use(Object target) {
        return actFmt(47, target);
    }
    public final String act_youCannotGain(Object target) {
        return actFmt(48, target);
    }
    public final String act_zCosts(Object object, Object ordinal, Object amount) {
        return actFmt(49, object, ordinal, amount);
    }
    public final String act_zGainsBonus(Object object, Object amount, Object bonus) {
        return actFmt(50, object, amount, bonus);
    }
    public final String act_zHas(Object object, Object amount) {
        return actFmt(51, object, amount);
    }
    public final String act_zLoses(Object object, Object ordinal, Object amount, Object bonus) {
        return actFmt(52, object, ordinal, amount, bonus);
    }
    public final String act_zOn(Object action, Object object, Object target) {
        return actFmt(53, action, object, target);
    }
    public final String act_zOnAmount(Object action, Object amount, Object object, Object target) {
        return actFmt(54, action, amount, object, target);
    }
    public final String act_zTo(Object action, Object object, Object target) {
        return actFmt(55, action, object, target);
    }

    public final String act_channel(Object subject) {
        return act_generic2(PGR.core.tooltips.channel.title, subject);
    }
    public final String act_channelX(Object amount, Object subject) {
        return act_generic3(PGR.core.tooltips.channel.title, amount, subject);
    }
    public final String act_cycle(Object amount) {
        return act_generic2(PGR.core.tooltips.cycle.title, amount);
    }
    public final String act_cycleType(Object amount, Object subject) {
        return act_generic3(PGR.core.tooltips.cycle.title, amount, subject);
    }
    public final String act_discard(Object amount) {
        return act_generic2(PGR.core.tooltips.discard.title, amount);
    }
    public final String act_draw(Object amount) {
        return act_generic2(PGR.core.tooltips.draw.title, amount);
    }
    public final String act_drawType(Object amount, Object subject) {
        return act_generic3(PGR.core.tooltips.draw.title, amount, subject);
    }
    public final String act_evoke(Object subject) {
        return act_generic2(PGR.core.tooltips.evoke.title, subject);
    }
    public final String act_evokeXTimes(Object subject, Object amount) {
        return act_genericTimes(PGR.core.tooltips.evoke.title, subject, amount);
    }
    public final String act_exhaust(Object amount) {
        return act_generic2(PGR.core.tooltips.exhaust.title, amount);
    }
    public final String act_fetch(Object amount) {
        return act_generic2(PGR.core.tooltips.fetch.title, amount);
    }
    public final String act_generic2(Object verb, Object subject) {
        return EUIRM.strings.verbNoun(verb, subject);
    }
    public final String act_generic3(Object verb, Object adj, Object subject) {
        return EUIRM.strings.verbAdjNoun(verb, adj, subject);
    }
    public final String act_genericFrom(Object verb, Object amount, Object subject, Object target) {
        return EUIRM.strings.verbNumNounPlace(verb, amount, subject, subjects_from(target));
    }
    public final String act_genericTimes(Object verb, Object subject, Object times) {
        return EUIRM.strings.verbNounAdv(verb, subject, subjects_times(times));
    }
    public final String act_giveFrom(Object subject, Object target, Object power) {
        return act_giveTargetAmount(subject, subjects_from(target), power);
    }
    public final String act_heal(Object amount) {
        return act_generic3(PGR.core.tooltips.heal.title, amount, PGR.core.tooltips.hp.title);
    }
    public final String act_healOn(Object amount, Object target) {
        return act_zOnAmount(PGR.core.tooltips.heal.title, amount, PGR.core.tooltips.hp.title, target);
    }
    public final String act_kill(String targetString) {
        return act_generic2(PGR.core.tooltips.kill.title, targetString);
    }
    public final String act_play(Object amount) {
        return EUIRM.strings.verbNoun(PGR.core.tooltips.play.title, amount);
    }
    public final String act_playFrom(Object amount, Object subject, Object target) {
        return act_genericFrom(PGR.core.tooltips.play.title, amount, subject, target);
    }
    public final String act_playXTimes(Object subject, Object amount) {
        return act_genericTimes(PGR.core.tooltips.play.title, subject, amount);
    }
    public final String act_purge(Object amount) {
        return act_generic2(PGR.core.tooltips.purge.title, amount);
    }
    public final String act_reduceCooldown(Object target, Object amount) {
        return act_reducePropertyBy(PGR.core.tooltips.cooldown.title, target, amount);
    }
    public final String act_reshuffle(Object amount) {
        return act_generic2(PGR.core.tooltips.reshuffle.title, amount);
    }
    public final String act_retain(Object amount, Object subject) {
        return EUIRM.strings.verbAdjNoun(PGR.core.tooltips.retain.title, amount, subject);
    }
    public final String act_retain(Object amount) {
        return act_generic2(PGR.core.tooltips.retain.title, amount);
    }
    public final String act_retainX(Object amount, Object subject) {
        return act_generic3(PGR.core.tooltips.retain.title, amount, subject);
    }
    public final String act_scout(Object amount) {
        return act_generic2(PGR.core.tooltips.scout.title, amount);
    }
    public final String act_scry(Object amount) {
        return act_generic2(PGR.core.tooltips.scry.title, amount);
    }
    public final String act_spread(Object subject, Object target) {
        return act_zOn(PGR.core.tooltips.spread.title, subject, target);
    }
    public final String act_spreadAmount(Object amount, Object subject, Object target) {
        return act_zOnAmount(PGR.core.tooltips.spread.title, amount, subject, target);
    }
    public final String act_stabilize(Object subject, Object target) {
        return act_zOn(PGR.core.tooltips.stabilize.title, subject, target);
    }
    public final String act_trigger(Object subject) {
        return act_generic2(PGR.core.tooltips.trigger.title, subject);
    }
    public final String act_triggerXTimes(Object subject, Object amount) {
        return act_genericTimes(PGR.core.tooltips.trigger.title, subject, amount);
    }
    public final String act_upgrade(Object amount) {
        return act_generic2(PGR.core.tooltips.upgrade.title, amount);
    }
    public final String act_upgradeFrom(Object amount, Object subject, Object target) {
        return act_genericFrom(PGR.core.tooltips.upgrade.title, amount, subject, target);
    }
    public final String act_withdraw(Object subject) {
        return act_generic2(PGR.core.tooltips.withdraw.title, subject);
    }



    public final String combat_controlPileDescriptionFull(String keyName) {
        return EUIUtils.format(combat_controlPileDescription, keyName);
    }

    public final String combat_count(Object t, Object desc) {
        return headerString(EUIUtils.format(combat_count, t), desc);
    }

    public final String combat_effect(Object desc) {
        return headerString(combat_effect, desc);
    }

    public final String combat_nextLevelEffect(Object desc) {
        return headerString(combat_nextLevelEffect, desc);
    }

    private String condFmt(int index, Object... objects) {
        String text = conditions.TEXT[index];
        return EUIUtils.format(text, objects);
    }

    private String condFmt(int index) {
        return conditions.TEXT[index];
    }

    // Condition functions
    public final String cond_any(Object desc1) {
        return condFmt(0, desc1);
    }
    public final String cond_atEndOfCombat() {
        return condFmt(1);
    }
    public final String cond_atStartOfCombat() {
        return condFmt(2);
    }
    public final String cond_atEndOfTurn() {
        return condFmt(3);
    }
    public final String cond_atStartOfTurn() {
        return condFmt(4);
    }
    public final String cond_doX(Object desc3) {
        return condFmt(5, desc3);
    }
    public final String cond_doForEach() {
        return condFmt(6);
    }
    public final String cond_forTurns(Object desc3) {
        return condFmt(7, desc3);
    }
    public final String cond_ifTargetDidX(Object target, Object desc3, Object desc4) {
        return condFmt(11, target, desc3, desc4);
    }
    public final String cond_ifTargetHas(Object target, Object ordinal, Object desc) {
        return condFmt(9, target, ordinal, desc);
    }
    public final String cond_ifTargetIs(Object target, Object desc3) {
        return cond_ifX(cond_objIs(target, desc3));
    }
    public final String cond_ifTargetTook(Object target, Object desc) {
        return condFmt(10, target, desc);
    }
    public final String cond_ifX(Object desc3) {
        return condFmt(12, desc3);
    }
    public final String cond_ifYouDidThisCombat(Object verb, Object obj) {
        return subjects_thisCombat(cond_ifTargetDidX(subjects_you, verb, obj));
    }
    public final String cond_ifYouDidThisTurn(Object verb, Object obj) {
        return subjects_thisTurn(cond_ifTargetDidX(subjects_you, verb, obj));
    }
    public final String cond_ifYourHighest(Object desc3) {
        return condFmt(8, desc3);
    }
    public final String cond_inOrderTo(Object desc3, Object desc4) {
        return condFmt(37, desc3, desc4);
    }
    public final String cond_inTurns(Object desc3) {
        return condFmt(15, desc3);
    }
    public final String cond_inXAtTurnEnd(Object desc3) {
        return condFmt(13, desc3);
    }
    public final String cond_inXAtTurnStart(Object desc3) {
        return condFmt(14, desc3);
    }
    public final String cond_levelItem(Object level, Object desc3) {
        return condFmt(16, level, desc3);
    }
    public final String cond_nextTurn() {
        return condFmt(17);
    }
    public final String cond_no(Object desc3) {
        return condFmt(18, desc3);
    }
    public final String cond_not(Object desc3) {
        return condFmt(19, desc3);
    }
    public final String cond_numIf(Object desc3, Object desc4) {
        return condFmt(28, desc3, desc4);
    }
    public final String cond_objIs(Object desc3, Object desc4) {
        return condFmt(29, desc3, desc4);
    }
    public final String cond_onGeneric(Object desc3) {
        return condFmt(20, desc3);
    }
    public final String cond_or(Object desc3, Object desc4) {
        return condFmt(30, desc3, desc4);
    }
    public final String cond_otherwise(Object desc3) {
        return condFmt(21, desc3);
    }
    public final String cond_passive() {
        return condFmt(40);
    }
    public final String cond_per(Object subject, Object per) {
        return condFmt(31, subject, per);
    }
    public final String cond_perDistinct(Object desc3, Object desc4) {
        return condFmt(34, desc3, desc4);
    }
    public final String cond_perIn(Object desc3, Object desc4, Object desc5) {
        return condFmt(33, desc3, desc4, desc5);
    }
    public final String cond_perThisCombat(Object desc3, Object desc4, Object desc5) {
        return subjects_thisCombat(cond_perXY(desc3, desc4, desc5));
    }
    public final String cond_perThisTurn(Object desc3, Object desc4, Object desc5) {
        return subjects_thisTurn(cond_perXY(desc3, desc4, desc5));
    }
    public final String cond_perXY(Object desc3, Object desc4, Object desc5) {
        return condFmt(32, desc3, desc4, desc5);
    }
    public final String cond_timesPerCombat(Object desc3) {
        return condFmt(35, desc3);
    }
    public final String cond_timesPerTurn(Object desc3) {
        return condFmt(36, desc3);
    }
    public final String cond_whenAObjectIs(Object desc3, Object desc4) {
        return condFmt(25, desc3, desc4);
    }
    public final String cond_whenMulti(Object desc3, Object desc4) {
        return condFmt(22, desc3, desc4);
    }
    public final String cond_whenObjectIs(Object subject, Object ord, Object desc) {
        return condFmt(24, subject, ord, desc);
    }
    public final String cond_whenSingle(Object desc3) {
        return condFmt(23, desc3);
    }
    public final String cond_wheneverYou(Object desc1) {
        return cond_whenMulti(subjects_you, desc1);
    }
    public final String cond_xActivated(Object desc1) {
        return condFmt(26, desc1);
    }
    public final String cond_xAndY(Object desc1, Object desc2) {
        return condFmt(27, desc1, desc2);
    }
    public final String cond_xThenY(Object desc3, Object desc4) {
        return condFmt(38, desc3, desc4);
    }
    public final String cond_xConditional(Object desc3, Object desc4) {
        return condFmt(39, desc3, desc4);
    }

    public final String csel_obtainBronzeAtAscension(int ascension) {
        return EUIUtils.format(charselect.TEXT[7], ascension);
    }

    public final String grid_cardsInPile(Object item, int amount) {
        return EUIUtils.format(grid_cardsInPile, item, amount);
    }
    public final String grid_chooseCards(int amount) {
        return EUIUtils.format(grid_chooseCards, amount);
    }

    public final String loadout_cardsCount(int value) {
        return EUIUtils.format(loadout_cardsCount, value);
    }
    public final String loadout_hindranceValue(int value) {
        return EUIUtils.format(loadout_hindranceValue, value);
    }
    public final String loadout_totalValue(int value, int max) {
        return EUIUtils.format(loadout_totalValue, value, max);
    }

    public final String rewards_goldBonus(int amount) {
        return EUIUtils.format(rewards_goldbonusF1, amount);
    }
    public final String rewards_maxHPBonus(int amount) {
        return EUIUtils.format(rewards_maxhpbonusF1, amount);
    }

    // Subject functions
    public final String subjects_allAllies() {
        return EUIUtils.format(subjects_allX, PCLCoreStrings.pluralForce(subjects_allyN));
    }
    public final String subjects_allEnemies() {
        return EUIUtils.format(subjects_allX, PCLCoreStrings.pluralForce(subjects_enemyN));
    }
    public final String subjects_allX(Object amount) {
        return EUIUtils.format(subjects_allX, amount);
    }
    public final String subjects_allyWithX(Object obj) {
        return subjects_withX(subjects_ally, obj);
    }
    public final String subjects_anyAlly() {
        return EUIUtils.format(subjects_anyX, subjects_ally);
    }
    public final String subjects_anyEnemy() {
        return EUIUtils.format(subjects_anyX, subjects_enemy);
    }
    public final String subjects_anyPile() {
        return EUIUtils.format(subjects_anyX, cpile_pile);
    }
    public final String subjects_anyX(Object amount) {
        return EUIUtils.format(subjects_anyX, amount);
    }
    public final String subjects_attacks(Object ordinal) {
        return EUIUtils.format(subjects_attacks, ordinal);
    }
    public final String subjects_bottomOf(Object amount) {
        return EUIUtils.format(subjects_bottomOfX, amount);
    }
    public final String subjects_characterWithX(Object obj) {
        return subjects_withX(subjects_character, obj);
    }
    public final String subjects_copiesOf(Object obj) {
        return EUIUtils.format(subjects_copiesOfX, obj);
    }
    public final String subjects_count(Object amount) {
        return EUIUtils.format(subjects_xCount, amount);
    }
    public final String subjects_enemyWithX(Object obj) {
        return subjects_withX(subjects_enemy, obj);
    }
    public final String subjects_from(Object place) {
        return EUIUtils.format(subjects_fromX, place);
    }
    public final String subjects_in(Object place) {
        return EUIUtils.format(subjects_fromX, place);
    }
    public final String subjects_max(Object amount) {
        return EUIUtils.format(subjects_maxX, amount);
    }
    public final String subjects_min(Object amount) {
        return EUIUtils.format(subjects_minX, amount);
    }
    public final String subjects_ofX(Object amount) {
        return EUIUtils.format(subjects_ofX, amount);
    }
    public final String subjects_onAnyCharacter(Object desc1) {
        return subjects_onTarget(desc1, subjects_anyone);
    }
    public final String subjects_onAnyEnemy(Object desc1) {
        return subjects_onTarget(desc1, subjects_anyEnemy());
    }
    public final String subjects_onTarget(Object desc1, Object desc2) {
        return EUIUtils.format(subjects_xOnY, desc1, desc2);
    }
    public final String subjects_onTheEnemy(Object desc1) {
        return subjects_onTarget(desc1, subjects_target);
    }
    public final String subjects_onYou(Object desc1) {
        return subjects_onTarget(desc1, subjects_you);
    }
    public final String subjects_permanentlyX(Object obj) {
        return EUIUtils.format(subjects_permanentlyX, obj);
    }
    public final String subjects_playingXWith(Object t1, Object t2) {
        return EUIUtils.format(subjects_playingXWithY, t1, t2);
    }
    public final String subjects_randomX(Object amount) {
        return EUIUtils.format(subjects_randomX, amount);
    }
    public final String subjects_randomly(Object amount) {
        return EUIUtils.format(subjects_randomlyX, amount);
    }
    public final String subjects_theirX(Object amount) {
        return EUIUtils.format(subjects_theirX, amount);
    }
    public final String subjects_them(Object amount) {
        return EUIUtils.format(subjects_themX, amount);
    }
    public final String subjects_they(Object amount) {
        return EUIUtils.format(subjects_theyX, amount);
    }
    public final String subjects_thisCombat(String base) {
        return EUIUtils.format(subjects_xThisCombat, base);
    }
    public final String subjects_thisTurn(String base) {
        return EUIUtils.format(subjects_xThisTurn, base);
    }
    public final String subjects_times(Object amount) {
        return EUIUtils.format(subjects_xTimes, amount);
    }
    public final String subjects_topOf(Object amount) {
        return EUIUtils.format(subjects_topOfX, amount);
    }
    public final String subjects_unblocked(Object amount) {
        return EUIUtils.format(subjects_unblockedX, amount);
    }
    public final String subjects_withX(Object obj, Object t) {
        return EUIUtils.format(subjects_xWithY, obj, t);
    }
    public final String subjects_xCost(Object amount) {
        return EUIUtils.format(subjects_xCost, amount);
    }
    public final String subjects_xOfY(Object obj, Object t) {
        return EUIUtils.format(subjects_xOfY, obj, t);
    }
    public final String subjects_your(Object amount) {
        return EUIUtils.format(subjects_yourX, amount);
    }
    public final String subjects_yourFirst(Object amount) {
        return EUIUtils.format(subjects_yourFirstX, amount);
    }

    public final String sui_selected(Object amount, Object total) {
        return EUIUtils.format(sui_selected, amount, total);
    }

    public final String sui_totalCards(Object cardCount, Object total, Object color, Object req) {
        return EUIUtils.format(sui_totalCards, cardCount, total, color, req);
    }

    public final String sui_unlocked(Object amount, Object total) {
        return EUIUtils.format(sui_unlocked, amount, total);
    }

    //@Formatter: On
}