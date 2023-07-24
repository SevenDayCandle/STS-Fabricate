package pinacolada.resources.loadout;

import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.cards.base.PCLDynamicCardData;
import pinacolada.misc.LoadoutStrings;
import pinacolada.utilities.GameUtilities;

import java.util.HashMap;

// TODO make editor for making this
public class PCLCustomLoadout extends PCLLoadout {
    private static final TypeToken<HashMap<Settings.GameLanguage, LoadoutStrings>> TStrings = new TypeToken<HashMap<Settings.GameLanguage, LoadoutStrings>>() {
    };
    public final HashMap<Settings.GameLanguage, LoadoutStrings> languageMap = new HashMap<>();
    public PCLCustomLoadoutInfo info;
    public LoadoutStrings strings;

    public PCLCustomLoadout(AbstractCard.CardColor color, String id, int unlockLevel) {
        super(color, id, unlockLevel);
    }

    public PCLCustomLoadout(PCLCustomLoadoutInfo info) {
        super(info.color, info.ID, info.unlockLevel);
        this.info = info;
    }

    public String getAuthor() {
        return strings != null && strings.AUTHOR != null ? strings.AUTHOR : "";
    }

    public String getName() {
        return strings != null && strings.NAME != null ? strings.NAME : ID;
    }

    protected static LoadoutStrings getInitialStrings() {
        LoadoutStrings retVal = new LoadoutStrings();
        retVal.NAME = GameUtilities.EMPTY_STRING;
        retVal.AUTHOR = GameUtilities.EMPTY_STRING;
        return retVal;
    }

    public LoadoutStrings getStringsForLanguage(Settings.GameLanguage language) {
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        languageMap.size() > 0 ? languageMap.entrySet().iterator().next().getValue() : getInitialStrings()));
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
        return EUIUtils.deserialize(languageStrings, TStrings.getType());
    }

    public PCLCustomLoadout setLanguageMap(HashMap<Settings.GameLanguage, LoadoutStrings> languageMap) {
        this.languageMap.putAll(languageMap);
        return this;
    }

    public PCLCustomLoadout setLanguageMapEntry(Settings.GameLanguage language) {
        this.languageMap.put(language, this.strings);
        return this;
    }
}
