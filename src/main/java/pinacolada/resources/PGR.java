package pinacolada.resources;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.RelicType;
import com.evacipated.cardcrawl.mod.stslib.patches.CustomTargeting;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIGameUtils;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.EUIBase;
import extendedui.ui.cardFilter.CountingPanel;
import extendedui.ui.controls.EUIButton;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.annotations.VisibleCard;
import pinacolada.annotations.VisiblePotion;
import pinacolada.annotations.VisiblePower;
import pinacolada.annotations.VisibleRelic;
import pinacolada.augments.PCLAugmentData;
import pinacolada.augments.PCLCustomAugmentSlot;
import pinacolada.blights.PCLCustomBlightSlot;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.fields.CardFlag;
import pinacolada.cards.base.fields.PCLCustomFlagInfo;
import pinacolada.characters.PCLCharacterAnimation;
import pinacolada.commands.*;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLCardTargetingManager;
import pinacolada.dungeon.PCLDungeon;
import pinacolada.effects.EffekseerEFK;
import pinacolada.misc.AugmentStrings;
import pinacolada.misc.LoadoutStrings;
import pinacolada.misc.PCLAffinityPanelFilter;
import pinacolada.orbs.PCLCustomOrbSlot;
import pinacolada.patches.basemod.PotionPoolPatches;
import pinacolada.potions.PCLCustomPotionSlot;
import pinacolada.potions.PCLPotion;
import pinacolada.powers.PCLCustomPowerSlot;
import pinacolada.powers.PCLPowerData;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.loadout.PCLCustomLoadoutInfo;
import pinacolada.resources.loadout.PCLLoadoutDataInfo;
import pinacolada.resources.pcl.PCLCoreResources;
import pinacolada.rewards.pcl.AugmentReward;
import pinacolada.skills.PSkill;
import pinacolada.ui.cardReward.PCLCardRewardScreen;
import pinacolada.ui.cardView.PCLSingleCardPopup;
import pinacolada.ui.cardView.PCLSingleRelicPopup;
import pinacolada.ui.characterSelection.PCLCharacterSelectOverlay;
import pinacolada.ui.characterSelection.PCLLoadoutScreen;
import pinacolada.ui.characterSelection.PCLSeriesSelectScreen;
import pinacolada.ui.customRun.PCLCustomRunScreen;
import pinacolada.ui.debug.*;
import pinacolada.ui.editor.augment.PCLCustomAugmentSelectorScreen;
import pinacolada.ui.editor.blight.PCLCustomBlightSelectorScreen;
import pinacolada.ui.editor.card.PCLCustomCardSelectorScreen;
import pinacolada.ui.editor.orb.PCLCustomOrbSelectorScreen;
import pinacolada.ui.editor.potion.PCLCustomPotionSelectorScreen;
import pinacolada.ui.editor.power.PCLCustomPowerSelectorScreen;
import pinacolada.ui.editor.relic.PCLCustomRelicSelectorScreen;
import pinacolada.ui.menu.*;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static pinacolada.utilities.GameUtilities.screenH;
import static pinacolada.utilities.GameUtilities.screenW;

// Copied and modified from STS-AnimatorMod
public class PGR {
    private static final ArrayList<PCLResources<?,?,?,?>> resourceList = new ArrayList<>();
    private static final HashMap<AbstractCard.CardColor, PCLResources<?, ?, ?, ?>> colorResourceMap = new HashMap<>();
    private static final HashMap<AbstractPlayer.PlayerClass, PCLResources<?, ?, ?, ?>> playerResourceMap = new HashMap<>();
    private static final String IMAGES_FOLDER = "images/";
    private static final String REWARDS_SUBFOLDER = "ui/rewards";
    private final static HashMap<String, AugmentStrings> AUGMENT_STRINGS = new HashMap<>();
    private final static HashMap<String, LoadoutStrings> LOADOUT_STRINGS = new HashMap<>();
    private static final Type AUGMENT_TYPE = new TypeToken<Map<String, AugmentStrings>>() {
    }.getType();
    private static final Type LOADOUT_TYPE = new TypeToken<Map<String, LoadoutStrings>>() {
    }.getType();
    public static final String BASE_PREFIX = "pcl";
    public static final PCLDungeon dungeon = PCLDungeon.register();
    public static PCLCoreResources core;
    public static PCLMainConfig config;
    public static PCLAugmentPanelItem augmentPanel;
    public static PCLAffinityPoolModule affinityFilters;
    public static PCLAugmentKeywordFilters augmentFilters;
    public static PCLAugmentLibraryScreen augmentLibrary;
    public static PCLAugmentCollectionScreen augmentScreen;
    public static PCLCardRewardScreen rewardScreen;
    public static PCLCharacterSelectOverlay charSelectProvider;
    public static PCLCustomAugmentSelectorScreen customAugments;
    public static PCLCustomBlightSelectorScreen customBlights;
    public static PCLCustomCardSelectorScreen customCards;
    public static PCLCustomOrbSelectorScreen customOrbs;
    public static PCLCustomPotionSelectorScreen customPotions;
    public static PCLCustomPowerSelectorScreen customPowers;
    public static PCLCustomRelicSelectorScreen customRelics;
    public static PCLCustomRunScreen customMode;
    public static PCLColorlessGroupLibraryModule colorlessGroups;
    public static PCLLoadoutScreen loadoutEditor;
    public static PCLOrbKeywordFilters orbFilters;
    public static PCLPowerKeywordFilters powerFilters;
    public static PCLSeriesSelectScreen seriesSelection;
    public static PCLSingleCardPopup cardPopup;
    public static PCLSingleRelicPopup relicPopup;
    public static PCLDebugAugmentPanel debugAugments;
    public static PCLDebugBlightPanel debugBlights;
    public static PCLDebugCardPanel debugCards;
    public static PCLDebugPotionPanel debugPotions;
    public static PCLDebugRelicPanel debugRelics;
    public static EUIImage blackScreen;
    public static EUIButton helpMeButton;

    public static void addAugmentStrings(String jsonString) {
        PGR.AUGMENT_STRINGS.putAll(new HashMap<String, AugmentStrings>(EUIUtils.deserialize(jsonString, AUGMENT_TYPE)));
    }

    public static void addLoadoutStrings(String jsonString) {
        PGR.LOADOUT_STRINGS.putAll(new HashMap<String, LoadoutStrings>(EUIUtils.deserialize(jsonString, LOADOUT_TYPE)));
    }

    public static void addRelicToLibrary(AbstractRelic relic, AbstractCard.CardColor color) {
        switch (color) {
            case COLORLESS:
            case CURSE:
                BaseMod.addRelic(relic, RelicType.SHARED);
                break;
            case RED:
                BaseMod.addRelic(relic, RelicType.RED);
                break;
            case GREEN:
                BaseMod.addRelic(relic, RelicType.GREEN);
                break;
            case BLUE:
                BaseMod.addRelic(relic, RelicType.BLUE);
                break;
            case PURPLE:
                BaseMod.addRelic(relic, RelicType.PURPLE);
                break;
            default:
                BaseMod.addRelicToCustomPool(relic, color);
        }
    }

    public static String createID(String prefix, String suffix) {
        return prefix + ":" + suffix;
    }

    public static String getAugmentImage(String id) {
        return getPng(id, "augments");
    }

    public static AugmentStrings getAugmentStrings(String stringID) {
        return AUGMENT_STRINGS.get(stringID);
    }

    public static String getBlightImage(String id) {
        return getPng(id, "blights");
    }

    public static BlightStrings getBlightStrings(String blightID) {
        return getLanguagePack().getBlightString(blightID);
    }

    public static String getCardImage(String id) {
        return getPng(id, "cards");
    }

    public static CardStrings getCardStrings(String cardID) {
        return getLanguagePack().getCardStrings(cardID);
    }

    public static CharacterStrings getCharacterStrings(String characterID) {
        return getLanguagePack().getCharacterString(characterID);
    }

    public static String getEventImage(String id) {
        return getPng(id, "events");
    }

    public static EventStrings getEventStrings(String eventID) {
        return getLanguagePack().getEventString(eventID);
    }

    public static LocalizedStrings getLanguagePack() {
        return CardCrawlGame.languagePack;
    }

    public static LoadoutStrings getLoadoutStrings(String stringID) {
        return LOADOUT_STRINGS.get(stringID);
    }

    public static String getMonsterImage(String id) {
        return getPng(id, "monsters");
    }

    public static MonsterStrings getMonsterStrings(String monsterID) {
        return getLanguagePack().getMonsterStrings(monsterID);
    }

    public static String getOrbImage(String id) {
        return getPng(id, "orbs");
    }

    public static OrbStrings getOrbStrings(String orbID) {
        return getLanguagePack().getOrbString(orbID);
    }

    public static PCLPlayerData<?, ?, ?> getPlayerData(AbstractCard.CardColor playerClass) {
        return getResources(playerClass).data;
    }

    public static PCLPlayerData<?, ?, ?> getPlayerData(AbstractPlayer.PlayerClass playerClass) {
        return getResources(playerClass).data;
    }

    public static String getPng(String id, String subFolder) {
        String[] s = EUI.splitID(id);
        return s.length > 1 ? IMAGES_FOLDER + s[0] + "/" + subFolder + "/" + s[1].replace(":", "_") + ".png" : null;
    }

    public static String getPotionImage(String id) {
        return getPng(id, "potions");
    }

    public static PotionStrings getPotionStrings(String relicID) {
        return getLanguagePack().getPotionString(relicID);
    }

    public static String getPowerImage(String id) {
        return getPng(id, "powers");
    }

    public static PowerStrings getPowerStrings(String powerID) {
        return getLanguagePack().getPowerStrings(powerID);
    }

    public static Collection<PCLResources<?, ?, ?, ?>> getRegisteredPlayerResources() {
        return playerResourceMap.values();
    }

    public static Collection<PCLResources<?, ?, ?, ?>> getRegisteredResources() {
        return resourceList;
    }

    public static String getRelicImage(String id) {
        return getPng(id, "relics");
    }

    public static RelicStrings getRelicStrings(String relicID) {
        return getLanguagePack().getRelicStrings(relicID);
    }

    public static PCLResources<?, ?, ?, ?> getResources(AbstractCard.CardColor cardColor) {
        return colorResourceMap.getOrDefault(cardColor, core);
    }

    public static PCLResources<?, ?, ?, ?> getResources(AbstractPlayer.PlayerClass playerClass) {
        return playerResourceMap.getOrDefault(playerClass, core);
    }

    public static String getRewardImage(String id) {
        return getPng(id, REWARDS_SUBFOLDER);
    }

    public static String getRunModImage(String id) {
        return getPng(id, "runmods");
    }

    public static RunModStrings getRunModStrings(String stringID) {
        return getLanguagePack().getRunModString(stringID);
    }

    public static StanceStrings getStanceString(String stanceID) {
        return getLanguagePack().getStanceString(stanceID);
    }

    public static void initialize() {
        // Ensure that Fabricate's BLESSING type is treated as the same type as other BLESSING types
        try {
            PCLEnum.CardType.BLESSING = AbstractCard.CardType.valueOf(PCLEnum.CardType.BLESSING.name());
        }
        catch (Exception e) {
            EUIUtils.logError(PGR.class, "Blessing type is missing, WTF");
        }

        if (core != null) {
            throw new RuntimeException("Already Initialized");
        }

        config = new PCLMainConfig();
        config.load();
        core = new PCLCoreResources();
        core.initializeColor();
    }

    protected static void initializeUI() {
        PGR.helpMeButton = new EUIButton(EUIRM.images.info.texture(), new EUIHitbox(Settings.WIDTH - EUIBase.scale(72f), Settings.HEIGHT - EUIBase.scale(72f), EUIBase.scale(48f), EUIBase.scale(48f)))
                .setTooltip(PGR.core.strings.tutorial_tour, PGR.core.strings.tutorial_tourDesc);
        PGR.cardPopup = new PCLSingleCardPopup();
        PGR.relicPopup = new PCLSingleRelicPopup();
        PGR.seriesSelection = new PCLSeriesSelectScreen();
        PGR.loadoutEditor = new PCLLoadoutScreen();
        PGR.customAugments = new PCLCustomAugmentSelectorScreen();
        PGR.customBlights = new PCLCustomBlightSelectorScreen();
        PGR.customCards = new PCLCustomCardSelectorScreen();
        PGR.customOrbs = new PCLCustomOrbSelectorScreen();
        PGR.customPotions = new PCLCustomPotionSelectorScreen();
        PGR.customPowers = new PCLCustomPowerSelectorScreen();
        PGR.customRelics = new PCLCustomRelicSelectorScreen();
        PGR.customMode = new PCLCustomRunScreen();
        PGR.charSelectProvider = new PCLCharacterSelectOverlay();
        PGR.affinityFilters = new PCLAffinityPoolModule(EUI.cardFilters);
        PGR.colorlessGroups = new PCLColorlessGroupLibraryModule(EUI.customLibraryScreen);
        PGR.augmentScreen = new PCLAugmentCollectionScreen();
        PGR.augmentPanel = new PCLAugmentPanelItem();
        PGR.augmentFilters = new PCLAugmentKeywordFilters();
        PGR.augmentLibrary = new PCLAugmentLibraryScreen();
        PGR.orbFilters = new PCLOrbKeywordFilters();
        PGR.powerFilters = new PCLPowerKeywordFilters();
        PGR.blackScreen = new EUIImage(ImageMaster.WHITE_SQUARE_IMG, new EUIHitbox(screenW(1), screenH(1)))
                .setPosition(screenW(0.5f), screenH(0.5f))
                .setColor(0, 0, 0, 0.8f);
        PGR.rewardScreen = new PCLCardRewardScreen();
        try {
            PGR.debugAugments = new PCLDebugAugmentPanel();
            PGR.debugBlights = new PCLDebugBlightPanel();
            PGR.debugCards = new PCLDebugCardPanel();
            PGR.debugPotions = new PCLDebugPotionPanel();
            PGR.debugRelics = new PCLDebugRelicPanel();
        }
        catch (Error | Exception e) {
            e.printStackTrace();
            EUIUtils.logError(PGR.class, "Failed to load ImGUI debug panels. These panels will not be available in ImGUI.");
        }

        EUI.addBattleSubscriber(CombatManager.renderInstance);
        EUI.addSubscriber(PGR.cardPopup);
        EUI.addSubscriber(PGR.relicPopup);
        EUI.addGlobalCustomCardFilter(PGR.affinityFilters);
        EUI.addCardSetFilter(AbstractCard.CardColor.COLORLESS, GameUtilities::getLoadoutNameForCard);
        EUI.setCustomCardLibraryModule(AbstractCard.CardColor.COLORLESS, PGR.colorlessGroups);
        EUI.setCustomCardLibraryModule(AbstractCard.CardColor.CURSE, PGR.colorlessGroups);

        BaseMod.addCustomScreen(PGR.augmentScreen);
    }

    public static boolean isLoaded() {
        return core != null && core.isLoaded && EUIUtils.all(getRegisteredResources(), r -> r.isLoaded);
    }

    public static void loadCustomCards() {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleCard.class)) {
            try {
                AbstractCard card = (AbstractCard) ct.getConstructor().newInstance();
                // Game may treat extra cards as locked, this should not be the case
                if (UnlockTracker.isCardLocked(card.cardID)) {
                    UnlockTracker.unlockCard(card.cardID);
                    card.isLocked = false;
                }

                VisibleCard a = ct.getAnnotation(VisibleCard.class);
                if (a.add()) {
                    BaseMod.addCard(card);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(PGR.class, "Failed to load card " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void loadCustomPotions() {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisiblePotion.class)) {
            try {
                AbstractPotion potion = (AbstractPotion) ct.getConstructor().newInstance();
                if (potion instanceof PCLPotion) {
                    // We should only add the potion to a player pool if it corresponds with an actual player (i.e. it has playerdata), simply having a playerclass isn't enough
                    PCLPlayerData<?,?,?> data = ((PCLPotion) potion).potionData.resources.data;
                    if (data != null) {
                        BaseMod.addPotion(potion.getClass(), potion.liquidColor, potion.hybridColor, potion.spotsColor, potion.ID, data.resources.playerClass);
                    }
                    else {
                        BaseMod.addPotion(potion.getClass(), potion.liquidColor, potion.hybridColor, potion.spotsColor, potion.ID);
                    }
                }
                else {
                    AbstractPlayer.PlayerClass pc = EUIGameUtils.getPlayerClassForCardColor(ct.getAnnotation(VisiblePotion.class).color());
                    if (pc != null) {
                        BaseMod.addPotion(potion.getClass(), potion.liquidColor, potion.hybridColor, potion.spotsColor, potion.ID, pc);
                    }
                    else {
                        BaseMod.addPotion(potion.getClass(), potion.liquidColor, potion.hybridColor, potion.spotsColor, potion.ID);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(PGR.class, "Failed to load potion " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void loadCustomPowers() {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisiblePower.class)) {
            try {
                VisiblePower a = ct.getAnnotation(VisiblePower.class);
                String field = a.data();
                if (field != null) {
                    PCLPowerData data = ReflectionHacks.getPrivateStatic(ct, field);
                    BaseMod.addPower((Class<? extends AbstractPower>) ct, data.ID);
                }
                else {
                    BaseMod.addPower((Class<? extends AbstractPower>) ct, PGR.core.createID(ct.getSimpleName()));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(PSkill.class, "Failed to load power " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void loadCustomRelics() {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleRelic.class)) {
            try {
                AbstractRelic relic = (AbstractRelic) ct.getConstructor().newInstance();
                AbstractCard.CardColor color = relic instanceof PCLRelic ? ((PCLRelic) relic).relicData.cardColor : ct.getAnnotation(VisibleRelic.class).color();
                addRelicToLibrary(relic, color);
            }
            catch (Exception e) {
                e.printStackTrace();
                EUIUtils.logError(PGR.class, "Failed to load relic " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void postInitialize() {
        registerCommands();
        PCLPlayerData.postInitialize();
        PotionPoolPatches.initialize();
        config.load(CardCrawlGame.saveSlot);
        config.initializeOptions();
        initializeUI();
        PSkill.initialize();
        PCLAugmentData.initialize();
        EffekseerEFK.initialize();
        CardFlag.postInitialize();
        reloadCustoms();
        CountingPanel.registerForCard(new PCLAffinityPanelFilter());
        CustomTargeting.registerCustomTargeting(PCLCardTargetingManager.PCL, CombatManager.targeting);
        core.receivePostInitialize();
        for (PCLResources<?, ?, ?, ?> resources : getRegisteredResources()) {
            resources.receivePostInitialize();
        }
        PCLLoadoutDataInfo.reloadLoadouts();
        PCLCharacterAnimation.postInitialize();

        for (AbstractCard c : CardLibrary.getAllCards()) {
            if (c instanceof PCLCard) {
                c.initializeDescription();
            }
        }
    }

    public static void receiveEditCharacters() {
        core.receiveEditCharacters();
        for (PCLResources<?, ?, ?, ?> resources : getRegisteredResources()) {
            resources.receiveEditCharacters();
        }
    }

    public static void receiveEditKeywords() {
        core.receiveEditKeywords();
        for (PCLResources<?, ?, ?, ?> resources : getRegisteredResources()) {
            resources.receiveEditKeywords();
        }
    }

    public static void receiveEditStrings() {
        core.receiveEditStrings();
        for (PCLResources<?, ?, ?, ?> resources : getRegisteredResources()) {
            resources.receiveEditStrings();
        }
    }

    public static void registerCommands() {
        ConsoleCommand.addCommand("augment", AugmentCommand.class);
        ConsoleCommand.addCommand("effekseer", EffekseerCommand.class);
        ConsoleCommand.addCommand("infuseaugment", InfuseAugmentCommand.class);
        ConsoleCommand.addCommand("jumpanywhere", JumpAnywhereCommand.class);
        ConsoleCommand.addCommand("obtain", ObtainCardCommand.class);
        ConsoleCommand.addCommand("obtaincustom", ObtainCustomCardCommand.class);
        ConsoleCommand.addCommand("obtaincustompotion", ObtainCustomPotionCommand.class);
        ConsoleCommand.addCommand("obtaincustomrelic", ObtainCustomRelicCommand.class);
        ConsoleCommand.addCommand("obtaindeck", ObtainDeckCardCommand.class);
        ConsoleCommand.addCommand("obtaindeckcustom", ObtainDeckCustomCardCommand.class);
        ConsoleCommand.addCommand("reloadcustom", ReloadCustomCommand.class);
        ConsoleCommand.addCommand("unlockall", UnlockAllCommand.class);
        ConsoleCommand.addCommand("unlockascension", UnlockAscensionCommand.class);
        ConsoleCommand.addCommand("unlocklevel", UnlockLevelCommand.class);
    }

    public static void registerResource(PCLResources<?, ?, ?, ?> resources) {
        registerResource(resources, true, true);
    }

    public static void registerResource(PCLResources<?, ?, ?, ?> resources, boolean registerColor, boolean registerPlayerClass) {
        if (core == null) {
            throw new RuntimeException("No core present");
        }
        resourceList.add(resources);
        if (resources.cardColor != null && registerColor) {
            colorResourceMap.putIfAbsent(resources.cardColor, resources);
        }
        if (resources.playerClass != null && registerPlayerClass) {
            playerResourceMap.putIfAbsent(resources.playerClass, resources);
        }
        resources.initializeColor();
    }

    public static void registerRewards() {
        AugmentReward.Serializer augmentSerializer = new AugmentReward.Serializer();
        BaseMod.registerCustomReward(PCLEnum.Rewards.AUGMENT, augmentSerializer, augmentSerializer);
    }

    // Powers must be initialized before other customs because they are factored into checks
    public static void reloadCustoms() {
        PCLCustomFlagInfo.initialize();
        PCLCustomLoadoutInfo.initialize();
        PCLCustomPowerSlot.initialize();
        PCLCustomOrbSlot.initialize();
        PCLCustomCardSlot.initialize();
        PCLCustomRelicSlot.initialize();
        PCLCustomPotionSlot.initialize();
        PCLCustomBlightSlot.initialize();
        PCLCustomAugmentSlot.initialize();
    }
}
