package pinacolada.actions.special;

import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLAction;
import pinacolada.interfaces.providers.CooldownProvider;

public class CooldownProgressAction extends PCLAction<Boolean>
{
    protected final CooldownProvider provider;
    public CooldownProgressAction(CooldownProvider provider, AbstractCreature source, AbstractCreature target, int amount)
    {
        super(ActionType.SPECIAL);

        this.provider = provider;

        initialize(source, target, amount);
    }

    @Override
    protected void firstUpdate()
    {
        complete(provider.progressCooldownAndTrigger(source, target, amount));
    }
}
