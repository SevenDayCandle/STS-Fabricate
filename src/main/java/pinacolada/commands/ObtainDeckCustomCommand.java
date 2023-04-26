package pinacolada.commands;

import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;

public class ObtainDeckCustomCommand extends ObtainCustomCommand {

    public ObtainDeckCustomCommand() {
        super();
    }

    @Override
    protected void doAction(PCLCard copy) {
        PCLEffects.Queue.showAndObtain(copy);
    }
}
