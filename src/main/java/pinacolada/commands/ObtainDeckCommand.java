package pinacolada.commands;

import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;

public class ObtainDeckCommand extends ObtainCommand
{

    public ObtainDeckCommand()
    {
        super();
    }

    @Override
    protected void doAction(PCLCard copy)
    {
        PCLEffects.Queue.showAndObtain(copy);
    }
}
