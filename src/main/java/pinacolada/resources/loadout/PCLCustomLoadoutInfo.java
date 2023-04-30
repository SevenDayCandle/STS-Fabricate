package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.misc.PCLCustomLoadable;

public class PCLCustomLoadoutInfo extends PCLCustomLoadable {
    public static final String SUBFOLDER = "loadout";

    public String ID;
    public String author;
    public String name;
    public AbstractCard.CardColor color;
    public int unlockLevel;

    @Override
    protected String getSubfolderPath() {
        return SUBFOLDER;
    }
}
