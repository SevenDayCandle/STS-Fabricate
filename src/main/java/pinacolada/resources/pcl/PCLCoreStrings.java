package pinacolada.resources.pcl;

import com.megacrit.cardcrawl.actions.common.DiscardAction;
import com.megacrit.cardcrawl.actions.common.ExhaustAction;
import com.megacrit.cardcrawl.actions.unique.GamblingChipAction;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.text.EUISmartText;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PCLStrings;
import pinacolada.resources.PGR;

import java.util.List;
import java.util.StringJoiner;

// TODO collapse classes
public class PCLCoreStrings extends PCLStrings
{
    public Rewards rewards;
    public Misc misc;
    public SeriesUI seriesUI;
    public CardEditor cardEditor;
    public CharacterSelect charSelect;
    public SeriesSelection seriesSelection;
    public SeriesSelectionButtons seriesSelectionButtons;
    public SingleCardPopupButtons singleCardPopupButtons;
    public Subjects subjects;
    public Actions actions;
    public Conditions conditions;
    public CardPile cardPile;
    public CardType cardType;
    public Options options;
    public Tutorial tutorial;
    public CardEditorTutorial cardEditorTutorial;
    public Combat combat;
    public Hotkeys hotkeys;
    public CardMods cardMods;
    public GridSelection gridSelection;

    public PCLCoreStrings(PCLResources<?,?,?> resources)
    {
        super(resources);
    }

    public void initialize()
    {
        misc = new Misc();
        rewards = new Rewards();
        seriesUI = new SeriesUI();
        cardEditor = new CardEditor();
        charSelect = new CharacterSelect();
        subjects = new Subjects();
        actions = new Actions(subjects);
        conditions = new Conditions(subjects);
        cardPile = new CardPile();
        cardType = new CardType();
        tutorial = new Tutorial();
        cardEditorTutorial = new CardEditorTutorial();
        seriesSelection = new SeriesSelection();
        seriesSelectionButtons = new SeriesSelectionButtons();
        singleCardPopupButtons = new SingleCardPopupButtons();
        hotkeys = new Hotkeys();
        combat = new Combat();
        cardMods = new CardMods();
        gridSelection = new GridSelection();
        options = new Options();
    }

    public class Rewards
    {
        private final UIStrings rewards = getUIStrings("Rewards");

        public final String rewardBreak = rewards.TEXT[0];
        public final String breakDescription = rewards.TEXT[1];
        public final String reroll = rewards.TEXT[2];
        public final String rerollDescription = rewards.TEXT[3];
        public final String maxhpbonusF1 = rewards.TEXT[4];
        public final String goldbonusF1 = rewards.TEXT[5];
        public final String commonUpgrade = rewards.TEXT[6];
        public final String rightClickPreview = rewards.TEXT[7];
        public final String potionSlot = rewards.TEXT[8];
        public final String orbSlot = rewards.TEXT[9];

        public final String maxHPBonus(int amount)
        {
            return EUIUtils.format(maxhpbonusF1, amount);
        }

        public final String goldBonus(int amount)
        {
            return EUIUtils.format(goldbonusF1, amount);
        }
    }

    public class SeriesUI
    {
        private final UIStrings seriesui = getUIStrings("SeriesUI");

        public final String seriesUI = seriesui.TEXT[0];
        public final String affinities = seriesui.TEXT[1];
        public final String core = seriesui.TEXT[2];
        public final String scalings = seriesui.TEXT[3];
    }

    public class CardEditor
    {
        private final UIStrings cardeditor = getUIStrings("CardEditor");

        public final String attributes = cardeditor.TEXT[0];
        public final String effects = cardeditor.TEXT[1];
        public final String tags = cardeditor.TEXT[2];
        public final String value = cardeditor.TEXT[3];
        public final String newCard = cardeditor.TEXT[4];
        public final String damage = cardeditor.TEXT[5];
        public final String block = cardeditor.TEXT[6];
        public final String magicNumber = cardeditor.TEXT[7];
        public final String secondaryNumber = cardeditor.TEXT[8];
        public final String hitCount = cardeditor.TEXT[9];
        public final String upgrades = cardeditor.TEXT[10];
        public final String cardTarget = cardeditor.TEXT[11];
        public final String attackType = cardeditor.TEXT[12];
        public final String attackEffect = cardeditor.TEXT[13];
        public final String condition = cardeditor.TEXT[14];
        public final String mainCondition = cardeditor.TEXT[15];
        public final String effect = cardeditor.TEXT[16];
        public final String effectX = cardeditor.TEXT[17];
        public final String powerX = cardeditor.TEXT[18];
        public final String modifier = cardeditor.TEXT[19];
        public final String trigger = cardeditor.TEXT[20];
        public final String not = cardeditor.TEXT[21];
        public final String addTo = cardeditor.TEXT[22];
        public final String orbs = cardeditor.TEXT[23];
        public final String powers = cardeditor.TEXT[24];
        public final String maxUpgrades = cardeditor.TEXT[25];
        public final String flags = cardeditor.TEXT[26];
        public final String editForm = cardeditor.TEXT[27];
        public final String turnDelay = cardeditor.TEXT[28];
        public final String extraValue = cardeditor.TEXT[29];
        public final String maxCopies = cardeditor.TEXT[30];
        public final String loadImage = cardeditor.TEXT[31];
        public final String loadFile = cardeditor.TEXT[32];
        public final String paste = cardeditor.TEXT[33];
        public final String addForm = cardeditor.TEXT[34];
        public final String removeForm = cardeditor.TEXT[35];
        public final String undo = cardeditor.TEXT[36];
        public final String custom = cardeditor.TEXT[37];
        public final String delegate = cardeditor.TEXT[38];
        public final String duplicate = cardeditor.TEXT[39];
        public final String delete = cardeditor.TEXT[40];
        public final String reloadCards = cardeditor.TEXT[41];
        public final String confirmDeletion = cardeditor.TEXT[42];
        public final String confirmDeletionDesc = cardeditor.TEXT[43];
        public final String customCards = cardeditor.TEXT[44];
        public final String customCardsDesc = cardeditor.TEXT[45];
        public final String primaryInfo = cardeditor.TEXT[46];
        public final String primaryInfoDesc = cardeditor.TEXT[47];
        public final String choices = cardeditor.TEXT[48];
        public final String ifElseCondition = cardeditor.TEXT[49];
        public final String orCondition = cardeditor.TEXT[50];
        public final String idSuffix = cardeditor.TEXT[51];
        public final String idSuffixWarning = cardeditor.TEXT[52];
        public final String duplicateToColor = cardeditor.TEXT[53];
        public final String duplicateToColorDesc = cardeditor.TEXT[54];
        public final String createRandom = cardeditor.TEXT[55];
        public final String importExisting = cardeditor.TEXT[56];
        public final String openFolder = cardeditor.TEXT[57];
        public final String exportCSV = cardeditor.TEXT[58];
        public final String random = cardeditor.TEXT[59];
        public final String required = cardeditor.TEXT[60];
        public final String origins = cardeditor.TEXT[61];
        public final String enable = cardeditor.TEXT[62];
        public final String existingCardImage = cardeditor.TEXT[63];
    }

    public class Misc
    {
        private final UIStrings misc = getUIStrings("Misc");

        public final String viewAugments = misc.TEXT[0];
        public final String viewAugmentsDescription = misc.TEXT[1];
        public final String viewAugmentsNone = misc.TEXT[2];
        public final String viewCardPoolSeries = misc.TEXT[3];
        public final String cardModeHeader = misc.TEXT[4];
        public final String simpleMode = misc.TEXT[5];
        public final String simpleModeDescription = misc.TEXT[6];
        public final String complexMode = misc.TEXT[7];
        public final String complexModeDescription = misc.TEXT[8];
        public final String allowCustomCards = misc.TEXT[9];
        public final String pcl = misc.TEXT[10];
        public final String leftClick = misc.TEXT[11];
        public final String rightClick = misc.TEXT[12];
    }

    public class Options
    {
        private final UIStrings options = getUIStrings("Options");

        public final String cropCardImages = options.TEXT[0];
        public final String displayCardTagDescription = options.TEXT[1];
        public final String enableEventsForOtherCharacters = options.TEXT[2];
        public final String enableRelicsForOtherCharacters = options.TEXT[3];
        public final String replaceCards = options.TEXT[4];
        public final String usePCLPowersForAll = options.TEXT[5];
        public final String hideIrrelevantAffinities = options.TEXT[6];
        public final String showFormulaDisplay = options.TEXT[7];
    }

    public class CharacterSelect
    {
        private final UIStrings charselect = getUIStrings("CharacterSelect");

        public final String leftText = charselect.TEXT[0];  // Starting Cards:
        public final String rightText = charselect.TEXT[1]; // Unlock
        public final String invalidLoadout = charselect.TEXT[3];
        public final String deckEditor = charselect.TEXT[5];
        public final String deckEditorInfo = charselect.TEXT[6];
        public final String seriesEditor = charselect.TEXT[8];
        public final String seriesEditorInfo = charselect.TEXT[9];
        public final String deckHeader = charselect.TEXT[10];
        public final String relicsHeader = charselect.TEXT[11];
        public final String attributesHeader = charselect.TEXT[12];
        public final String valueHeader = charselect.TEXT[13];
        public final String hindranceDescription = charselect.TEXT[18];
        public final String affinityDescription = charselect.TEXT[19];
        public final String unsavedChanges = charselect.TEXT[20];
        public final String clear = charselect.TEXT[21];
        public final String copyTo = charselect.TEXT[22];
        public final String copyFrom = charselect.TEXT[23];
        public final String export = charselect.TEXT[24];
        public final String ascensionGlyph = charselect.TEXT[25];
        public final String cardEditor = charselect.TEXT[26];
        public final String cardEditorInfo = charselect.TEXT[27];
        public final String cardEditorEnabled = charselect.TEXT[28];
        public final String cardEditorDisabled = charselect.TEXT[29];
        public final String cardEditorToggle = charselect.TEXT[30];

        public final String unlocksAtLevel(int unlockLevel, int currentLevel)
        {
            return EUIUtils.format(charselect.TEXT[2], unlockLevel, currentLevel);
        }

        public final String unlocksAtAscension(int ascension)
        {
            return EUIUtils.format(charselect.TEXT[4], ascension);
        }

        public final String obtainBronzeAtAscension(int ascension)
        {
            return EUIUtils.format(charselect.TEXT[7], ascension);
        }

        public final String hindranceValue(int value)
        {
            return EUIUtils.format(charselect.TEXT[14], value);
        }

        public final String affinityValue(int value)
        {
            return EUIUtils.format(charselect.TEXT[15], value);
        }

        public final String cardsCount(int value)
        {
            return EUIUtils.format(charselect.TEXT[16], value);
        }

        public final String totalValue(int value, int max)
        {
            return EUIUtils.format(charselect.TEXT[17], value, max);
        }
    }

    public class SeriesSelection
    {
        private final UIStrings seriesselect = getUIStrings("SeriesSelection");

        public final String selected = seriesselect.TEXT[0];
        public final String unlocked = seriesselect.TEXT[1];
        public final String removeFromPool = seriesselect.TEXT[2];
        public final String addToPool = seriesselect.TEXT[3];
        public final String viewPool = seriesselect.TEXT[4];
        public final String totalCards = seriesselect.TEXT[5];
        public final String instructions1 = seriesselect.TEXT[6];
        public final String instructions2 = seriesselect.TEXT[7];

        public final String selected(Object amount, Object total) {
            return EUIUtils.format(selected, amount, total);
        }

        public final String unlocked(Object amount, Object total) {
            return EUIUtils.format(unlocked, amount, total);
        }

        public final String totalCards(Object cardCount, Object req)
        {
            return EUIUtils.format(totalCards, cardCount, req);
        }
    }

    public class SeriesSelectionButtons
    {
        private final UIStrings seriesselectbuttons = getUIStrings("SeriesSelectionButtons");

        public final String cardsInPool = seriesselectbuttons.TEXT[0];
        public final String selectAll = seriesselectbuttons.TEXT[1];
        public final String deselectAll = seriesselectbuttons.TEXT[2];
        public final String selectRandom = seriesselectbuttons.TEXT[3];
        public final String showCardPool = seriesselectbuttons.TEXT[4];
        public final String save = seriesselectbuttons.TEXT[5];
        public final String enableExpansion = seriesselectbuttons.TEXT[6];
        public final String disableExpansion = seriesselectbuttons.TEXT[7];
        public final String allExpansionEnable = seriesselectbuttons.TEXT[8];
        public final String allExpansionDisable = seriesselectbuttons.TEXT[9];
        public final String cancel = seriesselectbuttons.TEXT[10];
        public final String showColorless = seriesselectbuttons.TEXT[11];

        public final String selectRandom(int cards)
        {
            return EUIUtils.format(seriesselectbuttons.TEXT[2], cards) ;
        }
    }

    public class SingleCardPopupButtons
    {
        private final UIStrings strings = getUIStrings("SingleCardPopupButtons");

        public final String variant = strings.TEXT[0];
        public final String changeVariant = strings.TEXT[1];
        public final String changeVariantTooltipPermanent = strings.TEXT[2];
        public final String changeVariantTooltipAlways = strings.TEXT[3];
        public final String currentCopies = strings.TEXT[4];
        public final String maxCopies = strings.TEXT[5];
        public final String artAuthor = strings.TEXT[6];
        public final String emptyAugment = strings.TEXT[7];
        public final String viewAugments = strings.TEXT[8];
        public final String viewTooltips = strings.TEXT[9];
        public final String clickToSlot = strings.TEXT[10];
        public final String clickToRemove = strings.TEXT[11];
        public final String cannotRemove = strings.TEXT[12];
    }

    public class Hotkeys
    {
        private final UIStrings hotkeys = getUIStrings("Hotkeys");

        public final String controlPileChange = hotkeys.TEXT[0];
        public final String controlPileSelect = hotkeys.TEXT[1];
        public final String rerollCurrent = hotkeys.TEXT[2];
        public final String toggleFormulaDisplay = hotkeys.TEXT[3];
        public final String viewAugments = hotkeys.TEXT[4];
    }

    public class Combat
    {
        private final UIStrings combat = getUIStrings("Combat");

        public final String current = combat.TEXT[0];
        public final String next = combat.TEXT[1];
        public final String uses = combat.TEXT[2];
        public final String rerolls = combat.TEXT[3];
        public final String controlPile = combat.TEXT[4];
        public final String controlPileDescription = combat.TEXT[5];
        public final String count = combat.TEXT[6];
        public final String effect = combat.TEXT[7];
        public final String nextLevelEffect = combat.TEXT[8];
        public final String active = combat.TEXT[9];
        public final String inactive = combat.TEXT[10];
        public final String disabled = combat.TEXT[11];
        public final String na = combat.TEXT[12];
        public final String eternalMeter = combat.TEXT[13];
        public final String eternalMeterAffinity = combat.TEXT[14];
        public final String eternalMeterCurrent = combat.TEXT[15];
        public final String eternalMeterGain = combat.TEXT[16];
        public final String eternalMeterSpend = combat.TEXT[17];
        public final String conjurerMeterDebuff = combat.TEXT[18];
        public final String conjurerMeterCost = combat.TEXT[19];
        public final String conjurerMeterDamage = combat.TEXT[20];
        public final String conjurerMeterSwitch = combat.TEXT[21];
        public final String conjurerMeterCombust = combat.TEXT[22];
        public final String conjurerMeterRedox = combat.TEXT[23];
        public final String conjurerMeterNextIntensity = combat.TEXT[24];
        public final String deciderMeterStacks = combat.TEXT[25];
        public final String deciderMeterStacksNextLevel = combat.TEXT[26];
        public final String dodged = combat.TEXT[27];

        public final String controlPileDescriptionFull(String keyName) {
            return EUIUtils.format(controlPileDescription, keyName);
        }

        public final String count(Object t, Object desc) {
            return headerString(EUIUtils.format(count, t), desc);
        }

        public final String effect(Object desc) {
            return headerString(effect, desc);
        }

        public final String nextLevelEffect(Object desc) {
            return headerString(nextLevelEffect, desc);
        }
    }

    public class CardMods
    {
        private final UIStrings cardmods = getUIStrings("CardMods");

        public final String handSize = cardmods.TEXT[0];
        public final String afterlifeMet = cardmods.TEXT[1];
        public final String afterlifeRequirement = cardmods.TEXT[2];
        public final String respecLivingPicture = cardmods.TEXT[3];
        public final String respecLivingPictureLocked = cardmods.TEXT[4];
        public final String respecLivingPictureDescription = cardmods.TEXT[5];
        public final String kirby = cardmods.TEXT[6];
        public final String kirbyDescription = cardmods.TEXT[7];
        public final String tempPowerPrefix = cardmods.TEXT[8];
        public final String requirement = cardmods.TEXT[9];
    }

    public class CardType
    {
        private final UIStrings cardtype = getUIStrings("CardType");

        public final String none = cardtype.TEXT[0];
        public final String allAlly = cardtype.TEXT[1];
        public final String allCharacter = cardtype.TEXT[2];
        public final String allEnemy = cardtype.TEXT[3];
        public final String any = cardtype.TEXT[4];
        public final String randomAlly = cardtype.TEXT[5];
        public final String randomEnemy = cardtype.TEXT[6];
        public final String self = cardtype.TEXT[7];
        public final String singleAlly = cardtype.TEXT[8];
        public final String singleTarget = cardtype.TEXT[9];
        public final String team = cardtype.TEXT[10];
        public final String legendary = cardtype.TEXT[11];
        public final String secretRare = cardtype.TEXT[12];

        public final String tagAoE = cardtype.EXTRA_TEXT[0];
        public final String tagRandom = cardtype.EXTRA_TEXT[1];
    }

    public class Subjects
    {
        private final UIStrings subjects = getUIStrings("Subjects");

        public final String allyN = subjects.TEXT[0];
        public final String cardN = subjects.TEXT[1];
        public final String characterN = subjects.TEXT[2];
        public final String enemyN = subjects.TEXT[3];
        public final String cost = subjects.TEXT[4];
        public final String infinite = subjects.TEXT[5];
        public final String maxX = subjects.TEXT[6];
        public final String minX = subjects.TEXT[7];
        public final String other = subjects.TEXT[8];
        public final String xCount = subjects.TEXT[9];
        public final String allX = subjects.TEXT[10];
        public final String all = subjects.TEXT[11];
        public final String ally = subjects.TEXT[12];
        public final String anyPile = subjects.TEXT[13];
        public final String anyX = subjects.TEXT[14];
        public final String anyone = subjects.TEXT[15];
        public final String attacking = subjects.TEXT[16];
        public final String buffing = subjects.TEXT[17];
        public final String card = subjects.TEXT[18];
        public final String character = subjects.TEXT[19];
        public final String copiesOfX = subjects.TEXT[20];
        public final String damage = subjects.TEXT[21];
        public final String debuffing = subjects.TEXT[22];
        public final String defending = subjects.TEXT[23];
        public final String effectBonus = subjects.TEXT[24];
        public final String enemy = subjects.TEXT[25];
        public final String fromX = subjects.TEXT[26];
        public final String hits = subjects.TEXT[27];
        public final String inX = subjects.TEXT[28];
        public final String ofX = subjects.TEXT[29];
        public final String permanentlyX = subjects.TEXT[30];
        public final String playingXWithY = subjects.TEXT[31];
        public final String randomX = subjects.TEXT[32];
        public final String randomlyX = subjects.TEXT[33];
        public final String shuffleYourDeck = subjects.TEXT[34];
        public final String bottomOfX = subjects.TEXT[35];
        public final String leftmostX = subjects.TEXT[36];
        public final String rightmostX = subjects.TEXT[37];
        public final String target = subjects.TEXT[38];
        public final String topOfX = subjects.TEXT[39];
        public final String theirX = subjects.TEXT[40];
        public final String them = subjects.TEXT[41];
        public final String thisObj = subjects.TEXT[42];
        public final String thisX = subjects.TEXT[43];
        public final String x = subjects.TEXT[44];
        public final String you = subjects.TEXT[45];
        public final String yourFirstX = subjects.TEXT[46];
        public final String yourX = subjects.TEXT[47];
        public final String xOfY = subjects.TEXT[48];
        public final String xOnY = subjects.TEXT[49];
        public final String xThisTurn = subjects.TEXT[50];
        public final String xTimes = subjects.TEXT[51];
        public final String xWithY = subjects.TEXT[52];
        public final String xCost = subjects.TEXT[53];
        public final String xBonus = subjects.TEXT[54];

        public final String allX(Object amount) {
            return EUIUtils.format(allX, amount);
        }
        public final String allyWithX(Object obj) {
            return withX(ally, obj);
        }
        public final String withX(Object obj, Object t) {
            return EUIUtils.format(xWithY, obj, t);
        }
        public final String anyX(Object amount) {
            return EUIUtils.format(anyX, amount);
        }
        public final String anyAlly() {
            return EUIUtils.format(anyX, ally);
        }
        public final String anyEnemy() {
            return EUIUtils.format(anyX, enemy);
        }
        public final String bottomOf(Object amount) {
            return EUIUtils.format(bottomOfX, amount);
        }
        public final String characterWithX(Object obj) {
            return withX(character, obj);
        }
        public final String copiesOf(Object obj) {
            return EUIUtils.format(copiesOfX, obj);
        }
        public final String count(Object amount) {
            return EUIUtils.format(xCount, amount);
        }
        public final String enemyWithX(Object obj) {
            return withX(enemy, obj);
        }
        public final String from(Object place) {
            return EUIUtils.format(fromX, place);
        }
        public final String in(Object place) {
            return EUIUtils.format(fromX, place);
        }
        public final String max(Object amount) {
            return EUIUtils.format(maxX, amount);
        }
        public final String min(Object amount) {
            return EUIUtils.format(minX, amount);
        }
        public final String ofX(Object amount) {
            return EUIUtils.format(ofX, amount);
        }
        public final String onAnyCharacter(Object desc1) {return onTarget(desc1, anyone);}
        public final String onAnyEnemy(Object desc1) {return onTarget(desc1, anyEnemy());}
        public final String onTheEnemy(Object desc1) {return onTarget(desc1, target);}
        public final String onYou(Object desc1) {return onTarget(desc1, you);}
        public final String onTarget(Object desc1, Object desc2) {return EUIUtils.format(xOnY, desc1, desc2);}
        public final String permanentlyX(Object obj) {
            return EUIUtils.format(permanentlyX, obj);
        }
        public final String playingXWith(Object t1, Object t2) {
            return EUIUtils.format(playingXWithY, t1, t2);
        }
        public final String randomX(Object amount) {
            return EUIUtils.format(randomX, amount);
        }
        public final String randomly(Object amount) {
            return EUIUtils.format(randomlyX, amount);
        }
        public final String theirX(Object amount) {
            return EUIUtils.format(theirX, amount);
        }
        public String thisTurn(String base)
        {
            return EUIUtils.format(xThisTurn, base);
        }
        public final String times(Object amount) {
            return EUIUtils.format(xTimes, amount);
        }
        public final String topOf(Object amount) {
            return EUIUtils.format(topOfX, amount);
        }
        public final String xBonus(Object amount) {
            return EUIUtils.format(xBonus, amount);
        }
        public final String xCost(Object amount) {
            return EUIUtils.format(xCost, amount);
        }
        public final String xOfY(Object obj, Object t) {
            return EUIUtils.format(xOfY, obj, t);
        }
        public final String yourFirst(Object amount) {
            return EUIUtils.format(yourFirstX, amount);
        }
        public final String your(Object amount) {
            return EUIUtils.format(yourX, amount);
        }
    }

    public class Actions
    {
        private final UIStrings actions = getUIStrings("Actions");
        private final Subjects subjects;
        public Actions(Subjects subjects)
        {
            this.subjects = subjects;
        }

        public final String activate(Object desc1)
        {
            return format(0, desc1);
        }
        public final String addToPile(Object desc1, Object desc2, Object pile)
        {
            return format(1, desc1, desc2, pile);
        }
        public final String applyToTarget(Object power, Object target)
        {
            return format(2, power, target);
        }
        public final String applyAmountToTarget(Object amount, Object power, Object target)
        {
            return format(3, amount, power, target);
        }
        public final String applyAmount(Object amount, Object power) {return format(4, amount, power);}
        public final String apply(Object power)
        {
            return format(5, power);
        }
        public final String choose(Object amount)
        {
            return format(6, amount);
        }
        public final String costs(Object amount)
        {
            return format(7, amount);
        }
        public final String dealTo(Object amount, Object damage, Object target)
        {
            return format(8, amount, damage, target);
        }
        public final String deal(Object amount, Object damage)
        {
            return format(9, amount, damage);
        }
        public final String enterAnyStance()
        {
            return format(10);
        }
        public final String enterStance(Object stance)
        {
            return format(11, stance);
        }
        public final String exitStance()
        {
            return format(12);
        }
        public final String gainAmount(Object amount, Object power)
        {
            return format(13, amount, power);
        }
        public final String gain(Object power)
        {
            return format(14, power);
        }
        public final String giveTargetAmount(Object target, Object amount, Object power) {return format(15, target, amount, power);}
        public final String giveTarget(Object target, Object power) {return format(16, target, power);}
        public final String has(Object amount)
        {
            return format(17, amount);
        }
        public final String hasAmount(Object amount, Object power)
        {
            return format(18, amount, power);
        }
        public final String haveObject(Object amount, Object power)
        {
            return format(19, amount, power);
        }
        public final String increaseBy(Object power, Object amount)
        {
            return format(20, power, amount);
        }
        public final String loseAmount(Object amount, Object power)
        {
            return format(21, amount, power);
        }
        public final String move(Object amount, Object target)
        {
            return format(22, amount, target);
        }
        public final String moveTo(Object amount, Object dest, Object target)
        {
            return format(23, amount, dest, target);
        }
        public final String obtainAmount(Object amount, Object card)
        {
            return format(24, amount, card);
        }
        public final String obtain(Object card)
        {
            return format(25, card);
        }
        public final String pay(Object amount, Object power) {return format(26, amount, power);}
        public final String reduceBy(Object power, Object amount)
        {
            return format(27, power, amount);
        }
        public final String reducePropertyBy(Object property, Object target, Object amount)
        {
            return format(28, property, target, amount);
        }
        public final String remove(Object item)
        {
            return format(29, item);
        }
        public final String removeFrom(Object item, Object target)
        {
            return format(30, item, target);
        }
        public final String removeFromPlace(Object item, Object target, Object place)
        {
            return format(31, item, target, place);
        }
        public final String repeat(Object times) {return format(32, times);}
        public final String select(Object amount)
        {
            return format(33, amount);
        }
        public final String setTheOf(Object item, Object target, Object affinity2) {return format(34, item, target, affinity2);}
        public final String setTheOfFrom(Object item, Object target, Object place, Object affinity2) {return format(35, item, target, place, affinity2);}
        public final String setTheItem(Object item, Object affinity) {return format(36, item, affinity);}
        public final String setTheLast(Object amount, Object item, Object affinity) {return format(37, amount, item, affinity);}
        public final String shift(Object item)
        {
            return format(38, item);
        }
        public final String stealFrom(Object amount, Object item, Object target) {return format(39, amount, item, target);}
        public final String stealAmount(Object amount, Object power)
        {
            return format(40, amount, power);
        }
        public final String stun(Object target)
        {
            return format(41, target);
        }
        public final String takeDamage(Object amount) {return format(42, amount);}
        public final String transform(Object subject, Object target) {return format(43, subject, target);}
        public final String use(Object target)
        {
            return format(44, target);
        }
        public final String youCannotGain(Object target)
        {
            return format(45, target);
        }
        public final String objectGainsBonus(Object object, Object amount, Object bonus)
        {
            return format(46, object, amount, bonus);
        }
        public final String objectHas(Object object, Object amount)
        {
            return format(47, object, amount);
        }
        public final String objectLoses(Object object, Object amount, Object bonus)
        {
            return format(48, object, amount, bonus);
        }
        public final String objectOn(Object action, Object object, Object target)
        {
            return format(49, action, object, target);
        }
        public final String objectOnAmount(Object action, Object amount, Object object, Object target)
        {
            return format(50, action, amount, object, target);
        }
        public final String objectTo(Object action, Object object, Object target)
        {
            return format(51, action, object, target);
        }

        public final String channelX(Object amount, Object subject)
        {
            return generic3(PGR.core.tooltips.channel.title, amount, subject);
        }
        public final String channel(Object subject)
        {
            return generic2(PGR.core.tooltips.channel.title, subject);
        }
        public final String cycleType(Object amount, Object subject)
        {
            return generic3(PGR.core.tooltips.cycle.title, amount, subject);
        }
        public final String cycle(Object amount)
        {
            return generic2(PGR.core.tooltips.cycle.title, amount);
        }
        public final String discard(Object amount)
        {
            return generic2(PGR.core.tooltips.discard.title, amount);
        }
        public final String drawType(Object amount, Object subject)
        {
            return generic3(PGR.core.tooltips.draw.title, amount, subject);
        }
        public final String draw(Object amount)
        {
            return generic2(PGR.core.tooltips.draw.title, amount);
        }
        public final String exhaust(Object amount)
        {
            return generic2(PGR.core.tooltips.exhaust.title, amount);
        }
        public final String evokeXTimes(Object subject, Object amount)
        {
            return genericTimes(PGR.core.tooltips.evoke.title, subject, amount);
        }
        public final String evoke(Object subject)
        {
            return generic2(PGR.core.tooltips.evoke.title, subject);
        }
        public final String heal(Object amount)
        {
            return generic3(PGR.core.tooltips.heal.title, amount, PGR.core.tooltips.hp.title);
        }
        public final String healOn(Object amount, Object target)
        {
            return objectOnAmount(PGR.core.tooltips.heal.title, amount, PGR.core.tooltips.hp.title, target);
        }
        public final String fetch(Object amount)
        {
            return generic2(PGR.core.tooltips.fetch.title, amount);
        }
        public final String giveFrom(Object subject, Object target, Object power)
        {
            return giveTargetAmount(subject, subjects.from(target), power);
        }
        public final String kill(String targetString)
        {
            return generic2(PGR.core.tooltips.kill.title, targetString);
        }
        public final String playFrom(Object amount, Object subject, Object target)
        {
            return genericFrom(PGR.core.tooltips.play.title, amount, subject, target);
        }
        public final String play(Object amount)
        {
            return EUIRM.strings.verbNoun(PGR.core.tooltips.play.title, amount);
        }
        public final String purge(Object amount)
        {
            return generic2(PGR.core.tooltips.purge.title, amount);
        }
        public final String reduceCooldown(Object target, Object amount) {return reducePropertyBy(PGR.core.tooltips.cooldown.title, target, amount);}
        public final String reshuffle(Object amount)
        {
            return generic2(PGR.core.tooltips.reshuffle.title, amount);
        }
        public final String retainX(Object amount, Object subject)
        {
            return generic3(PGR.core.tooltips.retain.title, amount, subject);
        }
        public final String retain(Object amount, Object subject)
        {
            return EUIRM.strings.verbAdjNoun(PGR.core.tooltips.retain.title, amount, subject);
        }
        public final String retain(Object amount)
        {
            return generic2(PGR.core.tooltips.retain.title, amount);
        }
        public final String scout(Object amount)
        {
            return generic2(PGR.core.tooltips.scout.title, amount);
        }
        public final String scry(Object amount)
        {
            return generic2(PGR.core.tooltips.scry.title, amount);
        }
        public final String spread(Object subject, Object target)
        {
            return objectOn(PGR.core.tooltips.spread.title, subject, target);
        }
        public final String spreadAmount(Object amount, Object subject, Object target)
        {
            return objectOnAmount(PGR.core.tooltips.spread.title, amount, subject, target);
        }
        public final String stabilize(Object subject, Object target)
        {
            return objectOn(PGR.core.tooltips.stabilize.title, subject, target);
        }
        public final String triggerXTimes(Object subject, Object amount)
        {
            return genericTimes(PGR.core.tooltips.trigger.title, subject, amount);
        }
        public final String trigger(Object subject)
        {
            return generic2(PGR.core.tooltips.trigger.title, subject);
        }
        public final String upgradeFrom(Object amount, Object subject, Object target)
        {
            return genericFrom(PGR.core.tooltips.upgrade.title, amount, subject, target);
        }
        public final String upgrade(Object amount)
        {
            return generic2(PGR.core.tooltips.upgrade.title, amount);
        }
        public final String withdraw(Object subject)
        {
            return generic2(PGR.core.tooltips.withdraw.title, subject);
        }

        public final String genericFrom(Object verb, Object amount, Object subject, Object target)
        {
            return EUIRM.strings.verbNumNounPlace(verb, amount, subject, subjects.from(target));
        }

        public final String genericTimes(Object verb, Object subject, Object times)
        {
            return EUIRM.strings.verbNounAdv(verb, subject, subjects.times(times));
        }

        public final String generic2(Object verb, Object subject)
        {
            return EUIRM.strings.verbNoun(verb, subject);
        }

        public final String generic3(Object verb, Object adj, Object subject)
        {
            return EUIRM.strings.verbAdjNoun(verb, adj, subject);
        }

        private String format(int index, Object... objects)
        {
            return EUIUtils.format(actions.TEXT[index], objects);
        }

        private String format(int index)
        {
            return actions.TEXT[index];
        }
    }

    public class Conditions
    {
        private final UIStrings conditions = getUIStrings("Conditions");
        private final Subjects subjects;
        public Conditions(Subjects subjects)
        {
            this.subjects = subjects;
        }

        public final String any(Object desc1)
        {
            return format( 0, desc1);
        }
        public final String atEndOfTurn()
        {
            return format(1);
        }
        public final String atStartOfTurn()
        {
            return format(2);
        }
        public final String doX(Object desc1)
        {
            return format( 3, desc1);
        }
        public final String doForEach()
        {
            return format(4);
        }
        public final String forTurns(Object desc1)
        {
            return format( 5, desc1);
        }
        public final String ifYouDidThisTurn(Object verb, Object obj)
        {
            return format( 6, verb, obj);
        }
        public final String ifYouHave(Object desc1)
        {
            return format( 7, desc1);
        }
        public final String ifYourHighest(Object desc1)
        {
            return format( 8, desc1);
        }
        public final String ifTargetHas(Object desc1, Object desc2)
        {
            return format(9, desc1, desc2);
        }
        public final String ifX(Object desc1)
        {
            return format( 10, desc1);
        }
        public final String inXAtTurnEnd(Object desc1)
        {
            return format( 11, desc1);
        }
        public final String inXAtTurnStart(Object desc1)
        {
            return format( 12, desc1);
        }
        public final String inTurns(Object desc1)
        {
            return format( 13, desc1);
        }
        public final String levelItem(Object level, Object desc1)
        {
            return format(14, level, desc1);
        }
        public final String nextTurn()
        {
            return format(15);
        }
        public final String no(Object desc1)
        {
            return format( 16, desc1);
        }
        public final String not(Object desc1)
        {
            return format( 17, desc1);
        }
        public final String onGeneric(Object desc1)
        {
            return format( 18, desc1);
        }
        public final String otherwise(Object desc1)
        {
            return format( 19, desc1);
        }
        public final String takeDamage(Object amount)
        {
            return format(20, amount);
        }
        public final String whenMulti(Object desc1, Object desc2)
        {
            return format(21, desc1, desc2);
        }
        public final String whenSingle(Object desc1)
        {
            return format( 22, desc1);
        }
        public final String whenObjectIs(Object desc1, Object desc2)
        {
            return format(23, desc1, desc2);
        }
        public final String activated(Object desc1)
        {
            return format( 24, desc1);
        }
        public final String and(Object desc1, Object desc2)
        {
            return format(25, desc1, desc2);
        }
        public final String numIf(Object desc1, Object desc2)
        {
            return format(26, desc1, desc2);
        }
        public final String objIs(Object desc1, Object desc2)
        {
            return format(27, desc1, desc2);
        }
        public final String or(Object desc1, Object desc2)
        {
            return format(28, desc1, desc2);
        }
        public final String per(Object desc1, Object desc2)
        {
            return format(29, desc1, desc2);
        }
        public final String perThisCombat(Object desc1, Object desc2, Object desc3, Object extra)
        {
            return format(30, desc1, desc2, desc3, extra);
        }
        public final String perThisTurn(Object desc1, Object desc2, Object desc3, Object extra)
        {
            return format(31, desc1, desc2, desc3, extra);
        }
        public final String perIn(Object desc1, Object desc2, Object desc3)
        {
            return format(32, desc1, desc2, desc3);
        }
        public final String perDistinct(Object desc1, Object desc2)
        {
            return format(33, desc1, desc2);
        }
        public final String timesPerCombat(Object desc1)
        {
            return format( 34, desc1);
        }
        public final String timesPerTurn(Object desc1)
        {
            return format( 35, desc1);
        }
        public final String inOrderTo(Object desc1, Object desc2)
        {
            return format(36, desc1, desc2);
        }
        public final String doThen(Object desc1, Object desc2)
        {
            return format(37, desc1, desc2);
        }
        public final String genericConditional(Object desc1, Object desc2)
        {
            return format(38, desc1, desc2);
        }
        public final String modifyCards()
        {
            return format(39);
        }
        public final String modifyCreatures()
        {
            return format(40);
        }

        public final String ifTheEnemyHas(Object desc1) {return ifTargetHas(subjects.target, desc1);}
        public final String ifAnyEnemyHas(Object desc1) {return ifTargetHas(subjects.anyEnemy(), desc1);}
        public final String ifAnyCharacterHas(Object desc1) {return ifTargetHas(subjects.anyone, desc1);}
        public final String ifTheEnemyIs(Object desc1) {return ifX(objIs(subjects.target, desc1));}
        public final String ifAnyEnemyIs(Object desc1) {return ifX(objIs(subjects.anyEnemy(), desc1));}
        public final String wheneverYou(Object desc1) {return whenMulti(subjects.you, desc1);}

        private String format(int index, Object... objects)
        {
            String text = conditions.TEXT[index];
            return EUIUtils.format(text, objects);
        }
        private String format(int index)
        {
            return conditions.TEXT[index];
        }
    }

    public class CardPile
    {
        private final UIStrings cardPile = getUIStrings("CardPile");

        public final String discardPile = cardPile.TEXT[0];
        public final String drawPile = cardPile.TEXT[1];
        public final String exhaustPile = cardPile.TEXT[2];
        public final String hand = cardPile.TEXT[3];
        public final String masterDeck = cardPile.TEXT[4];
        public final String purgedPile = cardPile.TEXT[5];
        public final String manual = cardPile.TEXT[6];
        public final String top = cardPile.TEXT[7];
        public final String bottom = cardPile.TEXT[8];
        public final String random = cardPile.TEXT[9];
    }

    public class Tutorial
    {
        private final UIStrings tutorial = getUIStrings("Tutorial");

        public final String learnMore = tutorial.TEXT[0];
        public final String deciderSimple = tutorial.TEXT[1];
        public final String conjurerSimple = tutorial.TEXT[2];
        public final String eternalSimple = tutorial.TEXT[3];
        public final String affinityTutorial = tutorial.TEXT[4];
        public final String deciderTutorial1 = tutorial.TEXT[5];
        public final String deciderTutorial2 = tutorial.TEXT[6];
        public final String deciderTutorial3 = tutorial.TEXT[7];
        public final String conjurerTutorial1 = tutorial.TEXT[8];
        public final String conjurerTutorial2 = tutorial.TEXT[9];
        public final String conjurerTutorial3 = tutorial.TEXT[10];
        public final String eternalTutorial1 = tutorial.TEXT[11];
        public final String eternalTutorial2 = tutorial.TEXT[12];
        public final String eternalTutorial3 = tutorial.TEXT[13];
        public final String characterTutorial1 = tutorial.TEXT[14];
        public final String characterTutorial2 = tutorial.TEXT[15];
        public final String augmentTutorial1 = tutorial.TEXT[16];
        public final String augmentTutorial2 = tutorial.TEXT[17];
    }

    public class CardEditorTutorial
    {
        public final UIStrings cardeditortutorial = getUIStrings("CardEditorTutorial");
        public final String selector1 = cardeditortutorial.TEXT[0];
        public final String selector2 = cardeditortutorial.TEXT[1];
        public final String selectorReload = cardeditortutorial.TEXT[2];
        public final String primaryForm = cardeditortutorial.TEXT[3];
        public final String primaryImage = cardeditortutorial.TEXT[4];
        public final String primaryFlags = cardeditortutorial.TEXT[5];
        public final String attrTags1 = cardeditortutorial.TEXT[6];
        public final String attrTags2 = cardeditortutorial.TEXT[7];
        public final String attrAffinity = cardeditortutorial.TEXT[8];
        public final String effectCondition = cardeditortutorial.TEXT[9];
        public final String effectEffect = cardeditortutorial.TEXT[10];
        public final String effectModifier = cardeditortutorial.TEXT[11];
        public final String effectTrigger = cardeditortutorial.TEXT[12];
        public final String effectTurnDelay = cardeditortutorial.TEXT[13];
        public final String effectChoices = cardeditortutorial.TEXT[14];
        public final String effectConditionIfElse = cardeditortutorial.TEXT[15];
        public final String effectConditionOr = cardeditortutorial.TEXT[16];
        public final String imageSelect = cardeditortutorial.TEXT[17];
        public final String imageCrop = cardeditortutorial.TEXT[18];
        public final String maxUpgrades = cardeditortutorial.TEXT[19];
        public final String maxCopies = cardeditortutorial.TEXT[20];
        public final String idSuffix = cardeditortutorial.TEXT[21];
        public final String nameLanguage = cardeditortutorial.TEXT[22];
        public final String rarity = cardeditortutorial.TEXT[23];
        public final String type = cardeditortutorial.TEXT[24];
        public final String cardTarget = cardeditortutorial.TEXT[25];
        public final String amount = cardeditortutorial.TEXT[26];
        public final String attackType = cardeditortutorial.TEXT[27];
        public final String attackEffect = cardeditortutorial.TEXT[28];
        public final String hitCount = cardeditortutorial.TEXT[29];
        public final String blockCount = cardeditortutorial.TEXT[30];
    }

    public class GridSelection
    {
        public final UIStrings gridselection = getUIStrings("GridSelection");
        public final String chooseCards = gridselection.TEXT[0];
        public final String cardsInPile = gridselection.TEXT[1];
        public final String scry = gridselection.TEXT[2];
        public final String discard = DiscardAction.TEXT[0];
        public final String exhaust = ExhaustAction.TEXT[0];
        public final String cycle = GamblingChipAction.TEXT[1];
        public final String chooseOneCard = CardRewardScreen.TEXT[1];

        public final String chooseCards(int amount)
        {
            return EUIUtils.format(chooseCards, amount);
        }

        public final String cardsInPile(Object item, int amount)
        {
            return EUIUtils.format(cardsInPile, item, amount);
        }
    }

    public static String period(boolean addPeriod) {
        return addPeriod ? LocalizedStrings.PERIOD : "";
    }

    public static String colorString(Object item, String colorHex) {
        return "{#" + colorHex + ":" + item + "}";
    }

    public static String headerString(String title, Object desc) {
        return "{#p:" + title + "}: " + desc;
    }

    public static String joinWithAnd(List<String> values) {
        return joinWith(PGR.core.strings.conditions::and, values);
    }

    public static String joinWithAnd(String... values) {
        return joinWith(PGR.core.strings.conditions::and, values);
    }

    public static String joinWithOr(List<String> values) {
        return joinWith(PGR.core.strings.conditions::or, values);
    }

    public static String joinWithOr(String... values) {
        return joinWith(PGR.core.strings.conditions::or, values);
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

    public static String plural(EUITooltip tip, Object evaluated) {
        return EUIUtils.format(tip.plural(), evaluated);
    }

    public static String plural(String tip, Object evaluated) {
        return EUIUtils.format(tip, evaluated);
    }

    public static String pluralEvaluated(String tip, Object evaluated) {
        return EUISmartText.parseLogicString(EUIUtils.format(tip, evaluated));
    }

    public static String pluralForce(String tip) {
        return EUISmartText.parseLogicString(plural(tip, 2));
    }

    public static String singularForce(String tip) {
        return EUISmartText.parseLogicString(plural(tip, 1));
    }

    public static String past(Object obj) {
        return obj instanceof EUITooltip ? ((EUITooltip) obj).past() : EUIRM.strings.past(obj);
    }

    public static String present(Object obj) {
        return obj instanceof EUITooltip ? ((EUITooltip) obj).present() : EUIRM.strings.present(obj);
    }

    public static String leftClick(Object desc)
    {
        return "{#o:" + PGR.core.strings.misc.leftClick + "}: " + desc;
    }

    public static String rightClick(Object desc)
    {
        return "{#o:" + PGR.core.strings.misc.rightClick + "}: " + desc;
    }
}