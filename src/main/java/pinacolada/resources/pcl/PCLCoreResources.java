package pinacolada.resources.pcl;

import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.*;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.ui.AbstractScreen;
import extendedui.utilities.EUIFontHelper;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.AttackEffects;
import pinacolada.effects.PCLEffekseerEFX;
import pinacolada.effects.SFX;
import pinacolada.powers.replacement.GenericFadingPower;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.rewards.pcl.AugmentReward;
import pinacolada.skills.PSkill;
import pinacolada.ui.PCLAugmentPanelItem;
import pinacolada.ui.cardEditor.PCLCustomCardSelectorScreen;
import pinacolada.ui.cardReward.CardAffinityPanel;
import pinacolada.ui.cardView.PCLSingleCardPopup;
import pinacolada.ui.cards.PCLAugmentScreen;
import pinacolada.ui.characterSelection.PCLCharacterSelectProvider;
import pinacolada.ui.characterSelection.PCLLoadoutEditor;
import pinacolada.ui.characterSelection.PCLSeriesSelectScreen;
import pinacolada.ui.combat.PCLCombatScreen;
import pinacolada.ui.common.PCLAffinityPoolModule;
import pinacolada.ui.common.PCLFtueScreen;
import pinacolada.ui.common.PCLLibraryModule;
import pinacolada.ui.customRun.PCLCustomRunScreen;
import pinacolada.ui.debug.PCLDebugAugmentPanel;
import pinacolada.ui.debug.PCLDebugCardPanel;

import static pinacolada.resources.PGR.Enums.Characters.THE_DECIDER;

public class PCLCoreResources extends PCLResources<PCLCoreConfig, PCLCoreImages, PCLCoreTooltips>
{
    public static final String ID = PGR.BASE_PREFIX;
    public final PCLDungeonData dungeon = PCLDungeonData.register(createID("Data"));
    public final PCLCoreStrings strings = new PCLCoreStrings(this);
    public AbstractScreen currentScreen;
    public PCLAugmentPanelItem augmentPanel;
    public CardAffinityPanel cardAffinities;
    public PCLAffinityPoolModule affinityFilters;
    public PCLAugmentScreen augmentScreen;
    public PCLCharacterSelectProvider charSelectProvider;
    public PCLCombatScreen combatScreen;
    public PCLCustomCardSelectorScreen customCards;
    public PCLCustomRunScreen customMode;
    public PCLFtueScreen ftueScreen;
    public PCLLibraryModule libraryFilters;
    public PCLLoadoutEditor loadoutEditor;
    public PCLSeriesSelectScreen seriesSelection;
    public PCLSingleCardPopup cardPopup;
    public PCLDebugAugmentPanel debugAugments;
    public PCLDebugCardPanel debugCards;
    protected String defaultLanguagePath;


    public PCLCoreResources()
    {
        super(ID, AbstractCard.CardColor.COLORLESS, THE_DECIDER, null, new PCLCoreConfig(), new PCLCoreImages(ID));
    }

    public String createID(String suffix)
    {
        return createID(ID, suffix);
    }

    public void initializeInternal()
    {
    }

    protected void initializeMonsters()
    {
    }

    protected void initializeEvents()
    {
        /*PCLEvent.RegisterEvents();*/
    }

    protected void initializeRewards()
    {
        AugmentReward.Serializer augmentSerializer = new AugmentReward.Serializer();
        BaseMod.registerCustomReward(Enums.Rewards.AUGMENT, augmentSerializer, augmentSerializer);
    }

    protected void initializeAudio()
    {
        SFX.initialize();
    }

    protected void initializeCards()
    {
        EUIUtils.logInfo(this, "InitializeCards();");

        tooltips = new PCLCoreTooltips();
        strings.initialize();
        loadCustomCards();
    }

    protected void initializePowers()
    {
        BaseMod.addPower(GenericFadingPower.class, GenericFadingPower.POWER_ID);
        // LoadCustomPowers();
    }

    protected void initializeStrings()
    {
        EUIUtils.logInfo(this, "InitializeStrings();");

        loadCustomStrings(OrbStrings.class);
        loadCustomCardStrings();
        loadCustomStrings(RelicStrings.class);
        loadCustomStrings(PowerStrings.class);
        loadCustomStrings(UIStrings.class);
        loadCustomStrings(EventStrings.class);
        loadCustomStrings(PotionStrings.class);
        loadCustomStrings(MonsterStrings.class);
        loadCustomStrings(BlightStrings.class);
        loadCustomStrings(RunModStrings.class);
        loadCustomStrings(StanceStrings.class);
        loadAugmentStrings();

        EUIFontHelper.initialize();
    }

    protected void initializeTextures()
    {
    }

    protected void initializeRelics()
    {
        EUIUtils.logInfo(this, "InitializeRelics();");

        loadCustomRelics();
    }

    protected void initializePotions()
    {
        EUIUtils.logInfo(this, "InitializePotions();");

        loadCustomPotions();
    }

    protected void initializeKeywords()
    {
        EUIUtils.logInfo(this, "InitializeKeywords();");

        loadKeywords();
    }

    protected void postInitialize()
    {
        AttackEffects.initialize();
        PGR.registerCommands();
        PCLAbstractPlayerData.postInitialize();
        config.load(CardCrawlGame.saveSlot);
        config.initializeOptions();
        tooltips.initializeIcons();
        initializeUI();
        PSkill.initialize();
        PCLAugment.initialize();
        PCLCustomCardSlot.initialize();
        PCLEffekseerEFX.initialize();
    }

    protected void initializeUI()
    {
        cardAffinities = new CardAffinityPanel();
        combatScreen = new PCLCombatScreen();
        cardPopup = new PCLSingleCardPopup();
        seriesSelection = new PCLSeriesSelectScreen();
        loadoutEditor = new PCLLoadoutEditor();
        customCards = new PCLCustomCardSelectorScreen();
        customMode = new PCLCustomRunScreen();
        charSelectProvider = new PCLCharacterSelectProvider();
        affinityFilters = new PCLAffinityPoolModule(EUI.cardFilters);
        libraryFilters = new PCLLibraryModule(EUI.customLibraryScreen);
        augmentScreen = new PCLAugmentScreen();
        augmentPanel = new PCLAugmentPanelItem();
        ftueScreen = new PCLFtueScreen();
        debugAugments = new PCLDebugAugmentPanel();
        debugCards = new PCLDebugCardPanel();

        EUI.addBattleSubscriber(combatScreen);
        EUI.addSubscriber(cardPopup);
        EUI.setCustomCardFilter(AbstractCard.CardColor.COLORLESS, affinityFilters);
        EUI.setCustomCardFilter(AbstractCard.CardColor.CURSE, affinityFilters);
        EUI.setCustomCardFilter(Enums.Cards.THE_CONJURER, affinityFilters);
        EUI.setCustomCardFilter(Enums.Cards.THE_DECIDER, affinityFilters);
        EUI.setCustomCardFilter(Enums.Cards.THE_ETERNAL, affinityFilters);
        EUI.setCustomCardPoolModule(Enums.Cards.THE_CONJURER, cardAffinities);
        EUI.setCustomCardPoolModule(Enums.Cards.THE_DECIDER, cardAffinities);
        EUI.setCustomCardPoolModule(Enums.Cards.THE_ETERNAL, cardAffinities);
        EUI.setCustomCardLibraryModule(AbstractCard.CardColor.COLORLESS, libraryFilters);
        EUI.setCustomCardLibraryModule(AbstractCard.CardColor.CURSE, libraryFilters);
        EUI.setCustomCardLibraryModule(Enums.Cards.THE_CONJURER, libraryFilters);
        EUI.setCustomCardLibraryModule(Enums.Cards.THE_DECIDER, libraryFilters);
        EUI.setCustomCardLibraryModule(Enums.Cards.THE_ETERNAL, libraryFilters);
    }

}