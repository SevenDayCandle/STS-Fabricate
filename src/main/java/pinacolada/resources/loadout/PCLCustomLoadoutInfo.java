package pinacolada.resources.loadout;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import pinacolada.misc.PCLCustomLoadable;

import java.util.ArrayList;
import java.util.HashMap;

public class PCLCustomLoadoutInfo extends PCLCustomLoadable {
    private static final HashMap<AbstractCard.CardColor, ArrayList<PCLCustomLoadoutInfo>> CUSTOM_LOADOUTS = new HashMap<>();
    public static final String SUBFOLDER = "loadout";
    public String ID;
    public String author;
    public String name;
    public AbstractCard.CardColor color;
    public int unlockLevel;

    public static String getBaseIDPrefix(AbstractCard.CardColor color) {
        return getBaseIDPrefix(SUBFOLDER, color);
    }

    public static ArrayList<PCLCustomLoadoutInfo> getCards(AbstractCard.CardColor color) {
        if (color == null) {
            return EUIUtils.flattenList(CUSTOM_LOADOUTS.values());
        }
        if (!CUSTOM_LOADOUTS.containsKey(color)) {
            CUSTOM_LOADOUTS.put(color, new ArrayList<>());
        }
        return CUSTOM_LOADOUTS.get(color);
    }

    public static boolean isIDDuplicate(String input, AbstractCard.CardColor color) {
        return isIDDuplicate(input, getCards(color));
    }

    protected static String makeNewID(AbstractCard.CardColor color) {
        return makeNewID(getBaseIDPrefix(color), getCards(color));
    }

    @Override
    protected String getSubfolderPath() {
        return SUBFOLDER;
    }
}