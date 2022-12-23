package pinacolada.resources.pcl;

import basemod.BaseMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.*;
import extendedui.EUI;
import extendedui.ui.AbstractScreen;
import pinacolada.augments.PCLAugment;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.effects.AttackEffects;
import pinacolada.effects.PCLEffekseerEFX;
import pinacolada.effects.SFX;
import pinacolada.resources.PCLAbstractPlayerData;
import pinacolada.resources.PCLEnum;
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
        super(ID, AbstractCard.CardColor.COLORLESS, AbstractPlayer.PlayerClass.IRONCLAD, new PCLCoreConfig(), new PCLCoreImages(ID), null);
    }

    protected void initializeEvents()
    {
        /*PCLEvent.RegisterEvents();*/
    }

    public void initializeInternal()
    {
    }

    protected void initializeMonsters()
    {
    }

    protected void initializePotions()
    {
        loadCustomPotions();
    }

    protected void initializePowers()
    {
    }

    protected void initializeRewards()
    {
        AugmentReward.Serializer augmentSerializer = new AugmentReward.Serializer();
        BaseMod.registerCustomReward(PCLEnum.Rewards.AUGMENT, augmentSerializer, augmentSerializer);
    }

    public void receiveAddAudio()
    {
        SFX.initialize();
    }

    public void receiveEditCards()
    {
        tooltips = new PCLCoreTooltips();
        strings.initialize();
        loadCustomCards();
    }

    public void receiveEditKeywords()
    {
        loadKeywords();
    }

    public void receiveEditRelics()
    {
        loadCustomRelics();
    }

    public void receiveEditStrings()
    {
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
        EUI.setCustomCardLibraryModule(AbstractCard.CardColor.COLORLESS, libraryFilters);
        EUI.setCustomCardLibraryModule(AbstractCard.CardColor.CURSE, libraryFilters);
        for (PCLResources r : PGR.getAllResources())
        {
            EUI.setCustomCardFilter(r.cardColor, affinityFilters);
            EUI.setCustomCardPoolModule(r.cardColor, cardAffinities);
            EUI.setCustomCardLibraryModule(r.cardColor, libraryFilters);
        }
    }

}