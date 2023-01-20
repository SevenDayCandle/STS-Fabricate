package pinacolada;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import pinacolada.effects.SFX;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;

@SpireInitializer //
public class PCLModInitializer implements OnStartBattleSubscriber, PostBattleSubscriber, PostDeathSubscriber,
                                          PreStartGameSubscriber, OnPlayerTurnStartPostDrawSubscriber, OnPlayerTurnStartSubscriber,
                                          EditCardsSubscriber, EditRelicsSubscriber, PostInitializeSubscriber, AddAudioSubscriber
{
    private static final PCLModInitializer instance = new PCLModInitializer();

    public static void initialize()
    {
        BaseMod.subscribe(instance);
        PGR.initialize();
    }

    @Override
    public void receiveAddAudio()
    {
        SFX.initialize();
    }

    @Override
    public void receiveEditCards()
    {
        PGR.loadCustomCards();
    }

    @Override
    public void receiveEditRelics()
    {
        PGR.loadCustomRelics();
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom)
    {
        CombatManager.onBattleStart();
    }

    @Override
    public void receiveOnPlayerTurnStart()
    {
        CombatManager.atPlayerTurnStart();
    }

    @Override
    public void receiveOnPlayerTurnStartPostDraw()
    {
        CombatManager.atPlayerTurnStartPostDraw();
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom)
    {
        CombatManager.onBattleEnd();
    }

    @Override
    public void receivePostInitialize()
    {
        PGR.loadCustomPotions();
        PGR.loadCustomPowers();
        PGR.registerRewards();
        CombatManager.initializeEvents();
    }

    @Override
    public void receivePreStartGame()
    {
        CombatManager.onGameStart();
    }

    @Override
    public void receivePostDeath()
    {
        CombatManager.onAfterDeath();
    }
}