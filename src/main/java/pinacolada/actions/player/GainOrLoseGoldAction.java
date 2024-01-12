package pinacolada.actions.player;

import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.GainPennyEffect;
import pinacolada.actions.PCLAction;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.PCLSFX;

// Copied and modified from STS-AnimatorMod
public class GainOrLoseGoldAction extends PCLAction<Integer> {
    private static final int MAX_COINS = 30;
    private boolean showEffect = true;

    public GainOrLoseGoldAction(int amount) {
        this(AbstractDungeon.player, amount);
    }

    public GainOrLoseGoldAction(AbstractCreature source, int amount) {
        super(ActionType.SPECIAL);
        initialize(amount);
    }

    @Override
    protected void firstUpdate() {
        if (shouldCancelAction() || (amount == 0)) {
            complete(null);
            return;
        }

        if (amount > 0) {
            AbstractDungeon.player.gainGold(amount);
            if (showEffect) {
                for (int i = 0; i < amount; i++) {
                    PCLEffects.Queue.add(new GainPennyEffect(player.hb.cX, player.hb.cY + (player.hb.height / 2)));
                }
            }
        }
        // INVERT amount because lose gold expects a positive amount to remove
        else {
            AbstractDungeon.player.loseGold(-amount);
            if (showEffect) {
                PCLSFX.play(PCLSFX.EVENT_PURCHASE);
            }
        }

        complete(amount);
    }

    public GainOrLoseGoldAction showEffect(boolean showEffect) {
        this.showEffect = showEffect;
        return this;
    }
}
