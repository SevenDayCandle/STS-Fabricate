package pinacolada.commands;

import pinacolada.cards.base.PCLCard;
import pinacolada.effects.PCLEffects;

public class ObtainDeckCardCommand extends ObtainCardCommand {

    public ObtainDeckCardCommand() {
        super();
    }

    @Override
    protected void doAction(PCLCard copy) {
        PCLEffects.Queue.showAndObtain(copy);
    }
}
