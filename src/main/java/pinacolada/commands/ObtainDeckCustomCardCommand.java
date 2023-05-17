package pinacolada.commands;

import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;

public class ObtainDeckCustomCardCommand extends ObtainCustomCardCommand {

    public ObtainDeckCustomCardCommand() {
        super();
    }

    @Override
    protected void doAction(PCLCard copy) {
        PCLEffects.Queue.showAndObtain(copy);
    }
}
