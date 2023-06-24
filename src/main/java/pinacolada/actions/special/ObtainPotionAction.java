package pinacolada.actions.special;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import pinacolada.actions.PCLAction;

public class ObtainPotionAction extends PCLAction<AbstractPotion> {
    protected final AbstractPotion potion;

    public ObtainPotionAction(AbstractPotion potion) {
        super(ActionType.SPECIAL);

        this.potion = potion;
        this.isRealtime = true;
        this.canCancel = false;

        initialize(1);
    }

    @Override
    protected void firstUpdate() {
        if (potion == null) {
            complete(null);
            return;
        }
        AbstractDungeon.player.obtainPotion(potion);
        complete(potion);
    }
}
