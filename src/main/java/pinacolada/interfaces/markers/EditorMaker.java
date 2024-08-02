package pinacolada.interfaces.markers;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT0;
import pinacolada.misc.PCLCustomEditorLoadable;
import pinacolada.skills.PSkill;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EditorMaker<T, U> {
    public static final TypeToken<HashMap<Settings.GameLanguage, String[]>> TDescriptions = new TypeToken<HashMap<Settings.GameLanguage, String[]>>() {};
    public static final TypeToken<HashMap<Settings.GameLanguage, String>> TNames = new TypeToken<HashMap<Settings.GameLanguage, String>>() {};

    default EditorMaker<T, U> addPPower(PSkill<?> effect) {
        return addPPower(effect, false);
    }

    default EditorMaker<T, U> addPPower(PSkill<?> effect, boolean makeCopy) {
        if (makeCopy && effect != null) {
            effect = effect.makeCopy();
        }
        getPowers().add(effect);

        return this;
    }

    default EditorMaker<T, U> addPSkill(PSkill<?> effect) {
        return addPSkill(effect, false);
    }

    default EditorMaker<T, U> addPSkill(PSkill<?> effect, boolean makeCopy) {
        if (makeCopy && effect != null) {
            effect = effect.makeCopy();
        }
        getMoves().add(effect);

        return this;
    }

    default void clearPowers() {
        getPowers().clear();
    }

    default void clearSkills() {
        getMoves().clear();
    }

    default HashMap<Settings.GameLanguage, String[]> createDescMap() {
        HashMap<Settings.GameLanguage, String[]> map = new HashMap<>();
        for (Map.Entry<Settings.GameLanguage, U> entry : getLanguageMap().entrySet()) {
            String[] descs = getDescString(entry.getValue());
            map.put(entry.getKey(), descs != null ? descs.clone() : new String[]{});
        }
        return map;
    }

    default String[] getDescString() {
        return getDescString(getStringsForLanguage(Settings.language));
    }

    default String[] getDescString(U item) {
        try {
            for (Field f : item.getClass().getFields()) {
                switch (f.getName()) {
                    case "EXTENDED_DESCRIPTION":
                    case "DESCRIPTIONS":
                    case "DESCRIPTION":
                        Object str = f.get(item);
                        if (str instanceof String[]) {
                            return (String[]) str;
                        }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(this, "Object doesn't have a description array?");
        }
        return new String[] {};
    }

    default PSkill<?> getEffectAtIndex(int i) {
        List<PSkill<?>> moves = getMoves();
        if (moves.size() > i) {
            return moves.get(i);
        }
        i -= moves.size();
        List<PSkill<?>> powers = getPowers();
        if (powers.size() > i) {
            return powers.get(i);
        }
        return null;
    }

    default U getStringsForLanguage(Settings.GameLanguage language) {
        HashMap<Settings.GameLanguage, U> languageMap = getLanguageMap();
        return languageMap.getOrDefault(language,
                languageMap.getOrDefault(Settings.GameLanguage.ENG,
                        !languageMap.isEmpty() ? languageMap.entrySet().iterator().next().getValue() : getDefaultStrings()));
    }

    default void parseLanguageStrings(String languageStrings, PCLCustomEditorLoadable.EffectItemForm form) {
        HashMap<Settings.GameLanguage, U> languageMap = getLanguageMap();
        languageMap.clear();
        try {
            HashMap<Settings.GameLanguage, String> names = EUIUtils.deserialize(languageStrings, TNames.getType());
            HashMap<Settings.GameLanguage, String[]> descs = EUIUtils.deserialize(form.textMap, TDescriptions.getType());
            for (Settings.GameLanguage lang : names.keySet()) {
                U entry = (U) getStrings().getClass().newInstance();
                setDescString(entry, descs.get(lang));
                setNameString(entry, names.get(lang));
                languageMap.put(lang, entry);
            }
        }
        catch (Exception e) {
            try {
                HashMap<Settings.GameLanguage, U> copy = EUIUtils.deserialize(languageStrings, typeToken());
                languageMap.putAll(copy);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        setTextForLanguage();
    }

    default void safeLoadValue(ActionT0 loadFunc) {
        try {
            loadFunc.invoke();
        }
        catch (Exception e) {
            // Using info since this can be really long
            EUIUtils.logInfoIfDebug(this, "Failed to load field: " + e.getLocalizedMessage());
        }
    }

    default void setDescString(U item, String[] items) {
        try {
            for (Field f : item.getClass().getFields()) {
                switch (f.getName()) {
                    case "EXTENDED_DESCRIPTION":
                    case "DESCRIPTIONS":
                    case "DESCRIPTION":
                        if (f.getType().isAssignableFrom(String[].class)) {
                            f.set(item, items);
                            return;
                        }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(this, "Object doesn't have a description array?");
        }
    }

    default EditorMaker<T, U> setLanguageMap(HashMap<Settings.GameLanguage, U> languageMap) {
        HashMap<Settings.GameLanguage, U> lm = getLanguageMap();
        for (Map.Entry<Settings.GameLanguage, U> entry : languageMap.entrySet()) {
            lm.put(entry.getKey(), copyStrings(entry.getValue()));
        }
        return setTextForLanguage();
    }

    default EditorMaker<T, U> setLanguageMapEntry(Settings.GameLanguage language) {
        return setLanguageMapEntry(language, getStrings());
    }

    default EditorMaker<T, U> setLanguageMapEntry(Settings.GameLanguage language, U entry) {
        getLanguageMap().put(language, entry);
        return this;
    }

    default void setNameString(U item, String name) {
        try {
            for (Field f : item.getClass().getFields()) {
                if (f.getName().equals("NAME")) {
                    if (f.getType().isAssignableFrom(String.class)) {
                        f.set(item, name);
                        return;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            EUIUtils.logError(this, "Object doesn't have a name array?");
        }
    }

    default EditorMaker<T, U> setPPower(PSkill<?>... effect) {
        return setPPower(Arrays.asList(effect));
    }

    default EditorMaker<T, U> setPPower(Iterable<PSkill<?>> currentEffects) {
        return setPPower(currentEffects, false, true);
    }

    default EditorMaker<T, U> setPPower(Iterable<PSkill<?>> currentEffects, boolean makeCopy, boolean clear) {
        if (clear) {
            clearPowers();
        }
        for (PSkill<?> be : currentEffects) {
            addPPower(be, makeCopy);
        }
        return this;
    }

    default EditorMaker<T, U> setPSkill(PSkill<?>... effect) {
        return setPSkill(Arrays.asList(effect));
    }

    default EditorMaker<T, U> setPSkill(Iterable<PSkill<?>> currentEffects) {
        return setPSkill(currentEffects, false, true);
    }

    default EditorMaker<T, U> setPSkill(Iterable<PSkill<?>> currentEffects, boolean makeCopy, boolean clear) {
        if (clear) {
            clearSkills();
        }
        for (PSkill<?> be : currentEffects) {
            addPSkill(be, makeCopy);
        }
        return this;
    }

    default EditorMaker<T, U> setTextForLanguage() {
        return setTextForLanguage(Settings.language);
    }

    default EditorMaker<T, U> setTextForLanguage(Settings.GameLanguage language) {
        U strings = getStringsForLanguage(language);
        return setText(strings);
    }

    default void updateTextFromMap(HashMap<Settings.GameLanguage, String[]> incoming) {
        HashMap<Settings.GameLanguage, U> map = getLanguageMap();
        for (Map.Entry<Settings.GameLanguage, String[]> entry : incoming.entrySet()) {
            U strings = map.get(entry.getKey());
            setDescString(strings, entry.getValue());
        }
    }

    U copyStrings(U initial);

    U copyStrings(U initial, U dest);

    T create();

    AbstractCard.CardColor getCardColor();

    U getDefaultStrings();

    Texture getImage();

    List<PSkill<?>> getMoves();

    List<PSkill<?>> getPowers();

    U getStrings();

    HashMap<Settings.GameLanguage, U> getLanguageMap();

    <V extends EditorMaker<T, U>> V makeCopy();

    <V extends EditorMaker<T, U>> V setText(U cardStrings);

    Type typeToken();
}
