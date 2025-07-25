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
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.EUITextHelper;
import pinacolada.dungeon.SummonPool;
import pinacolada.resources.AbstractStrings;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;

import java.util.List;
import java.util.StringJoiner;

public class PCLCoreStrings extends AbstractStrings {
    private final UIStrings actions = getUIStrings("Actions");
    private final UIStrings augment = getUIStrings("Augment");
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
    private final UIStrings power = getUIStrings("Power");
    private final UIStrings rewards = getUIStrings("Rewards");
    private final UIStrings seriesui = getUIStrings("SeriesUI");
    private final UIStrings scp = getUIStrings("SingleCardPopupButtons");
    private final UIStrings subjects = getUIStrings("Subjects");
    private final UIStrings tutorial = getUIStrings("Tutorial");
    public final String grid_discard = DiscardAction.TEXT[0];
    public final String grid_exhaust = ExhaustAction.TEXT[0];
    public final String grid_cycle = GamblingChipAction.TEXT[1];
    public final String grid_chooseOneCard = CardRewardScreen.TEXT[1];
    // Augment
    public final String augment_category = augment.TEXT[0];
    public final String augment_requirement = augment.TEXT[1];
    public final String augment_tier = augment.TEXT[2];
    public final String augment_unremovable = augment.TEXT[3];
    public final String augment_unremovableDesc = augment.TEXT[4];
    public final String augment_general = augment.TEXT[5];
    public final String augment_played = augment.TEXT[6];
    public final String augment_hindrance = augment.TEXT[7];
    public final String augment_generalDesc = augment.TEXT[8];
    public final String augment_playedDesc = augment.TEXT[9];
    public final String augment_summonDesc = augment.TEXT[10];
    public final String augment_hindranceDesc = augment.TEXT[11];
    public final String augment_specialDesc = augment.TEXT[12];
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
    public final String cedit_trait = cedit.TEXT[20];
    public final String cedit_trigger = cedit.TEXT[21];
    public final String cedit_multiEffect = cedit.TEXT[22];
    public final String cedit_multiCondition = cedit.TEXT[23];
    public final String cedit_branchCondition = cedit.TEXT[24];
    public final String cedit_powers = cedit.TEXT[25];
    public final String cedit_maxUpgrades = cedit.TEXT[26];
    public final String cedit_flags = cedit.TEXT[27];
    public final String cedit_form = cedit.TEXT[28];
    public final String cedit_turnDelay = cedit.TEXT[29];
    public final String cedit_extraValue = cedit.TEXT[30];
    public final String cedit_maxCopies = cedit.TEXT[31];
    public final String cedit_loadImage = cedit.TEXT[32];
    public final String cedit_loadFile = cedit.TEXT[33];
    public final String cedit_paste = cedit.TEXT[34];
    public final String cedit_addForm = cedit.TEXT[35];
    public final String cedit_removeForm = cedit.TEXT[36];
    public final String cedit_undo = cedit.TEXT[37];
    public final String cedit_customPower = cedit.TEXT[38];
    public final String cedit_branchUpgrade = cedit.TEXT[39];
    public final String cedit_duplicate = cedit.TEXT[40];
    public final String cedit_delete = cedit.TEXT[41];
    public final String cedit_reloadCards = cedit.TEXT[42];
    public final String cedit_confirmDeletion = cedit.TEXT[43];
    public final String cedit_confirmDeletionDesc = cedit.TEXT[44];
    public final String cedit_customCards = cedit.TEXT[45];
    public final String cedit_addToEffect = cedit.TEXT[46];
    public final String cedit_primaryInfo = cedit.TEXT[47];
    public final String cedit_primaryInfoDesc = cedit.TEXT[48];
    public final String cedit_choices = cedit.TEXT[49];
    public final String cedit_not = cedit.TEXT[50];
    public final String cedit_or = cedit.TEXT[51];
    public final String cedit_idSuffix = cedit.TEXT[52];
    public final String cedit_idSuffixWarning = cedit.TEXT[53];
    public final String cedit_duplicateToColor = cedit.TEXT[54];
    public final String cedit_duplicateToColorDesc = cedit.TEXT[55];
    public final String cedit_useParent = cedit.TEXT[56];
    public final String cedit_loadFromCard = cedit.TEXT[57];
    public final String cedit_openFolder = cedit.TEXT[58];
    public final String cedit_export = cedit.TEXT[59];
    public final String cedit_random = cedit.TEXT[60];
    public final String cedit_required = cedit.TEXT[61];
    public final String cedit_origins = cedit.TEXT[62];
    public final String cedit_destinations = cedit.TEXT[63];
    public final String cedit_pile = cedit.TEXT[64];
    public final String cedit_combat = cedit.TEXT[65];
    public final String cedit_exact = cedit.TEXT[66];
    public final String cedit_liquidColor = cedit.TEXT[67];
    public final String cedit_hybridColor = cedit.TEXT[68];
    public final String cedit_spotsColor = cedit.TEXT[69];
    public final String cedit_anchorColor = cedit.TEXT[70];
    public final String cedit_targetColor = cedit.TEXT[71];
    public final String cedit_enableTint = cedit.TEXT[72];
    public final String cedit_tintDesc = cedit.TEXT[73];
    public final String cedit_repeat = cedit.TEXT[74];
    public final String cedit_newLoadout = cedit.TEXT[75];
    public final String cedit_renameItem = cedit.TEXT[76];
    public final String cedit_deleteItem = cedit.TEXT[77];
    public final String cedit_invert = cedit.TEXT[78];
    public final String cedit_every = cedit.TEXT[79];
    public final String cedit_minMaxStacks = cedit.TEXT[80];
    public final String cedit_scope = cedit.TEXT[81];
    public final String cedit_newFlag = cedit.TEXT[82];
    public final String cedit_turns = cedit.TEXT[83];
    public final String cedit_loadoutValue = cedit.TEXT[84];
    public final String cedit_augmentSlots = cedit.TEXT[85];
    public final String cedit_relicReplace = cedit.TEXT[86];
    public final String cedit_newEffect = cedit.TEXT[87];
    public final String cedit_generic = cedit.TEXT[88];
    public final String cedit_moveLeft = cedit.TEXT[89];
    public final String cedit_moveRight = cedit.TEXT[90];
    public final String cedit_orbSfx = cedit.TEXT[91];
    public final String cedit_orbRotation = cedit.TEXT[92];
    public final String cedit_orbFlash = cedit.TEXT[93];
    public final String cedit_orbFocusEvoke = cedit.TEXT[94];
    public final String cedit_orbFocusPassive = cedit.TEXT[95];
    public final String cedit_effekseer = cedit.TEXT[96];
    public final String cedit_editDesc = cedit.TEXT[97];
    public final String cedit_preview = cedit.TEXT[98];
    public final String cedit_partial = cedit.TEXT[99];
    public final String cedit_startTour = cedit.TEXT[100];
    public final String cedit_viewSummonTutorial = cedit.TEXT[101];
    public final String cedit_viewAugmentTutorial = cedit.TEXT[102];
    public final String cedit_quickAdd = cedit.TEXT[103];
    public final String cedit_enable = cedit.TEXT[104];
    public final String cedit_disable = cedit.TEXT[105];
    public final String cedit_cardPoolRestrict = cedit.TEXT[106];
    // Card Editor Tutorial
    public final String cetut_selectorGeneric = cetut.TEXT[0];
    public final String cetut_selectorCard = cetut.TEXT[1];
    public final String cetut_selectorRelic = cetut.TEXT[2];
    public final String cetut_selectorPotion = cetut.TEXT[3];
    public final String cetut_selectorBlight = cetut.TEXT[4];
    public final String cetut_selectorAugment = cetut.TEXT[5];
    public final String cetut_selectorReload = cetut.TEXT[6];
    public final String cetut_primaryForm = cetut.TEXT[7];
    public final String cetut_primaryImage = cetut.TEXT[8];
    public final String cetut_primaryFlags = cetut.TEXT[9];
    public final String cetut_attrTags1 = cetut.TEXT[10];
    public final String cetut_attrTags2 = cetut.TEXT[11];
    public final String cetut_attrAffinity = cetut.TEXT[12];
    public final String cetut_effectCondition = cetut.TEXT[13];
    public final String cetut_effectEffect = cetut.TEXT[14];
    public final String cetut_effectModifier = cetut.TEXT[15];
    public final String cetut_effectTrait = cetut.TEXT[16];
    public final String cetut_effectPrimary = cetut.TEXT[17];
    public final String cetut_effectTurnDelay = cetut.TEXT[18];
    public final String cetut_effectChoices = cetut.TEXT[19];
    public final String cetut_effectMultiCondition = cetut.TEXT[20];
    public final String cetut_effectBranchCondition = cetut.TEXT[21];
    public final String cetut_imageSelect = cetut.TEXT[22];
    public final String cetut_imageCrop = cetut.TEXT[23];
    public final String cetut_maxUpgrades = cetut.TEXT[24];
    public final String cetut_maxCopies = cetut.TEXT[25];
    public final String cetut_idSuffix = cetut.TEXT[26];
    public final String cetut_nameLanguage = cetut.TEXT[27];
    public final String cetut_rarity = cetut.TEXT[28];
    public final String cetut_type = cetut.TEXT[29];
    public final String cetut_cardTarget = cetut.TEXT[30];
    public final String cetut_amount = cetut.TEXT[31];
    public final String cetut_attackType = cetut.TEXT[32];
    public final String cetut_attackEffect = cetut.TEXT[33];
    public final String cetut_hitCount = cetut.TEXT[34];
    public final String cetut_blockCount = cetut.TEXT[35];
    public final String cetut_loadFromCardScreen = cetut.TEXT[36];
    public final String cetut_branchUpgrade = cetut.TEXT[37];
    public final String cetut_useParent = cetut.TEXT[38];
    public final String cetut_required1 = cetut.TEXT[39];
    public final String cetut_required2 = cetut.TEXT[40];
    public final String cetut_passive = cetut.TEXT[41];
    public final String cetut_when = cetut.TEXT[42];
    public final String cetut_interactable = cetut.TEXT[43];
    public final String cetut_onObtain = cetut.TEXT[44];
    public final String cetut_onRemove = cetut.TEXT[45];
    public final String cetut_combatEnd = cetut.TEXT[46];
    public final String cetut_bonus = cetut.TEXT[47];
    public final String cetut_nodeTutorial = cetut.TEXT[48];
    public final String cetut_blankPrimary = cetut.TEXT[49];
    public final String cetut_blankProxy = cetut.TEXT[50];
    public final String cetut_relicRarity = cetut.TEXT[51];
    public final String cetut_landingSound = cetut.TEXT[52];
    public final String cetut_addToEffect = cetut.TEXT[53];
    public final String cetut_primaryWarning = cetut.TEXT[54];
    public final String cetut_childWarning = cetut.TEXT[55];
    public final String cetut_undo = cetut.TEXT[56];
    public final String cetut_openFolder = cetut.TEXT[57];
    public final String cetut_topBarTutorial = cetut.TEXT[58];
    public final String cetut_potionRarity = cetut.TEXT[59];
    public final String cetut_potionSize = cetut.TEXT[60];
    public final String cetut_potionEffect = cetut.TEXT[61];
    public final String cetut_potionColor = cetut.TEXT[62];
    public final String cetut_blightUnique = cetut.TEXT[63];
    public final String cetut_augmentCategory = cetut.TEXT[64];
    public final String cetut_augmentSlots = cetut.TEXT[65];
    public final String cetut_augmentTier = cetut.TEXT[66];
    public final String cetut_powerType = cetut.TEXT[67];
    public final String cetut_powerTurnBehavior = cetut.TEXT[68];
    public final String cetut_powerPriority = cetut.TEXT[69];
    public final String cetut_powerMinMaxStacks = cetut.TEXT[70];
    public final String cetut_powerStandard = cetut.TEXT[71];
    public final String cetut_pile = cetut.TEXT[72];
    public final String cetut_origin = cetut.TEXT[73];
    public final String cetut_destination = cetut.TEXT[74];
    public final String cetut_effectTarget = cetut.TEXT[75];
    public final String cetut_amountPower = cetut.TEXT[76];
    public final String cetut_scope = cetut.TEXT[77];
    public final String cetut_turns = cetut.TEXT[78];
    public final String cetut_blightRarity = cetut.TEXT[79];
    public final String cetut_loadoutValue = cetut.TEXT[80];
    public final String cetut_blankAttack = cetut.TEXT[81];
    public final String cetut_blankBlock = cetut.TEXT[82];
    public final String cetut_relicReplace = cetut.TEXT[83];
    public final String cetut_colorAnchor = cetut.TEXT[84];
    public final String cetut_orbSfx = cetut.TEXT[85];
    public final String cetut_orbRotation = cetut.TEXT[86];
    public final String cetut_orbFlash = cetut.TEXT[87];
    public final String cetut_orbFocusPassive = cetut.TEXT[88];
    public final String cetut_orbFocusEvoke = cetut.TEXT[89];
    public final String cetut_effekseer = cetut.TEXT[90];
    public final String cetut_editDesc = cetut.TEXT[91];
    public final String cetut_legendDesc = cetut.TEXT[92];
    public final String cetut_legendLineBreak = cetut.TEXT[93];
    public final String cetut_legendColored = cetut.TEXT[94];
    public final String cetut_legendDamage = cetut.TEXT[95];
    public final String cetut_legendBlock = cetut.TEXT[96];
    public final String cetut_legendIcon = cetut.TEXT[97];
    public final String cetut_legendAmount = cetut.TEXT[98];
    public final String cetut_legendExtra = cetut.TEXT[99];
    public final String cetut_legendExtra2 = cetut.TEXT[100];
    public final String cetut_legendScope = cetut.TEXT[101];
    public final String cetut_legendUpgrade = cetut.TEXT[102];
    public final String cetut_legendDynamic = cetut.TEXT[103];
    public final String cetut_legendDynamic2 = cetut.TEXT[104];
    public final String cetut_cardPoolRestrict = cetut.TEXT[105];
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
    public final String combat_turns = combat.TEXT[16];
    // Card Pile
    public final String cpile_hand = cardPile.TEXT[0];
    public final String cpile_deck = cardPile.TEXT[1];
    public final String cpile_pile = cardPile.TEXT[2];
    public final String cpile_manual = cardPile.TEXT[3];
    public final String cpile_top = cardPile.TEXT[4];
    public final String cpile_bottom = cardPile.TEXT[5];
    public final String cpile_random = cardPile.TEXT[6];
    // Character
    public final String csel_leftText = charselect.TEXT[0];
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
    public final String csel_ability = charselect.TEXT[11];
    public final String csel_unlocksAt = charselect.TEXT[12];
    public final String csel_skinEditor = charselect.TEXT[13];
    public final String csel_skinEditorDesc = charselect.TEXT[14];
    public final String csel_betaSet = charselect.TEXT[15];
    public final String csel_betaSetDesc = charselect.TEXT[16];
    public final String csel_betaCardSet = charselect.TEXT[17];
    // Card type
    public final String ctype_allAlly = cardtype.TEXT[0];
    public final String ctype_allAllyEnemy = cardtype.TEXT[1];
    public final String ctype_randomAlly = cardtype.TEXT[2];
    public final String ctype_randomAllyEnemy = cardtype.TEXT[3];
    public final String ctype_randomEnemy = cardtype.TEXT[4];
    public final String ctype_singleAlly = cardtype.TEXT[5];
    public final String ctype_singleTarget = cardtype.TEXT[6];
    public final String ctype_team = cardtype.TEXT[7];
    public final String ctype_legendary = cardtype.TEXT[8];
    public final String ctype_secretRare = cardtype.TEXT[9];
    public final String ctype_tagAll = cardtype.TEXT[10];
    public final String ctype_tagAlly = cardtype.TEXT[11];
    public final String ctype_tagAoE = cardtype.TEXT[12];
    public final String ctype_tagAoEAlly = cardtype.TEXT[13];
    public final String ctype_tagRandom = cardtype.TEXT[14];
    public final String ctype_tagSingle = cardtype.TEXT[15];
    public final String ctype_turnStartFirst = cardtype.TEXT[16];
    public final String ctype_turnStartLast = cardtype.TEXT[17];
    public final String ctype_turnEndFirst = cardtype.TEXT[18];
    public final String ctype_turnEndLast = cardtype.TEXT[19];
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
    public final String loadout_tutorialCard = loadout.TEXT[26];
    public final String loadout_tutorialValue = loadout.TEXT[27];
    public final String loadout_tutorialRelic = loadout.TEXT[28];
    public final String loadout_tutorialAttributes = loadout.TEXT[29];
    public final String loadout_tutorialRequired = loadout.TEXT[30];
    public final String loadout_newPreset = loadout.TEXT[31];
    public final String loadout_changePreset = loadout.TEXT[32];
    public final String loadout_tutorialPreset = loadout.TEXT[33];
    public final String loadout_duplicatePreset = loadout.TEXT[34];
    // Menu
    public final String menu_editor = menu.TEXT[0];
    public final String menu_card = menu.TEXT[1];
    public final String menu_cardDesc = menu.TEXT[2];
    public final String menu_relic = menu.TEXT[3];
    public final String menu_relicDesc = menu.TEXT[4];
    public final String menu_potion = menu.TEXT[5];
    public final String menu_potionDesc = menu.TEXT[6];
    public final String menu_blight = menu.TEXT[7];
    public final String menu_blightDesc = menu.TEXT[8];
    public final String menu_power = menu.TEXT[9];
    public final String menu_powerDesc = menu.TEXT[10];
    public final String menu_orb = menu.TEXT[11];
    public final String menu_orbDesc = menu.TEXT[12];
    public final String menu_augmentLibrary = menu.TEXT[13];
    public final String menu_augmentLibraryDesc = menu.TEXT[14];
    public final String menu_augmentCreator = menu.TEXT[15];
    public final String menu_augmentCreatorDesc = menu.TEXT[16];
    // Misc
    public final String misc_viewAugments = misc.TEXT[0];
    public final String misc_viewAugmentsDescription = misc.TEXT[1];
    public final String misc_rightClickLearnMore = misc.TEXT[2];
    public final String misc_customCards = misc.TEXT[3];
    public final String misc_customCardsDesc = misc.TEXT[4];
    public final String misc_fabricate = misc.TEXT[5];
    public final String misc_leftClick = misc.TEXT[6];
    public final String misc_rightClick = misc.TEXT[7];
    public final String misc_tempPowerPrefix = misc.TEXT[8];
    public final String misc_edit = misc.TEXT[9];
    public final String misc_customRelics = misc.TEXT[10];
    public final String misc_customRelicsDesc = misc.TEXT[11];
    public final String misc_customPotions = misc.TEXT[12];
    public final String misc_customPotionsDesc = misc.TEXT[13];
    public final String misc_customBlights = misc.TEXT[14];
    public final String misc_customBlightsDesc = misc.TEXT[15];
    public final String misc_customLoadout = misc.TEXT[16];
    public final String misc_customLoadoutDesc = misc.TEXT[17];
    public final String misc_customAugment = misc.TEXT[18];
    public final String misc_customAugmentDesc = misc.TEXT[19];
    public final String misc_replaces = misc.TEXT[20];
    // Options
    public final String options_cropCardImages = options.TEXT[0];
    public final String options_displayCardTagDescription = options.TEXT[1];
    public final String options_expandAbbreviatedEffects = options.TEXT[2];
    public final String options_removeLineBreaks = options.TEXT[3];
    public final String options_vanillaCustomRunMenu = options.TEXT[4];
    public final String options_showEstimatedDamage = options.TEXT[5];
    public final String options_showFormulaDisplay = options.TEXT[6];
    public final String options_showUpgradeToggle = options.TEXT[7];
    public final String options_hideIrrelevantAffinities = options.TEXT[8];
    public final String options_enableCustomCards = options.TEXT[9];
    public final String options_enableCustomRelics = options.TEXT[10];
    public final String options_enableCustomPotions = options.TEXT[11];
    public final String options_enableCustomBlights = options.TEXT[12];
    public final String options_lowVRAM = options.TEXT[13];
    public final String options_madnessReplacements = options.TEXT[14];
    public final String options_fabricatePopup = options.TEXT[15];
    public final String options_alwaysPCLCard = options.TEXT[16];
    public final String options_showCardTarget = options.TEXT[17];
    public final String options_enableAugments = options.TEXT[18];
    public final String options_augmentChance = options.TEXT[19];
    public final String options_cardTextClip = options.TEXT[20];
    // Option Descriptions
    public final String optionDesc_cropCardImages = optionDesc.TEXT[0];
    public final String optionDesc_displayCardTagDescription = optionDesc.TEXT[1];
    public final String optionDesc_expandAbbreviatedEffects = optionDesc.TEXT[2];
    public final String optionDesc_removeLineBreaks = optionDesc.TEXT[3];
    public final String optionDesc_vanillaCustomRunMenu = optionDesc.TEXT[4];
    public final String optionDesc_showEstimatedDamage = optionDesc.TEXT[5];
    public final String optionDesc_showFormulaDisplay = optionDesc.TEXT[6];
    public final String optionDesc_showUpgradeToggle = options.TEXT[7];
    public final String optionDesc_hideIrrelevantAffinities = optionDesc.TEXT[8];
    public final String optionDesc_onlyNewRuns = optionDesc.TEXT[9];
    public final String optionDesc_lowVRAM = optionDesc.TEXT[10];
    public final String optionDesc_madnessReplacements = optionDesc.TEXT[11];
    public final String optionDesc_fabricatePopup = optionDesc.TEXT[12];
    public final String optionDesc_alwaysPCLCard = optionDesc.TEXT[13];
    public final String optionDesc_showCardTarget = optionDesc.TEXT[14];
    public final String optionDesc_enableAugments = optionDesc.TEXT[15];
    public final String optionDesc_augmentChance = optionDesc.TEXT[16];
    public final String optionDesc_cardTextClip = optionDesc.TEXT[17];
    // Power
    public final String power_turnBehavior = power.TEXT[0];
    public final String power_priority = power.TEXT[1];
    public final String power_common = power.TEXT[2];
    public final String power_neutral = power.TEXT[3];
    public final String power_permanent = power.TEXT[4];
    public final String power_turnBased = power.TEXT[5];
    public final String power_turnBasedNext = power.TEXT[6];
    public final String power_singleTurn = power.TEXT[7];
    public final String power_singleTurnNext = power.TEXT[8];
    public final String power_plated = power.TEXT[9];
    public final String power_instant = power.TEXT[10];
    public final String power_custom = power.TEXT[11];
    public final String power_permanentDesc = power.TEXT[12];
    public final String power_turnBasedDesc = power.TEXT[13];
    public final String power_turnBasedNextDesc = power.TEXT[14];
    public final String power_singleTurnDesc = power.TEXT[15];
    public final String power_singleTurnNextDesc = power.TEXT[16];
    public final String power_platedDesc = power.TEXT[17];
    public final String power_instantDesc = power.TEXT[18];
    public final String power_lastsForX = power.TEXT[19];
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
    public final String sui_core = seriesui.TEXT[1];
    public final String sui_selected = seriesui.TEXT[2];
    public final String sui_unlocked = seriesui.TEXT[3];
    public final String sui_removeFromPool = seriesui.TEXT[4];
    public final String sui_addToPool = seriesui.TEXT[5];
    public final String sui_resetBan = seriesui.TEXT[6];
    public final String sui_viewPool = seriesui.TEXT[7];
    public final String sui_totalCards = seriesui.TEXT[8];
    public final String sui_instructions1 = seriesui.TEXT[9];
    public final String sui_instructions2 = seriesui.TEXT[10];
    public final String sui_viewPoolInstructions = seriesui.TEXT[11];
    public final String sui_characterCards = seriesui.TEXT[12];
    public final String sui_resetPool = seriesui.TEXT[13];
    public final String sui_resetPoolDesc = seriesui.TEXT[14];
    public final String sui_resetBanDesc = seriesui.TEXT[15];
    public final String sui_selectAll = seriesui.TEXT[16];
    public final String sui_deselectAll = seriesui.TEXT[17];
    public final String sui_selectRandom = seriesui.TEXT[18];
    public final String sui_showCardPool = seriesui.TEXT[19];
    public final String sui_showColorless = seriesui.TEXT[20];
    public final String sui_showColorlessInfo = seriesui.TEXT[21];
    public final String sui_relicPool = seriesui.TEXT[22];
    public final String sui_relicPoolInfo = seriesui.TEXT[23];
    public final String sui_totalInstructions = seriesui.TEXT[24];
    public final String sui_importFromFile = seriesui.TEXT[25];
    public final String sui_importFromFileInstructions = seriesui.TEXT[26];
    public final String sui_importFromFileLoadout = seriesui.TEXT[27];
    public final String sui_exportToFileLoadout = seriesui.TEXT[28];
    // Single View Card Popup
    public final String scp_variant = scp.TEXT[0];
    public final String scp_changeVariant = scp.TEXT[1];
    public final String scp_changeVariantTooltipPermanent = scp.TEXT[2];
    public final String scp_changeVariantTooltipAlways = scp.TEXT[3];
    public final String scp_currentUpgrades = scp.TEXT[4];
    public final String scp_maxUpgrades = scp.TEXT[5];
    public final String scp_currentCopies = scp.TEXT[6];
    public final String scp_maxCopies = scp.TEXT[7];
    public final String scp_artAuthor = scp.TEXT[8];
    public final String scp_emptyAugment = scp.TEXT[9];
    public final String scp_viewAugments = scp.TEXT[10];
    public final String scp_viewTooltips = scp.TEXT[11];
    public final String scp_clickToSlot = scp.TEXT[12];
    public final String scp_clickToRemove = scp.TEXT[13];
    public final String scp_cannotRemove = scp.TEXT[14];
    public final String scp_noAugments = scp.TEXT[15];
    public final String scp_tooltipInfo = scp.TEXT[16];
    // Subjects
    public final String subjects_allyN = subjects.TEXT[0];
    public final String subjects_blightN = subjects.TEXT[1];
    public final String subjects_cardN = subjects.TEXT[2];
    public final String subjects_characterN = subjects.TEXT[3];
    public final String subjects_enemyN = subjects.TEXT[4];
    public final String subjects_potionN = subjects.TEXT[5];
    public final String subjects_relicN = subjects.TEXT[6];
    public final String subjects_cost = subjects.TEXT[7];
    public final String subjects_infinite = subjects.TEXT[8];
    public final String subjects_maxX = subjects.TEXT[9];
    public final String subjects_minX = subjects.TEXT[10];
    public final String subjects_other = subjects.TEXT[11];
    public final String subjects_xCount = subjects.TEXT[12];
    public final String subjects_allX = subjects.TEXT[13];
    public final String subjects_all = subjects.TEXT[14];
    public final String subjects_ally = subjects.TEXT[15];
    public final String subjects_amount = subjects.TEXT[16];
    public final String subjects_any = subjects.TEXT[17];
    public final String subjects_anyX = subjects.TEXT[18];
    public final String subjects_anyone = subjects.TEXT[19];
    public final String subjects_attacks = subjects.TEXT[20];
    public final String subjects_blight = subjects.TEXT[21];
    public final String subjects_bonus = subjects.TEXT[22];
    public final String subjects_card = subjects.TEXT[23];
    public final String subjects_character = subjects.TEXT[24];
    public final String subjects_copiesOfX = subjects.TEXT[25];
    public final String subjects_damage = subjects.TEXT[26];
    public final String subjects_distinctX = subjects.TEXT[27];
    public final String subjects_effectBonus = subjects.TEXT[28];
    public final String subjects_enemy = subjects.TEXT[29];
    public final String subjects_everyone = subjects.TEXT[30];
    public final String subjects_exactlyX = subjects.TEXT[31];
    public final String subjects_fromX = subjects.TEXT[32];
    public final String subjects_hits = subjects.TEXT[33];
    public final String subjects_intendedX = subjects.TEXT[34];
    public final String subjects_intent = subjects.TEXT[35];
    public final String subjects_lostX = subjects.TEXT[36];
    public final String subjects_missingX = subjects.TEXT[37];
    public final String subjects_nonX = subjects.TEXT[38];
    public final String subjects_ofX = subjects.TEXT[39];
    public final String subjects_permanentlyX = subjects.TEXT[40];
    public final String subjects_playingXWithY = subjects.TEXT[41];
    public final String subjects_potion = subjects.TEXT[42];
    public final String subjects_randomX = subjects.TEXT[43];
    public final String subjects_randomlyX = subjects.TEXT[44];
    public final String subjects_relic = subjects.TEXT[45];
    public final String subjects_shuffle = subjects.TEXT[46];
    public final String subjects_shuffleYourDeck = subjects.TEXT[47];
    public final String subjects_bottomOfX = subjects.TEXT[48];
    public final String subjects_leftmostX = subjects.TEXT[49];
    public final String subjects_rightmostX = subjects.TEXT[50];
    public final String subjects_target = subjects.TEXT[51];
    public final String subjects_topOfX = subjects.TEXT[52];
    public final String subjects_theirX = subjects.TEXT[53];
    public final String subjects_themX = subjects.TEXT[54];
    public final String subjects_theyX = subjects.TEXT[55];
    public final String subjects_thisX = subjects.TEXT[56];
    public final String subjects_this = subjects.TEXT[57];
    public final String subjects_turnCount = subjects.TEXT[58];
    public final String subjects_unblockedX = subjects.TEXT[59];
    public final String subjects_upToX = subjects.TEXT[60];
    public final String subjects_x = subjects.TEXT[61];
    public final String subjects_you = subjects.TEXT[62];
    public final String subjects_yourFirstX = subjects.TEXT[63];
    public final String subjects_yourX = subjects.TEXT[64];
    public final String subjects_xOfY = subjects.TEXT[65];
    public final String subjects_xOnY = subjects.TEXT[66];
    public final String subjects_xOnYInZ = subjects.TEXT[67];
    public final String subjects_xOrLessY = subjects.TEXT[68];
    public final String subjects_xThisCombat = subjects.TEXT[69];
    public final String subjects_xThisTurn = subjects.TEXT[70];
    public final String subjects_xTimes = subjects.TEXT[71];
    public final String subjects_xUntilY = subjects.TEXT[72];
    public final String subjects_xWithY = subjects.TEXT[73];
    public final String subjects_xCost = subjects.TEXT[74];
    // Tutorial
    public final String tutorial_learnMore = tutorial.TEXT[0];
    public final String tutorial_tagTutorial = tutorial.TEXT[1];
    public final String tutorial_affinityTutorial = tutorial.TEXT[2];
    public final String tutorial_summonTutorial1 = tutorial.TEXT[3];
    public final String tutorial_summonTutorial2 = tutorial.TEXT[4];
    public final String tutorial_summonTutorial3 = tutorial.TEXT[5];
    public final String tutorial_summonTutorial4 = tutorial.TEXT[6];
    public final String tutorial_summonTutorial5 = tutorial.TEXT[7];
    public final String tutorial_summonTutorial6 = EUIUtils.format(tutorial.TEXT[8], SummonPool.BASE_DAMAGE_BONUS);
    public final String tutorial_summonTutorial7 = tutorial.TEXT[9];
    public final String tutorial_summonTutorial8 = tutorial.TEXT[10];
    public final String tutorial_augmentTutorial1 = tutorial.TEXT[11];
    public final String tutorial_augmentTutorial2 = tutorial.TEXT[12];
    public final String tutorial_augmentTutorial3 = tutorial.TEXT[13];
    public final String tutorial_augmentTutorial4 = tutorial.TEXT[14];
    public final String tutorial_augmentTutorial5 = tutorial.TEXT[15];
    public final String tutorial_augmentTutorial6 = tutorial.TEXT[16];
    public final String tutorial_tutorialReplay = tutorial.TEXT[17];
    public final String tutorial_tutorialNextStep = tutorial.TEXT[18];
    public final String tutorial_tutorialComplete = tutorial.TEXT[19];
    public final String tutorial_tutorialStepHeader = tutorial.TEXT[20];
    public final String tutorial_tutorialCompleteHeader = tutorial.TEXT[21];
    public final String tutorial_tour = tutorial.TEXT[22];
    public final String tutorial_tourDesc = tutorial.TEXT[23];

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
        return joinWith(PGR.core.strings::cond_xOrY, values);
    }

    public static <T> String joinWithOr(FuncT1<String, T> valueToStr, List<T> values) {
        return joinWith(PGR.core.strings::cond_xOrY, valueToStr, values);
    }

    public static String joinWithOr(String... values) {
        return joinWith(PGR.core.strings::cond_xOrY, values);
    }

    @SafeVarargs
    public static <T> String joinWithOr(FuncT1<String, T> valueToStr, T... values) {
        return joinWith(PGR.core.strings::cond_xOrY, valueToStr, values);
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
        return EUITextHelper.parseLogicString(EUIUtils.format(tip, evaluated));
    }

    public static String pluralForce(String tip) {
        return EUITextHelper.parseLogicString(EUIUtils.format(tip, 2));
    }

    public static String present(Object obj) {
        return obj instanceof EUIKeywordTooltip ? ((EUIKeywordTooltip) obj).present() : EUIRM.strings.present(obj);
    }

    public static String rightClick(Object desc) {
        return "{#o:" + PGR.core.strings.misc_rightClick + "}: " + desc;
    }

    public static String singularForce(String tip) {
        return EUITextHelper.parseLogicString(EUIUtils.format(tip, 1));
    }

    private String actFmt(int index, Object... objects) {
        return EUIUtils.format(actions.TEXT[index], objects);
    }

    private String actFmt(int index) {
        return actions.TEXT[index];
    }

    // @Formatter: Off

    // Actions functions
    public final String act_addAmountToPile(Object amount, Object desc2, Object pile) {
        return act_addTo(EUIRM.strings.numNoun(amount, desc2), pile);
    }
    public final String act_applyAmountX(Object amount, Object power) {
        return act_applyX(EUIRM.strings.numNoun(amount, power));
    }
    public final String act_applyAmountXToTarget(Object amount, Object power, Object target) {
        return act_applyXToTarget(EUIRM.strings.numNoun(amount, power), target);
    }
    public final String act_channel(Object subject) {
        return act_generic2(PGR.core.tooltips.channel.title, subject);
    }
    public final String act_channelX(Object amount, Object subject) {
        return act_generic3(PGR.core.tooltips.channel.title, amount, subject);
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
    public final String act_generic2(Object verb, Object subject) {
        return EUIRM.strings.verbNoun(verb, subject);
    }
    public final String act_generic3(Object verb, Object adj, Object subject) {
        return EUIRM.strings.verbAdjNoun(verb, adj, subject);
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
    public final String act_obtain(Object card) {
        return act_generic2(PGR.core.tooltips.obtain.title, card);
    }
    public final String act_obtainAmount(Object amount, Object card) {
        return act_generic3(PGR.core.tooltips.obtain.title, amount, card);
    }
    public final String act_pay(Object amount, Object power) {
        return act_generic3(PGR.core.tooltips.pay.title, amount, power);
    }
    public final String act_play(Object amount) {
        return EUIRM.strings.verbNoun(PGR.core.tooltips.play.title, amount);
    }
    public final String act_playXTimes(Object subject, Object amount) {
        return act_genericTimes(PGR.core.tooltips.play.title, subject, amount);
    }
    public final String act_reduceCooldown(Object target, Object amount) {
        return act_reducePropertyBy(PGR.core.tooltips.cooldown.title, target, amount);
    }
    public final String act_retain(Object amount, Object subject) {
        return EUIRM.strings.verbAdjNoun(PGR.core.tooltips.retain.title, amount, subject);
    }
    public final String act_retain(Object amount) {
        return act_generic2(PGR.core.tooltips.retain.title, amount);
    }
    public final String act_spread(Object subject, Object target) {
        return act_zOn(PGR.core.tooltips.spread.title, subject, target);
    }
    public final String act_spreadAmount(Object amount, Object subject, Object target) {
        return act_zOnAmount(PGR.core.tooltips.spread.title, amount, subject, target);
    }
    public final String act_stun(Object target) {
        return act_generic2(PGR.core.tooltips.stun.title, target);
    }
    public final String act_takeDamage(Object amount) {
        return act_take(amount, subjects_damage);
    }
    public final String act_targetTakesDamage(Object target, Object ordinal, Object amount) {
        return act_zTakes(target, ordinal, amount, subjects_damage);
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
    public final String act_withdraw(Object subject) {
        return act_generic2(PGR.core.tooltips.withdraw.title, subject);
    }

    public final String act_activate(Object desc1) {
        return actFmt(0, desc1);
    }
    public final String act_addTo(Object desc1, Object pile) {
        return actFmt(1, desc1, pile);
    }
    public final String act_applyXToTarget(Object power, Object target) {
        return actFmt(2, power, target);
    }
    public final String act_applyX(Object power) {
        return actFmt(3, power);
    }
    public final String act_changeXToY(Object subject, Object form) {
        return actFmt(4, subject, form);
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
    public final String act_disable(Object power) {
        return actFmt(10, power);
    }
    public final String act_doThis(Object times) {
        return actFmt(11, times);
    }
    public final String act_doThisFor(Object target) {
        return actFmt(12, target);
    }
    public final String act_enterStance(Object stance) {
        return actFmt(13, stance);
    }
    public final String act_exitStance() {
        return actFmt(14);
    }
    public final String act_gainAmount(Object amount, Object power) {
        return actFmt(15, amount, power);
    }
    public final String act_gainOrdinal(Object ordinal, Object power) {
        return actFmt(16, ordinal, power);
    }
    public final String act_giveTarget(Object target, Object power) {
        return actFmt(17, target, power);
    }
    public final String act_giveTargetAmount(Object target, Object amount, Object power) {
        return actFmt(18, target, amount, power);
    }
    public final String act_has(Object amount) {
        return actFmt(19, amount);
    }
    public final String act_hasAmount(Object amount, Object power) {
        return actFmt(20, amount, power);
    }
    public final String act_haveObject(Object amount, Object power) {
        return actFmt(21, amount, power);
    }
    public final String act_increaseBy(Object power, Object amount) {
        return actFmt(22, power, amount);
    }
    public final String act_increasePropertyBy(Object property, Object target, Object amount) {
        return actFmt(23, property, target, amount);
    }
    public final String act_increasePropertyFromBy(Object property, Object target, Object source, Object amount) {
        return actFmt(24, property, target, source, amount);
    }
    public final String act_lose(Object power) {
        return actFmt(25, power);
    }
    public final String act_loseAmount(Object amount, Object power) {
        return actFmt(26, amount, power);
    }
    public final String act_loseOrdinal(Object ordinal, Object power) {
        return actFmt(27, ordinal, power);
    }
    public final String act_move(Object amount, Object target) {
        return actFmt(28, amount, target);
    }
    public final String act_moveTo(Object amount, Object dest, Object target) {
        return actFmt(29, amount, dest, target);
    }
    public final String act_multiplyBy(Object power, Object amount) {
        return actFmt(30, power, amount);
    }
    public final String act_multiplyPropertyBy(Object property, Object target, Object amount) {
        return actFmt(31, property, target, amount);
    }
    public final String act_multiplyPropertyFromBy(Object property, Object target, Object source, Object amount) {
        return actFmt(32, property, target, source, amount);
    }
    public final String act_reduceBy(Object power, Object amount) {
        return actFmt(33, power, amount);
    }
    public final String act_reducePropertyBy(Object property, Object target, Object amount) {
        return actFmt(34, property, target, amount);
    }
    public final String act_reducePropertyFromBy(Object property, Object target, Object source, Object amount) {
        return actFmt(35, property, target, source, amount);
    }
    public final String act_remove(Object item) {
        return actFmt(36, item);
    }
    public final String act_removeFrom(Object item, Object target) {
        return actFmt(37, item, target);
    }
    public final String act_removeFromPlace(Object item, Object target, Object place) {
        return actFmt(38, item, target, place);
    }
    public final String act_removeInPlace(Object item, Object target, Object place) {
        return actFmt(39, item, target, place);
    }
    public final String act_select(Object amount) {
        return actFmt(40, amount);
    }
    public final String act_setOf(Object item, Object target, Object affinity9) {
        return actFmt(41, item, target, affinity9);
    }
    public final String act_setOfFrom(Object item, Object target, Object place, Object affinity9) {
        return actFmt(42, item, target, place, affinity9);
    }
    public final String act_setTo(Object item, Object affinity) {
        return actFmt(43, item, affinity);
    }
    public final String act_setTheLast(Object amount, Object item, Object affinity) {
        return actFmt(44, amount, item, affinity);
    }
    public final String act_skipTurn() {
        return actFmt(45);
    }
    public final String act_stealFrom(Object amount, Object item, Object target) {
        return actFmt(46, amount, item, target);
    }
    public final String act_stealX(Object amount, Object power) {
        return actFmt(47, amount, power);
    }
    public final String act_take(Object amount, Object damage) {
        return actFmt(48, amount, damage);
    }
    public final String act_transform(Object subject, Object target) {
        return actFmt(49, subject, target);
    }
    public final String act_use(Object target) {
        return actFmt(50, target);
    }
    public final String act_zCannot(Object target, Object action, Object object) {
        return actFmt(51, target, action, object);
    }
    public final String act_zCosts(Object object, Object ordinal, Object amount) {
        return actFmt(52, object, ordinal, amount);
    }
    public final String act_zGainsBonus(Object object, Object amount, Object bonus) {
        return actFmt(53, object, amount, bonus);
    }
    public final String act_zHas(Object object, Object amount) {
        return actFmt(54, object, amount);
    }
    public final String act_zLoses(Object object, Object ordinal, Object amount, Object bonus) {
        return actFmt(55, object, ordinal, amount, bonus);
    }
    public final String act_zTakes(Object object, Object ordinal, Object amount, Object bonus) {
        return actFmt(56, object, ordinal, amount, bonus);
    }
    public final String act_zOn(Object action, Object object, Object target) {
        return actFmt(57, action, object, target);
    }
    public final String act_zOnAmount(Object action, Object amount, Object object, Object target) {
        return actFmt(58, action, amount, object, target);
    }
    public final String act_zXFromY(Object verb, Object amount, Object subject, Object target) {
        return actFmt(59, verb, amount, subject, target);
    }
    public final String act_zXFromYToZ(Object verb, Object amount, Object subject, Object dest, Object target) {
        return actFmt(60, verb, amount, subject, dest, target);
    }
    public final String act_zToX(Object action, Object object, Object target) {
        return actFmt(61, action, object, target);
    }
    public final String act_zXToY(Object verb, Object amount, Object subject, Object target) {
        return actFmt(62, verb, amount, subject, target);
    }
    public final String act_exitX(String stance) {
        return actFmt(63, stance);
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
    public final String cond_bonusIf(Object desc6, Object desc7) {
        return EUIRM.strings.generic2(desc6, cond_ifX(desc7));
    }
    public final String cond_ifTargetIs(Object target, Object ordinal, Object subject) {
        return cond_ifX(cond_xIsY(target, ordinal, subject));
    }
    public final String cond_ifYouDidThisCombat(Object verb, Object obj) {
        return subjects_thisCombat(cond_ifTargetDidX(subjects_you, verb, obj));
    }
    public final String cond_ifYouDidThisTurn(Object verb, Object obj) {
        return subjects_thisTurn(cond_ifTargetDidX(subjects_you, verb, obj));
    }
    public final String cond_perThisCombat(Object subject, Object desc4, Object target) {
        return subjects_thisCombat(cond_xPerYZ(subject, desc4, target));
    }
    public final String cond_perThisTurn(Object subject, Object desc4, Object target) {
        return subjects_thisTurn(cond_xPerYZ(subject, desc4, target));
    }

    public final String cond_aObject(Object subject, Object target) {
        return condFmt(0, subject, target);
    }
    public final String cond_aObjectIs(Object target, Object desc9) {
        return condFmt(1, target, desc9);
    }
    public final String cond_aObjectIsOn(Object target, Object desc9, Object place) {
        return condFmt(2, target, desc9, place);
    }
    public final String cond_any(Object desc4) {
        return condFmt(3, desc4);
    }
    public final String cond_atEndOfCombat() {
        return condFmt(4);
    }
    public final String cond_atStartOfCombat() {
        return condFmt(5);
    }
    public final String cond_atEndOfTurn() {
        return condFmt(6);
    }
    public final String cond_atStartOfTurn() {
        return condFmt(7);
    }
    public final String cond_doX(Object subject) {
        return condFmt(8, subject);
    }
    public final String cond_doForEach() {
        return condFmt(9);
    }
    public final String cond_everyXTimesY(Object times, Object subject) {
        return condFmt(10, times, subject);
    }
    public final String cond_everyXTimesYThisTurn(Object times, Object subject) {
        return condFmt(11, times, subject);
    }
    public final String cond_forTurns(Object subject) {
        return condFmt(12, subject);
    }
    public final String cond_ifLast(Object obj, Object verb, Object amount, Object cond) {
        return condFmt(13, obj, verb, amount, cond);
    }
    public final String cond_ifThere(Object number, Object target) {
        return condFmt(14, number, target);
    }
    public final String cond_ifTargetHas(Object target, Object ordinal, Object desc) {
        return condFmt(15, target, ordinal, desc);
    }
    public final String cond_ifTargetLost(Object target, Object desc) {
        return condFmt(16, target, desc);
    }
    public final String cond_ifTargetTook(Object target, Object desc) {
        return condFmt(17, target, desc);
    }
    public final String cond_ifTargetWouldDie(Object target) {
        return condFmt(18, target);
    }
    public final String cond_ifTargetDidX(Object target, Object subject, Object desc12) {
        return condFmt(19, target, subject, desc12);
    }
    public final String cond_ifXDid(Object subject, Object desc) {
        return condFmt(20, subject, desc);
    }
    public final String cond_ifX(Object subject) {
        return condFmt(21, subject);
    }
    public final String cond_inXAtTurnEnd(Object subject) {
        return condFmt(22, subject);
    }
    public final String cond_inXAtTurnStart(Object subject) {
        return condFmt(23, subject);
    }
    public final String cond_inTurns(Object subject) {
        return condFmt(24, subject);
    }
    public final String cond_levelItem(Object level, Object subject) {
        return condFmt(25, level, subject);
    }
    public final String cond_nextTurn() {
        return condFmt(26);
    }
    public final String cond_no(Object subject) {
        return condFmt(27, subject);
    }
    public final String cond_not(Object subject) {
        return condFmt(28, subject);
    }
    public final String cond_onGeneric(Object subject) {
        return condFmt(29, subject);
    }
    public final String cond_otherwise(Object subject) {
        return condFmt(30, subject);
    }
    public final String cond_when(Object subject) {
        return condFmt(31, subject);
    }
    public final String cond_whileIn(Object subject) {
        return condFmt(32, subject);
    }
    public final String cond_xAndY(Object subject, Object target) {
        return condFmt(33, subject, target);
    }
    public final String cond_xIsY(Object desc11, Object ordinal, Object desc12) {
        return condFmt(34, desc11, ordinal, desc12);
    }
    public final String cond_xOnYIsBroken(Object desc11, Object desc12) {
        return condFmt(35, desc11, desc12);
    }
    public final String cond_xOrY(Object desc11, Object desc12) {
        return condFmt(36, desc11, desc12);
    }
    public final String cond_xPerY(Object subject, Object per) {
        return condFmt(37, subject, per);
    }
    public final String cond_xPerYZ(Object desc11, Object desc12, Object desc13) {
        return condFmt(38, desc11, desc12, desc13);
    }
    public final String cond_xPerIn(Object desc11, Object desc12, Object desc13) {
        return condFmt(39, desc11, desc12, desc13);
    }
    public final String cond_timesPerCombat(Object desc10) {
        return condFmt(40, desc10);
    }
    public final String cond_timesPerTurn(Object desc10) {
        return condFmt(41, desc10);
    }
    public final String cond_xToY(Object desc10, Object desc11) {
        return condFmt(42, desc10, desc11);
    }
    public final String cond_xThenY(Object desc10, Object desc11) {
        return condFmt(43, desc10, desc11);
    }
    public final String cond_xConditional(Object desc10, Object desc11) {
        return condFmt(44, desc10, desc11);
    }
    public final String cond_passive() {
        return condFmt(45);
    }

    public final String csel_obtainBronzeAtAscension(int ascension) {
        return EUIUtils.format(charselect.TEXT[7], ascension);
    }

    public final String grid_cardsInPile(Object item, int amount) {
        return EUIUtils.format(grid_cardsInPile, item, amount);
    }
    public final String grid_chooseCards(Object amount) {
        return EUIUtils.format(grid_chooseCards, amount);
    }

    public final String loadout_cardsCount(Object value, Object req) {
        return EUIUtils.format(loadout_cardsCount, value, req);
    }
    public final String loadout_hindranceValue(Object value) {
        return EUIUtils.format(loadout_hindranceValue, value);
    }
    public final String loadout_totalValue(Object value, Object max) {
        return EUIUtils.format(loadout_totalValue, value, max);
    }

    public final String rewards_goldBonus(Object amount) {
        return EUIUtils.format(rewards_goldbonusF1, amount);
    }
    public final String rewards_maxHPBonus(Object amount) {
        return EUIUtils.format(rewards_maxhpbonusF1, amount);
    }

    // Subject functions
    public final String subjects_allAllies() {
        return EUIUtils.format(subjects_allX, PCLCoreStrings.pluralForce(subjects_allyN));
    }
    public final String subjects_allAlliesAndEnemies() {
        return EUIUtils.format(subjects_allX, cond_xAndY(PCLCoreStrings.pluralForce(subjects_allyN), PCLCoreStrings.pluralForce(subjects_enemyN)));
    }
    public final String subjects_allAlliesOrEnemies() {
        return EUIUtils.format(subjects_allX, cond_xOrY(PCLCoreStrings.pluralForce(subjects_allyN), PCLCoreStrings.pluralForce(subjects_enemyN)));
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
    public final String subjects_anyAllyOrEnemy() {
        return EUIUtils.format(subjects_anyX, cond_xOrY(subjects_ally, subjects_enemy));
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
    public final String subjects_distinct(Object obj) {
        return EUIUtils.format(subjects_distinctX, obj);
    }
    public final String subjects_enemyWithX(Object obj) {
        return subjects_withX(subjects_enemy, obj);
    }
    public final String subjects_exactly(Object obj) {
        return subjects_withX(subjects_exactlyX, obj);
    }
    public final String subjects_from(Object place) {
        return EUIUtils.format(subjects_fromX, place);
    }
    public final String subjects_in(Object place) {
        return EUIUtils.format(subjects_fromX, place);
    }
    public final String subjects_intendedDamage(Object target) {
        return EUIUtils.format(subjects_intendedX, subjects_damage, target);
    }
    public final String subjects_lostX(Object object, Object target) {
        return EUIUtils.format(subjects_lostX, object, target);
    }
    public final String subjects_max(Object amount) {
        return EUIUtils.format(subjects_maxX, amount);
    }
    public final String subjects_min(Object amount) {
        return EUIUtils.format(subjects_minX, amount);
    }
    public final String subjects_missingX(Object object, Object target) {
        return EUIUtils.format(subjects_missingX, object, target);
    }
    public final String subjects_non(Object place) {
        return EUIUtils.format(subjects_nonX, place);
    }
    public final String subjects_ofX(Object amount) {
        return EUIUtils.format(subjects_ofX, amount);
    }
    public final String subjects_onAnyAlly(Object desc1) {
        return subjects_onTarget(desc1, subjects_ally);
    }
    public final String subjects_onAnyAllyOrEnemy(Object desc1) {
        return subjects_onTarget(desc1, cond_xOrY(subjects_ally, subjects_enemy));
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
    public final String subjects_onTheAlly(Object desc1) {
        return subjects_onTarget(desc1, subjects_ally);
    }
    public final String subjects_onTheEnemy(Object desc1) {
        return subjects_onTarget(desc1, subjects_enemy);
    }
    public final String subjects_onTheTarget(Object desc1) {
        return subjects_onTarget(desc1, subjects_target);
    }
    public final String subjects_onThis(Object desc1) {
        return subjects_onTarget(desc1, subjects_this);
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
    public final String subjects_thisCard() {
        return EUIUtils.format(subjects_thisX, subjects_card);
    }
    public final String subjects_thisCombat(String base) {
        return EUIUtils.format(subjects_xThisCombat, base);
    }
    public final String subjects_thisRelic() {
        return EUIUtils.format(subjects_thisX, subjects_relic);
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
    public final String subjects_untilX(Object obj, Object t) {
        return EUIUtils.format(subjects_xUntilY, obj, t);
    }
    public final String subjects_upToX(Object amount) {
        return EUIUtils.format(subjects_upToX, amount);
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
    public final String subjects_xOnYInZ(Object obj, Object t, Object pl) {
        return EUIUtils.format(subjects_xOnYInZ, obj, t, pl);
    }
    public final String subjects_xOrLess(Object obj) {
        return EUIUtils.format(subjects_xOrLessY, obj);
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

    public final String sui_totalCards(Object loadouts, Object color, Object total, Object req, Object color2, Object total2, Object req2) {
        return EUIUtils.format(sui_totalCards, loadouts, color, total, req, color2, total2, req2);
    }

    public final String sui_unlocked(Object amount, Object total) {
        return EUIUtils.format(sui_unlocked, amount, total);
    }

    // @Formatter: On
}