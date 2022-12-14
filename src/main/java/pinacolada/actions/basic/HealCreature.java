package pinacolada.actions.basic;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.HealEffect;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.effects.PCLEffects;

public class HealCreature extends PCLActionWithCallback<Integer>
{
    protected final int playerHpLastTurn;
    protected boolean showEffect = true;
    protected boolean recover = false;

    public HealCreature(AbstractCreature source, AbstractCreature target, int amount)
    {
        super(ActionType.HEAL, Settings.ACTION_DUR_FAST);

        this.playerHpLastTurn = GameActionManager.playerHpLastTurn;
        this.canCancel = false;

        initialize(target, source, amount);

        if (amount <= 0)
        {
            complete(0);
        }
    }

    @Override
    protected void firstUpdate()
    {
        if (target.isPlayer)
        {
            if (recover)
            {
                amount = Math.min(amount, playerHpLastTurn - player.currentHealth);

                if (amount <= 0)
                {
                    complete(0);
                    return;
                }
            }
        }

        target.heal(amount, false);

        if (showEffect)
        {
            if (target.isPlayer)
            {
                AbstractDungeon.topPanel.panelHealEffect();
            }

            PCLEffects.Queue.add(new HealEffect(target.hb.cX - target.animX, target.hb.cY, amount));
        }

        complete(amount);
    }

    public HealCreature recover(boolean recover)
    {
        this.recover = recover;

        return this;
    }

    public HealCreature showEffect(boolean showEffect)
    {
        this.showEffect = showEffect;

        return this;
    }
}