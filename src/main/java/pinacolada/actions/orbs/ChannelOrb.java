package pinacolada.actions.orbs;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.orbs.PCLOrbHelper;

import java.util.ArrayList;

public class ChannelOrb extends PCLActionWithCallback<ArrayList<AbstractOrb>>
{
    private final ArrayList<AbstractOrb> channeledOrbs = new ArrayList<>();
    private final PCLOrbHelper orbConstructor;
    private AbstractOrb orb;
    private boolean autoEvoke;

    public ChannelOrb(AbstractOrb orb)
    {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.orb = orb;
        this.orbConstructor = null;
        this.autoEvoke = true;

        initialize(1);
    }

    public ChannelOrb(PCLOrbHelper orbConstructor, int amount)
    {
        super(ActionType.SPECIAL, Settings.FAST_MODE ? Settings.ACTION_DUR_XFAST : Settings.ACTION_DUR_FAST);

        this.orb = null;
        this.orbConstructor = orbConstructor;
        this.autoEvoke = true;

        initialize(amount);
    }

    public ChannelOrb autoEvoke(boolean autoEvoke)
    {
        this.autoEvoke = autoEvoke;

        return this;
    }

    @Override
    protected void updateInternal(float deltaTime)
    {
        if (!tickDuration(deltaTime))
        {
            return;
        }

        if (amount > 0)
        {
            if (orbConstructor != null)
            {
                orb = orbConstructor.create();
            }

            if (autoEvoke)
            {
                AbstractDungeon.player.channelOrb(orb);
                channeledOrbs.add(orb);
            }
            else
            {
                for (AbstractOrb o : AbstractDungeon.player.orbs)
                {
                    if (o instanceof EmptyOrbSlot)
                    {
                        AbstractDungeon.player.channelOrb(orb);
                        channeledOrbs.add(orb);
                        break;
                    }
                }
            }

            amount -= 1;
        }

        if (amount > 0)
        {
            //repeat
            duration = startDuration;
            isDone = false;
        }
        else
        {
            complete(channeledOrbs);
        }
    }
}
