package pinacolada.actions.affinity;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.vfx.combat.PowerBuffEffect;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.effects.PCLEffects;
import pinacolada.misc.CombatManager;

public class AddAffinityLevel extends PCLActionWithCallback<PCLAffinity>
{
    public PCLAffinity affinity;
    public boolean showEffect;

    public AddAffinityLevel(PCLAffinity affinity, int amount)
    {
        super(ActionType.SPECIAL, Settings.ACTION_DUR_XFAST);

        this.affinity = affinity;

        if (affinity == null || AbstractDungeon.getMonsters().areMonstersBasicallyDead())
        {
            complete();
            return;
        }

        initialize(amount);
    }

    @Override
    protected void firstUpdate()
    {
        if (amount == 0 || shouldCancelAction())
        {
            complete(affinity);
            return;
        }

        CombatManager.playerSystem.addLevel(affinity, amount);
        if (showEffect)
        {
            CombatManager.playerSystem.flashAffinity(affinity);
            PCLEffects.List.add(new PowerBuffEffect(target.hb.cX - target.animX, target.hb.cY + target.hb.height / 2f, "+" + amount + " " + affinity.getTooltip().title));
        }
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (tickDuration(deltaTime))
        {
            complete(affinity);
        }
    }

    public AddAffinityLevel showEffect(boolean showEffect)
    {
        this.showEffect = showEffect;

        return this;
    }
}
