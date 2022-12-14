package pinacolada.actions.special;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.EscapeAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import pinacolada.actions.PCLActionWithCallback;
import pinacolada.utilities.GameActions;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.List;

public class SendMinionsAway extends PCLActionWithCallback<List<AbstractMonster>>
{
    protected final ArrayList<AbstractMonster> minions = new ArrayList<>();

    public SendMinionsAway()
    {
        super(AbstractGameAction.ActionType.TEXT);

        initialize(1);
    }

    @Override
    protected void firstUpdate()
    {
        for (AbstractMonster m : GameUtilities.getEnemies(true))
        {
            if (m.hasPower(MinionPower.POWER_ID))
            {
                GameActions.bottom.add(new EscapeAction(m));
                minions.add(m);
            }
        }

        complete(minions);
    }
}
