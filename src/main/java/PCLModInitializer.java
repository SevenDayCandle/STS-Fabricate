import basemod.BaseMod;
import basemod.interfaces.*;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import pinacolada.misc.CombatStats;
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
        CombatStats.onBattleStart();
    }

    @Override
    public void receiveOnPlayerTurnStart()
    {
        CombatStats.atPlayerTurnStart();
    }

    @Override
    public void receiveOnPlayerTurnStartPostDraw()
    {
        CombatStats.atPlayerTurnStartPostDraw();
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom)
    {
        CombatStats.onBattleEnd();
    }

    @Override
    public void receivePreStartGame()
    {
        CombatStats.onGameStart();
    }

    @Override
    public void receivePostDeath()
    {
        CombatStats.onAfterDeath();
    }
}