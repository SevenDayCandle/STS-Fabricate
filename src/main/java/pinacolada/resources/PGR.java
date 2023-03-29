package pinacolada.resources;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.RelicType;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.cardFilter.CountingPanel;
import extendedui.ui.controls.EUIImage;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.annotations.*;
import pinacolada.augments.AugmentStrings;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.commands.*;
import pinacolada.effects.EffekseerEFK;
import pinacolada.misc.PCLAffinityPanelFilter;
import pinacolada.misc.PCLDungeon;
import pinacolada.resources.pcl.PCLCoreResources;
import pinacolada.rewards.pcl.AugmentReward;
import pinacolada.skills.PSkill;
import pinacolada.ui.cardEditor.PCLCustomCardSelectorScreen;
import pinacolada.ui.cardView.PCLSingleCardPopup;
import pinacolada.ui.characterSelection.PCLCharacterSelectProvider;
import pinacolada.ui.characterSelection.PCLLoadoutEditor;
import pinacolada.ui.characterSelection.PCLSeriesSelectScreen;
import pinacolada.ui.combat.PCLCombatScreen;
import pinacolada.ui.customRun.PCLCustomRunScreen;
import pinacolada.ui.debug.PCLDebugAugmentPanel;
import pinacolada.ui.debug.PCLDebugCardPanel;
import pinacolada.ui.menu.*;
import pinacolada.utilities.GameUtilities;

import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Pattern;

import static pinacolada.utilities.GameUtilities.screenH;
import static pinacolada.utilities.GameUtilities.screenW;

// Copied and modified from STS-AnimatorMod
public class PGR
{
    private static final HashMap<AbstractCard.CardColor, PCLResources<?,?,?,?>> colorResourceMap = new HashMap<>();
    private static final HashMap<AbstractPlayer.PlayerClass, PCLResources<?,?,?,?>> playerResourceMap = new HashMap<>();
    public static final String BASE_PREFIX = "pcl";
    public static final PCLDungeon dungeon = PCLDungeon.register();
    public static PCLCoreResources core;
    public static PCLMainConfig config;
    public static PCLAugmentPanelItem augmentPanel;
    public static PCLAffinityPoolModule affinityFilters;
    public static PCLAugmentScreen augmentScreen;
    public static PCLCharacterSelectProvider charSelectProvider;
    public static PCLCombatScreen combatScreen;
    public static PCLCustomCardSelectorScreen customCards;
    public static PCLCustomRunScreen customMode;
    public static PCLFtueScreen ftueScreen;
    public static PCLLibraryModule libraryFilters;
    public static PCLLoadoutEditor loadoutEditor;
    public static PCLSeriesSelectScreen seriesSelection;
    public static PCLSingleCardPopup cardPopup;
    public static PCLDebugAugmentPanel debugAugments;
    public static PCLDebugCardPanel debugCards;
    public static EUIImage blackScreen;

    public static void registerResource(PCLResources<?,?,?,?> resources)
    {
        if (core == null)
        {
            throw new RuntimeException("No core present");
        }
        colorResourceMap.put(resources.cardColor, resources);
        playerResourceMap.put(resources.playerClass, resources);
        initialize(resources);
    }

    public static String createID(String prefix, String suffix)
    {
        return prefix + ":" + suffix;
    }

    public static AugmentStrings getAugmentStrings(String stringID)
    {
        return AugmentStrings.STRINGS.get(stringID);
    }

    public static String getBlightImage(String id)
    {
        return getPng(id, "blights");
    }

    public static String getBlightOutlineImage(String id)
    {
        return getPng(id, "blights/outline");
    }

    public static BlightStrings getBlightStrings(String blightID)
    {
        return getLanguagePack().getBlightString(blightID);
    }

    public static String getCardImage(String id)
    {
        return getPng(id, "cards");
    }

    public static CardStrings getCardStrings(String cardID)
    {
        return getLanguagePack().getCardStrings(cardID);
    }

    public static CharacterStrings getCharacterStrings(String characterID)
    {
        return getLanguagePack().getCharacterString(characterID);
    }

    public static CharacterStrings getCharacterStrings(AbstractCard.CardColor cardColor)
    {
        PCLResources<?,?,?,?> resources = colorResourceMap.getOrDefault(cardColor, null);
        return resources != null ? resources.getCharacterStrings() : null;
    }

    public static CharacterStrings getCharacterStrings(AbstractPlayer.PlayerClass player)
    {
        PCLResources<?,?,?,?> resources = playerResourceMap.getOrDefault(player, null);
        return resources != null ? resources.getCharacterStrings() : null;
    }

    public static EventStrings getEventStrings(String eventID)
    {
        return getLanguagePack().getEventString(eventID);
    }

    public static LocalizedStrings getLanguagePack()
    {
        return CardCrawlGame.languagePack;
    }

    public static String getMonsterImage(String id)
    {
        return getPng(id, "monsters");
    }

    public static MonsterStrings getMonsterStrings(String monsterID)
    {
        return getLanguagePack().getMonsterStrings(monsterID);
    }

    public static OrbStrings getOrbStrings(String orbID)
    {
        return getLanguagePack().getOrbString(orbID);
    }

    public static PCLAbstractPlayerData getPlayerData(AbstractCard.CardColor playerClass)
    {
        return getResources(playerClass).data;
    }

    public static PCLAbstractPlayerData getPlayerData(AbstractPlayer.PlayerClass playerClass)
    {
        return getResources(playerClass).data;
    }

    public static Collection<PCLResources<?,?,?,?>> getRegisteredResources()
    {
        return colorResourceMap.values();
    }

    public static PCLResources<?,?,?,?> getResources(AbstractCard.CardColor cardColor)
    {
        return colorResourceMap.getOrDefault(cardColor, core);
    }

    public static PCLResources<?,?,?,?> getResources(AbstractPlayer.PlayerClass playerClass)
    {
        return playerResourceMap.getOrDefault(playerClass, core);
    }

    public static String getPng(String id, String subFolder)
    {
        String[] s = id.split(Pattern.quote(":"), 2);
        return "images/" + s[0] + "/" + subFolder + "/" + s[1].replace(":", "_") + ".png";
    }

    public static String getPowerImage(String id)
    {
        return getPng(id, "powers");
    }

    public static PowerStrings getPowerStrings(String powerID)
    {
        return getLanguagePack().getPowerStrings(powerID);
    }

    public static String getRelicImage(String id)
    {
        return getPng(id, "relics");
    }

    public static RelicStrings getRelicStrings(String relicID)
    {
        return getLanguagePack().getRelicStrings(relicID);
    }

    public static String getRewardImage(String id)
    {
        return getPng(id, "ui/rewards");
    }

    public static RunModStrings getRunModStrings(String stringID)
    {
        return getLanguagePack().getRunModString(stringID);
    }

    public static StanceStrings getStanceString(String stanceID)
    {
        return getLanguagePack().getStanceString(stanceID);
    }

    public static void initialize()
    {
        if (core != null)
        {
            throw new RuntimeException("Already Initialized");
        }

        config = new PCLMainConfig();
        core = new PCLCoreResources();
        initialize(core);
    }

    protected static void initialize(PCLResources<?,?,?,?> resources)
    {
        resources.initializeColor();

        BaseMod.subscribe(resources);
    }

    public static boolean isLoaded()
    {
        return core != null && core.isLoaded && EUIUtils.all(getRegisteredResources(), r -> r.isLoaded);
    }

    public static void loadCustomCards()
    {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleCard.class))
        {
            try
            {
                AbstractCard card = (AbstractCard) ct.getConstructor().newInstance();
                if (UnlockTracker.isCardLocked(card.cardID))
                {
                    UnlockTracker.unlockCard(card.cardID);
                    card.isLocked = false;
                }

                BaseMod.addCard(card);
            }
            catch (Exception e)
            {
                EUIUtils.logError(PGR.class, "Failed to load potion " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void loadCustomPotions()
    {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisiblePotion.class))
        {
            try
            {
                AbstractPotion potion = (AbstractPotion) ct.getConstructor().newInstance();
                // TODO get color from potion and add it to the proper pool
                BaseMod.addPotion(potion.getClass(), potion.liquidColor, potion.hybridColor, potion.spotsColor, potion.ID);
            }
            catch (Exception e)
            {
                EUIUtils.logError(PGR.class, "Failed to load potion " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void loadCustomPowers()
    {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisiblePower.class))
        {
            try
            {
                VisibleBlight a = ct.getAnnotation(VisibleBlight.class);
                String field = a.id();
                if (field != null)
                {
                    String id = ReflectionHacks.getPrivateStatic(ct, field);
                    BaseMod.addPower((Class<? extends AbstractPower>) ct, id);
                }
                else
                {
                    BaseMod.addPower((Class<? extends AbstractPower>) ct, PGR.core.createID(ct.getSimpleName()));
                }
            }
            catch (Exception e)
            {
                EUIUtils.logError(PSkill.class, "Failed to load power " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void loadCustomRelics()
    {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleRelic.class))
        {
            try
            {
                AbstractRelic relic = (AbstractRelic) ct.getConstructor().newInstance();
                // TODO get color from relic and add it to the proper pool
                BaseMod.addRelic(relic, RelicType.SHARED);
            }
            catch (Exception e)
            {
                EUIUtils.logError(PGR.class, "Failed to load relic " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void postInitialize()
    {
        PGR.registerCommands();
        PCLAbstractPlayerData.postInitialize();
        PGR.config.load(CardCrawlGame.saveSlot);
        PGR.config.initializeOptions();
        initializeUI();
        PSkill.initialize();
        PCLAugment.initialize();
        PCLCustomCardSlot.initialize();
        EffekseerEFK.initialize();
        CountingPanel.register(new PCLAffinityPanelFilter());
    }

    protected static void initializeUI()
    {
        PGR.combatScreen = new PCLCombatScreen();
        PGR.cardPopup = new PCLSingleCardPopup();
        PGR.seriesSelection = new PCLSeriesSelectScreen();
        PGR.loadoutEditor = new PCLLoadoutEditor();
        PGR.customCards = new PCLCustomCardSelectorScreen();
        PGR.customMode = new PCLCustomRunScreen();
        PGR.charSelectProvider = new PCLCharacterSelectProvider();
        PGR.affinityFilters = new PCLAffinityPoolModule(EUI.cardFilters);
        PGR.libraryFilters = new PCLLibraryModule(EUI.customLibraryScreen);
        PGR.augmentScreen = new PCLAugmentScreen();
        PGR.augmentPanel = new PCLAugmentPanelItem();
        PGR.ftueScreen = new PCLFtueScreen();
        PGR.blackScreen = new EUIImage(EUIRM.images.fullSquare.texture(), new EUIHitbox(screenW(1), screenH(1)))
                .setPosition(screenW(0.5f), screenH(0.5f))
                .setColor(0, 0, 0, 0.9f);
        try
        {
            PGR.debugAugments = new PCLDebugAugmentPanel();
            PGR.debugCards = new PCLDebugCardPanel();
        }
        catch (Error | Exception e)
        {
            e.printStackTrace();
            EUIUtils.logError(PGR.class, "Failed to load ImGUI debug panels. These panels will not be available in ImGUI.");
        }

        EUI.addBattleSubscriber(PGR.combatScreen);
        EUI.addSubscriber(PGR.cardPopup);
        EUI.setCustomCardFilter(AbstractCard.CardColor.COLORLESS, PGR.affinityFilters);
        EUI.setCustomCardFilter(AbstractCard.CardColor.CURSE, PGR.affinityFilters);
        EUI.setCustomCardLibraryModule(AbstractCard.CardColor.COLORLESS, PGR.libraryFilters);
        EUI.setCustomCardLibraryModule(AbstractCard.CardColor.CURSE, PGR.libraryFilters);
        for (PCLResources<?,?,?,?> r : PGR.getRegisteredResources())
        {
            EUI.setCustomCardFilter(r.cardColor, PGR.affinityFilters);
            EUI.setCustomCardLibraryModule(r.cardColor, PGR.libraryFilters);
        }
    }

    public static void registerCommands()
    {
        ConsoleCommand.addCommand("augment", AugmentCommand.class);
        ConsoleCommand.addCommand("effekseer", EffekseerCommand.class);
        ConsoleCommand.addCommand("exportcsv", ExportCSVCommand.class);
        ConsoleCommand.addCommand("obtain", ObtainCommand.class);
        ConsoleCommand.addCommand("obtaincustom", ObtainCustomCommand.class);
        ConsoleCommand.addCommand("obtaindeck", ObtainDeckCommand.class);
        ConsoleCommand.addCommand("obtaindeckcustom", ObtainDeckCustomCommand.class);
        ConsoleCommand.addCommand("reloadcustom", ReloadCustomCommand.class);
        ConsoleCommand.addCommand("unlockall", UnlockAllCommand.class);
    }

    public static void registerRewards()
    {
        AugmentReward.Serializer augmentSerializer = new AugmentReward.Serializer();
        BaseMod.registerCustomReward(PCLEnum.Rewards.AUGMENT, augmentSerializer, augmentSerializer);
    }
}
