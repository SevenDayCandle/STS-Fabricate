package pinacolada.powers.special;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import pinacolada.actions.PCLActions;
import pinacolada.powers.PCLPower;

public class BatonPassPower extends PCLPower
{
    public static final String POWER_ID = createFullID(BatonPassPower.class);

    public BatonPassPower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);

        initialize(amount);
    }

    public void onUseCard(AbstractCard card, UseCardAction action)
    {
        super.onUseCard(card, action);
        PCLActions.bottom.progressCooldown(card, amount);
        removePower();
    }
}
