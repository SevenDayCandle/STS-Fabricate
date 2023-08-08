package pinacolada.resources.loadout;

import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import pinacolada.misc.LoadoutStrings;
import pinacolada.utilities.GameUtilities;

import java.util.HashMap;

public class PCLCustomLoadout extends PCLLoadout {
    private static final TypeToken<HashMap<Settings.GameLanguage, LoadoutStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, LoadoutStrings>>() {
    };
    public HashMap<Settings.GameLanguage, LoadoutStrings> languageMap;
    public PCLCustomLoadoutInfo info;
    public LoadoutStrings strings;

    public PCLCustomLoadout(PCLCustomLoadoutInfo info) {
        super(info.color, info.ID, info.unlockLevel);
        this.info = info;
        languageMap = parseLanguageStrings(info.languageStrings);
        refreshStrings();
    }

    public static LoadoutStrings getInitialStrings() {
        LoadoutStrings retVal = new LoadoutStrings();
        retVal.NAME = GameUtilities.EMPTY_STRING;
        retVal.AUTHOR = GameUtilities.EMPTY_STRING;
        return retVal;
    }

    public static LoadoutStrings getStringsForLanguage(HashMap<Settings.GameLanguage, LoadoutStrings> languageMap) {
        return getStringsForLanguage(languageMap, Settings.language);
    }

    public static LoadoutStrings getStringsForLanguage(HashMap<Settings.GameLanguage, LoadoutStrings> languageMap, Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
    }

    public static HashMap<Settings.GameLanguage, LoadoutStrings> parseLanguageStrings(String languageStrings) {
        HashMap<Settings.GameLanguage, LoadoutStrings> map = EUIUtils.deserialize(languageStrings, TStrings.getType());
        return map != null ? map : new HashMap<>();
    }

    public void commitChanges() {
        if (info != null) {
            info.ID = ID;
            info.color = color;
            info.languageStrings = EUIUtils.serialize(languageMap);
            info.commit();
        }
    }

    public String getAuthor() {
        return strings != null && strings.AUTHOR != null ? strings.AUTHOR : "";
    }


    protected AbstractCard.CardRarity getLoadoutCardRarity() {
        return AbstractCard.CardRarity.UNCOMMON;
    }

    public String getName() {
        return strings != null && strings.NAME != null ? strings.NAME : ID;
    }

    public void refreshStrings() {
        this.strings = getStringsForLanguage(languageMap);
    }
}
