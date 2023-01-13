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
    public Trophies trophies;
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
        trophies = new Trophies();
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
        private final UIStrings strings = getUIStrings("Rewards");

        public final String rewardBreak = strings.TEXT[0];
        public final String breakDescription = strings.TEXT[1];
        public final String reroll = strings.TEXT[2];
        public final String rerollDescription = strings.TEXT[3];
        public final String maxhpbonusF1 = strings.TEXT[4];
        public final String goldbonusF1 = strings.TEXT[5];
        public final String commonUpgrade = strings.TEXT[6];
        public final String rightClickPreview = strings.TEXT[7];
        public final String potionSlot = strings.TEXT[8];
        public final String orbSlot = strings.TEXT[9];

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
        private final UIStrings strings = getUIStrings("SeriesUI");

        public final String seriesUI = strings.TEXT[0];
        public final String affinities = strings.TEXT[1];
        public final String core = strings.TEXT[2];
        public final String scalings = strings.TEXT[3];
    }

    public class CardEditor
    {
        private final UIStrings strings = getUIStrings("CardEditor");

        public final String attributes = strings.TEXT[0];
        public final String effects = strings.TEXT[1];
        public final String tags = strings.TEXT[2];
        public final String value = strings.TEXT[3];
        public final String newCard = strings.TEXT[4];
        public final String damage = strings.TEXT[5];
        public final String block = strings.TEXT[6];
        public final String magicNumber = strings.TEXT[7];
        public final String secondaryNumber = strings.TEXT[8];
        public final String hitCount = strings.TEXT[9];
        public final String upgrades = strings.TEXT[10];
        public final String cardTarget = strings.TEXT[11];
        public final String attackType = strings.TEXT[12];
        public final String attackEffect = strings.TEXT[13];
        public final String condition = strings.TEXT[14];
        public final String mainCondition = strings.TEXT[15];
        public final String effect = strings.TEXT[16];
        public final String effectX = strings.TEXT[17];
        public final String powerX = strings.TEXT[18];
        public final String modifier = strings.TEXT[19];
        public final String trigger = strings.TEXT[20];
        public final String not = strings.TEXT[21];
        public final String addTo = strings.TEXT[22];
        public final String orbs = strings.TEXT[23];
        public final String powers = strings.TEXT[24];
        public final String maxUpgrades = strings.TEXT[25];
        public final String flags = strings.TEXT[26];
        public final String editForm = strings.TEXT[27];
        public final String turnDelay = strings.TEXT[28];
        public final String extraValue = strings.TEXT[29];
        public final String useBaseValue = strings.TEXT[30];
        public final String loadImage = strings.TEXT[31];
        public final String loadFile = strings.TEXT[32];
        public final String paste = strings.TEXT[33];
        public final String addForm = strings.TEXT[34];
        public final String removeForm = strings.TEXT[35];
        public final String undo = strings.TEXT[36];
        public final String custom = strings.TEXT[37];
        public final String delegate = strings.TEXT[38];
        public final String duplicate = strings.TEXT[39];
        public final String delete = strings.TEXT[40];
        public final String reloadCards = strings.TEXT[41];
        public final String confirmDeletion = strings.TEXT[42];
        public final String confirmDeletionDesc = strings.TEXT[43];
        public final String customCards = strings.TEXT[44];
        public final String customCardsDesc = strings.TEXT[45];
        public final String primaryInfo = strings.TEXT[46];
        public final String primaryInfoDesc = strings.TEXT[47];
        public final String choices = strings.TEXT[48];
        public final String ifElseCondition = strings.TEXT[49];
        public final String ifElseConditionDesc = strings.TEXT[50];
        public final String orCondition = strings.TEXT[51];
        public final String orConditionDesc = strings.TEXT[52];
        public final String duplicateToColor = strings.TEXT[53];
        public final String duplicateToColorDesc = strings.TEXT[54];
        public final String createRandom = strings.TEXT[55];
        public final String importExisting = strings.TEXT[56];
        public final String openFolder = strings.TEXT[57];
        public final String exportCSV = strings.TEXT[58];
        public final String random = strings.TEXT[59];
        public final String required = strings.TEXT[60];
    }

    public class Misc
    {
        private final UIStrings strings = getUIStrings("Misc");

        public final String viewAugments = strings.TEXT[0];
        public final String viewAugmentsDescription = strings.TEXT[1];
        public final String viewAugmentsNone = strings.TEXT[2];
        public final String viewCardPoolSeries = strings.TEXT[3];
        public final String cardModeHeader = strings.TEXT[4];
        public final String simpleMode = strings.TEXT[5];
        public final String simpleModeDescription = strings.TEXT[6];
        public final String complexMode = strings.TEXT[7];
        public final String complexModeDescription = strings.TEXT[8];
        public final String allowCustomCards = strings.TEXT[9];
        public final String pcl = strings.TEXT[10];
        public final String leftClick = strings.TEXT[11];
        public final String rightClick = strings.TEXT[12];
    }

    public class Options
    {
        private final UIStrings strings = getUIStrings("Options");

        public final String cropCardImages = strings.TEXT[0];
        public final String displayCardTagDescription = strings.TEXT[1];
        public final String enableEventsForOtherCharacters = strings.TEXT[2];
        public final String enableRelicsForOtherCharacters = strings.TEXT[3];
        public final String replaceCards = strings.TEXT[4];
        public final String usePCLPowersForAll = strings.TEXT[5];
        public final String hideIrrelevantAffinities = strings.TEXT[6];
        public final String showFormulaDisplay = strings.TEXT[7];
    }

    public class CharacterSelect
    {
        private final UIStrings strings = getUIStrings("CharacterSelect");

        public final String leftText = strings.TEXT[0];  // Starting Cards:
        public final String rightText = strings.TEXT[1]; // Unlock
        public final String invalidLoadout = strings.TEXT[3];
        public final String deckEditor = strings.TEXT[5];
        public final String deckEditorInfo = strings.TEXT[6];
        public final String seriesEditor = strings.TEXT[8];
        public final String seriesEditorInfo = strings.TEXT[9];
        public final String deckHeader = strings.TEXT[10];
        public final String relicsHeader = strings.TEXT[11];
        public final String attributesHeader = strings.TEXT[12];
        public final String valueHeader = strings.TEXT[13];
        public final String hindranceDescription = strings.TEXT[18];
        public final String affinityDescription = strings.TEXT[19];
        public final String unsavedChanges = strings.TEXT[20];
        public final String clear = strings.TEXT[21];
        public final String copyTo = strings.TEXT[22];
        public final String copyFrom = strings.TEXT[23];
        public final String export = strings.TEXT[24];
        public final String ascensionGlyph = strings.TEXT[25];
        public final String cardEditor = strings.TEXT[26];
        public final String cardEditorInfo = strings.TEXT[27];
        public final String cardEditorEnabled = strings.TEXT[28];
        public final String cardEditorDisabled = strings.TEXT[29];
        public final String cardEditorToggle = strings.TEXT[30];

        public final String unlocksAtLevel(int unlockLevel, int currentLevel)
        {
            return EUIUtils.format(strings.TEXT[2], unlockLevel, currentLevel);
        }

        public final String unlocksAtAscension(int ascension)
        {
            return EUIUtils.format(strings.TEXT[4], ascension);
        }

        public final String obtainBronzeAtAscension(int ascension)
        {
            return EUIUtils.format(strings.TEXT[7], ascension);
        }

        public final String hindranceValue(int value)
        {
            return EUIUtils.format(strings.TEXT[14], value);
        }

        public final String affinityValue(int value)
        {
            return EUIUtils.format(strings.TEXT[15], value);
        }

        public final String cardsCount(int value)
        {
            return EUIUtils.format(strings.TEXT[16], value);
        }

        public final String totalValue(int value, int max)
        {
            return EUIUtils.format(strings.TEXT[17], value, max);
        }
    }

    public class SeriesSelection
    {
        private final UIStrings strings = getUIStrings("SeriesSelection");

        public final String selected = strings.TEXT[0];
        public final String unlocked = strings.TEXT[1];
        public final String removeFromPool = strings.TEXT[2];
        public final String addToPool = strings.TEXT[3];
        public final String viewPool = strings.TEXT[4];
        public final String totalCards = strings.TEXT[5];
        public final String instructions1 = strings.TEXT[6];
        public final String instructions2 = strings.TEXT[7];

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
        private final UIStrings strings = getUIStrings("SeriesSelectionButtons");

        public final String cardsInPool = strings.TEXT[0];
        public final String selectAll = strings.TEXT[1];
        public final String deselectAll = strings.TEXT[2];
        public final String selectRandom = strings.TEXT[3];
        public final String showCardPool = strings.TEXT[4];
        public final String save = strings.TEXT[5];
        public final String enableExpansion = strings.TEXT[6];
        public final String disableExpansion = strings.TEXT[7];
        public final String allExpansionEnable = strings.TEXT[8];
        public final String allExpansionDisable = strings.TEXT[9];
        public final String cancel = strings.TEXT[10];
        public final String showColorless = strings.TEXT[11];

        public final String selectRandom(int cards)
        {
            return EUIUtils.format(strings.TEXT[2], cards) ;
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
        public final String maxCopiesTooltip = strings.TEXT[6];
        public final String artAuthor = strings.TEXT[7];
        public final String emptyAugment = strings.TEXT[8];
        public final String viewAugments = strings.TEXT[9];
        public final String viewTooltips = strings.TEXT[10];
        public final String clickToSlot = strings.TEXT[11];
        public final String clickToRemove = strings.TEXT[12];
        public final String cannotRemove = strings.TEXT[13];
    }

    public class Hotkeys
    {
        private final UIStrings strings = getUIStrings("Hotkeys");

        public final String controlPileChange = strings.TEXT[0];
        public final String controlPileSelect = strings.TEXT[1];
        public final String rerollCurrent = strings.TEXT[2];
        public final String toggleFormulaDisplay = strings.TEXT[3];
        public final String viewAugments = strings.TEXT[4];
    }

    public class Combat
    {
        private final UIStrings strings = getUIStrings("Combat");

        public final String current = strings.TEXT[0];
        public final String next = strings.TEXT[1];
        public final String uses = strings.TEXT[2];
        public final String rerolls = strings.TEXT[3];
        public final String controlPile = strings.TEXT[4];
        public final String controlPileDescription = strings.TEXT[5];
        public final String count = strings.TEXT[6];
        public final String effect = strings.TEXT[7];
        public final String nextLevelEffect = strings.TEXT[8];
        public final String active = strings.TEXT[9];
        public final String inactive = strings.TEXT[10];
        public final String disabled = strings.TEXT[11];
        public final String na = strings.TEXT[12];
        public final String eternalMeter = strings.TEXT[13];
        public final String eternalMeterAffinity = strings.TEXT[14];
        public final String eternalMeterCurrent = strings.TEXT[15];
        public final String eternalMeterGain = strings.TEXT[16];
        public final String eternalMeterSpend = strings.TEXT[17];
        public final String conjurerMeterDebuff = strings.TEXT[18];
        public final String conjurerMeterCost = strings.TEXT[19];
        public final String conjurerMeterDamage = strings.TEXT[20];
        public final String conjurerMeterSwitch = strings.TEXT[21];
        public final String conjurerMeterCombust = strings.TEXT[22];
        public final String conjurerMeterRedox = strings.TEXT[23];
        public final String conjurerMeterNextIntensity = strings.TEXT[24];
        public final String deciderMeterStacks = strings.TEXT[25];
        public final String deciderMeterStacksNextLevel = strings.TEXT[26];
        public final String dodged = strings.TEXT[27];

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
        private final UIStrings strings = getUIStrings("CardMods");

        public final String handSize = strings.TEXT[0];
        public final String afterlifeMet = strings.TEXT[1];
        public final String afterlifeRequirement = strings.TEXT[2];
        public final String respecLivingPicture = strings.TEXT[3];
        public final String respecLivingPictureLocked = strings.TEXT[4];
        public final String respecLivingPictureDescription = strings.TEXT[5];
        public final String kirby = strings.TEXT[6];
        public final String kirbyDescription = strings.TEXT[7];
        public final String tempPowerPrefix = strings.TEXT[8];
        public final String requirement = strings.TEXT[9];
    }

    public class CardType
    {
        private final UIStrings strings = getUIStrings("CardType");

        public final String none = strings.TEXT[0];
        public final String allAlly = strings.TEXT[1];
        public final String allCharacter = strings.TEXT[2];
        public final String allEnemy = strings.TEXT[3];
        public final String any = strings.TEXT[4];
        public final String randomAlly = strings.TEXT[5];
        public final String randomEnemy = strings.TEXT[6];
        public final String self = strings.TEXT[7];
        public final String singleAlly = strings.TEXT[8];
        public final String singleTarget = strings.TEXT[9];
        public final String team = strings.TEXT[10];
        public final String legendary = strings.TEXT[11];
        public final String secretRare = strings.TEXT[12];

        public final String tagAoE = strings.EXTRA_TEXT[0];
        public final String tagRandom = strings.EXTRA_TEXT[1];
    }

    public class Subjects
    {
        private final UIStrings strings = getUIStrings("Subjects");

        public final String allyN = strings.TEXT[0];
        public final String cardN = strings.TEXT[1];
        public final String characterN = strings.TEXT[2];
        public final String enemyN = strings.TEXT[3];
        public final String cost = strings.TEXT[4];
        public final String infinite = strings.TEXT[5];
        public final String maxX = strings.TEXT[6];
        public final String minX = strings.TEXT[7];
        public final String other = strings.TEXT[8];
        public final String xCount = strings.TEXT[9];
        public final String allX = strings.TEXT[10];
        public final String all = strings.TEXT[11];
        public final String ally = strings.TEXT[12];
        public final String anyPile = strings.TEXT[13];
        public final String anyX = strings.TEXT[14];
        public final String anyone = strings.TEXT[15];
        public final String attacking = strings.TEXT[16];
        public final String buffing = strings.TEXT[17];
        public final String card = strings.TEXT[18];
        public final String character = strings.TEXT[19];
        public final String copiesOfX = strings.TEXT[20];
        public final String damage = strings.TEXT[21];
        public final String debuffing = strings.TEXT[22];
        public final String defending = strings.TEXT[23];
        public final String effectBonus = strings.TEXT[24];
        public final String enemy = strings.TEXT[25];
        public final String fromX = strings.TEXT[26];
        public final String hits = strings.TEXT[27];
        public final String inX = strings.TEXT[28];
        public final String ofX = strings.TEXT[29];
        public final String permanentlyX = strings.TEXT[30];
        public final String playingXWithY = strings.TEXT[31];
        public final String randomX = strings.TEXT[32];
        public final String randomlyX = strings.TEXT[33];
        public final String shuffleYourDeck = strings.TEXT[34];
        public final String bottomOfX = strings.TEXT[35];
        public final String leftmostX = strings.TEXT[36];
        public final String rightmostX = strings.TEXT[37];
        public final String target = strings.TEXT[38];
        public final String topOfX = strings.TEXT[39];
        public final String theirX = strings.TEXT[40];
        public final String them = strings.TEXT[41];
        public final String thisObj = strings.TEXT[42];
        public final String thisX = strings.TEXT[43];
        public final String you = strings.TEXT[44];
        public final String yourFirstX = strings.TEXT[45];
        public final String yourX = strings.TEXT[46];
        public final String xOfY = strings.TEXT[47];
        public final String xOnY = strings.TEXT[48];
        public final String xThisTurn = strings.TEXT[49];
        public final String xTimes = strings.TEXT[50];
        public final String xWithY = strings.TEXT[51];
        public final String xCost = strings.TEXT[52];
        public final String xBonus = strings.TEXT[53];

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
        private final UIStrings strings = getUIStrings("Actions");
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
            return EUIUtils.format(strings.TEXT[index], objects);
        }

        private String format(int index)
        {
            return strings.TEXT[index];
        }
    }

    public class Conditions
    {
        private final UIStrings strings = getUIStrings("Conditions");
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
            String text = strings.TEXT[index];
            return EUIUtils.format(text, objects);
        }
        private String format(int index)
        {
            return strings.TEXT[index];
        }
    }

    public class CardPile
    {
        private final UIStrings strings = getUIStrings("CardPile");

        public final String discardPile = strings.TEXT[0];
        public final String drawPile = strings.TEXT[1];
        public final String exhaustPile = strings.TEXT[2];
        public final String hand = strings.TEXT[3];
        public final String masterDeck = strings.TEXT[4];
    }

    public class Trophies
    {
        private final UIStrings strings = getUIStrings("Trophies");

        public final String trophy = strings.TEXT[0];
        public final String glyph = strings.TEXT[1];
        public final String gold = strings.TEXT[2];
        public final String bronzeDescription = strings.TEXT[3];
        public final String silverDescription = strings.TEXT[4];
        public final String goldDescription = strings.TEXT[5];
        public final String bronzeLocked = strings.TEXT[6];
        public final String silverLocked = strings.TEXT[7];
        public final String goldLocked = strings.TEXT[8];

        public final String bronzeFormatted(int ascension)
        {
            return EUIUtils.format(bronzeDescription, ascension);
        }

        public final String silverFormatted(int ascension)
        {
            return EUIUtils.format(silverDescription, ascension);
        }

        public final String goldFormatted(int ascension)
        {
            return EUIUtils.format(goldDescription, ascension);
        }
    }

    public class Tutorial
    {
        private final UIStrings strings = getUIStrings("Tutorial");

        public final String learnMore = strings.TEXT[0];
        public final String deciderSimple = strings.TEXT[1];
        public final String conjurerSimple = strings.TEXT[2];
        public final String eternalSimple = strings.TEXT[3];
        public final String affinityTutorial = strings.TEXT[4];
        public final String deciderTutorial1 = strings.TEXT[5];
        public final String deciderTutorial2 = strings.TEXT[6];
        public final String deciderTutorial3 = strings.TEXT[7];
        public final String conjurerTutorial1 = strings.TEXT[8];
        public final String conjurerTutorial2 = strings.TEXT[9];
        public final String conjurerTutorial3 = strings.TEXT[10];
        public final String eternalTutorial1 = strings.TEXT[11];
        public final String eternalTutorial2 = strings.TEXT[12];
        public final String eternalTutorial3 = strings.TEXT[13];
        public final String characterTutorial1 = strings.TEXT[14];
        public final String characterTutorial2 = strings.TEXT[15];
        public final String augmentTutorial1 = strings.TEXT[16];
        public final String augmentTutorial2 = strings.TEXT[17];
    }

    public class CardEditorTutorial
    {
        public final UIStrings strings = getUIStrings("CardEditorTutorial");
        public final String selector1 = strings.TEXT[0];
        public final String selector2 = strings.TEXT[1];
        public final String selectorReload = strings.TEXT[2];
        public final String primaryForm = strings.TEXT[3];
        public final String primaryImage = strings.TEXT[4];
        public final String primaryFlags = strings.TEXT[5];
        public final String attrTags1 = strings.TEXT[6];
        public final String attrTags2 = strings.TEXT[7];
        public final String attrAffinity = strings.TEXT[8];
        public final String effectCondition = strings.TEXT[9];
        public final String effectEffect = strings.TEXT[10];
        public final String effectModifier = strings.TEXT[11];
        public final String effectTrigger = strings.TEXT[12];
        public final String effectTurnDelay = strings.TEXT[13];
        public final String effectChoices = strings.TEXT[14];
        public final String effectConditionIfElse = strings.TEXT[15];
        public final String effectConditionOr = strings.TEXT[16];
        public final String imageSelect = strings.TEXT[17];
        public final String imageCrop = strings.TEXT[18];
    }

    public class GridSelection
    {
        public final UIStrings strings = getUIStrings("GridSelection");
        public final String chooseCards = strings.TEXT[0];
        public final String cardsInPile = strings.TEXT[1];
        public final String scry = strings.TEXT[2];
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