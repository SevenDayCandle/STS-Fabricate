package pinacolada.actions.player;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import pinacolada.actions.PCLAction;
import pinacolada.utilities.GameEffects;

public class GainGold extends PCLAction
{
    protected boolean showCoins;

    public GainGold(int amount, boolean showCoins)
    {
        super(ActionType.SPECIAL);

        this.showCoins = showCoins;
        this.canCancel = false;

        initialize(amount);
    }

    @Override
    protected void firstUpdate()
    {
        if (amount > 0)
        {
            CardCrawlGame.sound.play("GOLD_JINGLE");

            if (showCoins)
            {
                for (int i = 0; i < amount; ++i)
                {
                    GameEffects.Queue.add(new GainPennyEffect(player.hb.cX, player.hb.cY + (player.hb.height / 2)));
                }
            }

            player.gainGold(amount);
        }

        complete();
    }
}
