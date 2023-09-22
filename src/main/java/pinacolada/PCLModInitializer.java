package pinacolada;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import pinacolada.dungeon.CombatManager;
import pinacolada.effects.PCLSFX;
import pinacolada.patches.library.BlightHelperPatches;
import pinacolada.resources.PGR;

@SpireInitializer //
public class PCLModInitializer implements PostBattleSubscriber, PostDeathSubscriber, PostDrawSubscriber, OnStartBattleSubscriber,
                                          PreStartGameSubscriber, OnPlayerTurnStartPostDrawSubscriber, OnPlayerTurnStartSubscriber,
                                          EditCardsSubscriber, EditCharactersSubscriber, EditKeywordsSubscriber, EditRelicsSubscriber, EditStringsSubscriber, PostInitializeSubscriber, AddAudioSubscriber {
    private static final PCLModInitializer instance = new PCLModInitializer();

    public static void initialize() {
        BaseMod.subscribe(instance);
        PGR.initialize();
    }

    @Override
    public void receiveAddAudio() {
        PCLSFX.initialize();
    }

    @Override
    public void receiveEditCards() {
        PGR.loadCustomCards();
    }

    @Override
    public void receiveEditCharacters() {
        PGR.receiveEditCharacters();
    }

    @Override
    public void receiveEditKeywords() {
        PGR.receiveEditKeywords();
    }

    @Override
    public void receiveEditRelics() {
        PGR.loadCustomRelics();
    }

    @Override
    public void receiveEditStrings() {
        PGR.receiveEditStrings();
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        CombatManager.onBattleStartPostRefresh();
    }

    @Override
    public void receiveOnPlayerTurnStart() {
        CombatManager.atPlayerTurnStart();
    }

    @Override
    public void receiveOnPlayerTurnStartPostDraw() {
        CombatManager.atPlayerTurnStartPostDraw();
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        CombatManager.onBattleEnd();
    }

    @Override
    public void receivePostDeath() {
        CombatManager.onAfterDeath();
    }

    @Override
    public void receivePostDraw(AbstractCard abstractCard) {
        CombatManager.onAfterDraw(abstractCard);
    }

    @Override
    public void receivePostInitialize() {
        PGR.loadCustomPotions();
        PGR.loadCustomPowers();
        BlightHelperPatches.loadCustomBlights();
        PGR.registerRewards();
        CombatManager.initializeEvents();
        PGR.postInitialize();
    }

    @Override
    public void receivePreStartGame() {
        CombatManager.onGameStart();
    }
}