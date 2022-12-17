package pinacolada;

import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;

@SpireInitializer //
public class PCLModInitializer implements OnStartBattleSubscriber, PostBattleSubscriber, PostDeathSubscriber,
                                          PreStartGameSubscriber, OnPlayerTurnStartPostDrawSubscriber, OnPlayerTurnStartSubscriber
{
    private static final PCLModInitializer instance = new PCLModInitializer();

    public static void initialize()
    {
        BaseMod.subscribe(instance);
        PGR.initialize();
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