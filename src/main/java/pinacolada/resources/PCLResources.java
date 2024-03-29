package pinacolada.resources;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUI;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.TemplateCardData;
import pinacolada.misc.AugmentStrings;
import pinacolada.misc.LoadoutStrings;
import pinacolada.resources.loadout.PCLLoadout;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static pinacolada.utilities.GameUtilities.JSON_EXT;

// Copied and modified from STS-AnimatorMod
public abstract class PCLResources<T extends PCLPlayerData<?, ?, ?>, U extends AbstractImages, V extends AbstractTooltips, W extends AbstractStrings> {
    private static final Type AUGMENT_TYPE = new TypeToken<Map<String, Map<String, AugmentStrings>>>() {
    }.getType();
    private static final Type GROUPED_CARD_TYPE = new TypeToken<Map<String, Map<String, CardStrings>>>() {
    }.getType();
    private static final Type LOADOUT_TYPE = new TypeToken<Map<String, Map<String, LoadoutStrings>>>() {
    }.getType();
    private static final String LOCALIZATION_ROOT = "localization/";
    private static final String PREF_AUGMENTS = "AugmentStrings";
    private static final String PREF_CARDS = "CardStrings";
    private static final String PREF_KEYWORDS = "KeywordStrings";
    private static final String PREF_LOADOUTS = "LoadoutStrings";
    public final String ID;
    public final AbstractCard.CardColor cardColor;
    public final AbstractPlayer.PlayerClass playerClass;
    public final boolean usePCLFrame;
    public final T data;
    public final U images;
    protected boolean isLoaded;
    public V tooltips;
    public W strings;

    protected PCLResources(String id, AbstractCard.CardColor color, AbstractPlayer.PlayerClass playerClass, U images) {
        this(id, color, playerClass, images, true);
    }

    protected PCLResources(String id, AbstractCard.CardColor color, AbstractPlayer.PlayerClass playerClass, U images, boolean usePCLFrame) {
        this.ID = id;
        this.usePCLFrame = usePCLFrame;
        this.cardColor = color;
        this.playerClass = playerClass;
        this.images = images;
        this.data = getData();
    }

    public static void loadGroupedCardStrings(String jsonString) {
        final Map<String, CardStrings> localizationStrings = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "cards");
        final Map<String, CardStrings> cardStrings = new HashMap<>();
        try {
            final Map<String, Map<String, CardStrings>> map = new HashMap<>(new Gson().fromJson(jsonString, GROUPED_CARD_TYPE));

            for (String key1 : map.keySet()) {
                final Map<String, CardStrings> map3 = (map.get(key1));
                for (String key2 : map3.keySet()) {
                    cardStrings.put(key2, map3.get(key2));
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            EUIUtils.getLogger(PGR.class).error("Loading card strings failed. Using default method.");
            BaseMod.loadCustomStrings(CardStrings.class, jsonString);
            return;
        }

        localizationStrings.putAll(cardStrings);
    }

    // The colorless pool is filled with ALL colorless cards by default. This will determine whether a colorless card is allowed when playing as a PCL character
    public boolean containsColorless(AbstractCard card) {
        if (card instanceof PCLCard) {
            PCLLoadout loadout = ((PCLCard) card).cardData.loadout;
            return loadout == null || loadout.isCore() || !loadout.isLocked();
        }
        return true;
    }

    public String createID(String suffix) {
        return PGR.createID(ID, suffix);
    }

    // The colorless pool is filled with ALL colorless cards by default. This will determine whether a colorless card should be removed when playing as a non-PCL character
    public boolean filterColorless(AbstractCard card) {
        return card instanceof PCLCard && ((PCLCard) card).cardData.resources == this;
    }

    // Intercepts the creation of Ascender's Bane upon starting a run
    public PCLCardData getAscendersBane() {
        return null;
    }

    // Intercepts cards generated from events
    public String getEventReplacement(String cardID) {
        return null;
    }

    public FileHandle getFallbackFile(String fileName) {
        return Gdx.files.internal(LOCALIZATION_ROOT + ID.toLowerCase() + "/eng/" + fileName);
    }

    public <Z> Z getFallbackStrings(String fileName, Type typeOfT) {
        FileHandle file = getFallbackFile(fileName);
        if (!file.exists()) {
            EUIUtils.logWarning(this, "File not found: " + file.path());
            return null;
        }

        String json = file.readString(String.valueOf(StandardCharsets.UTF_8));
        return EUIUtils.deserialize(json, typeOfT);
    }

    public FileHandle getFile(Settings.GameLanguage language, String fileName) {
        return Gdx.files.internal(LOCALIZATION_ROOT + ID.toLowerCase() + "/" + language.name().toLowerCase() + "/" + fileName);
    }

    // Intercepts CardLibrary's getCopy to return a different card. By default, this prevents example templates from showing up for regular characters
    public String getReplacement(String cardID) {
        PCLCardData data = PCLCardData.getStaticData(cardID);
        return data instanceof TemplateCardData ? ((TemplateCardData) data).originalID : null;
    }

    public UIStrings getUIStrings(String stringID) {
        return PGR.getLanguagePack().getUIString(PGR.createID(ID, stringID));
    }

    public int getUnlockCost() {
        return getUnlockCost(0, true);
    }

    public int getUnlockCost(int level, boolean relative) {
        if (relative) {
            level += getUnlockLevel();
        }

        return level <= 4 ? 300 + (level * 500) : 1000 + (level * 300);
    }

    public int getUnlockLevel() {
        return UnlockTracker.getUnlockLevel(playerClass);
    }

    protected void initializeColor() {
    }

    public boolean isSelected() {
        return GameUtilities.isPlayerClass(playerClass);
    }

    protected void loadAugmentStrings() {
        loadCustomNonBaseStrings(PREF_AUGMENTS + JSON_EXT, PGR::addAugmentStrings);
    }

    public void loadAugmentStrings(String name) {
        loadCustomNonBaseStrings(PREF_AUGMENTS + name + JSON_EXT, PGR::addAugmentStrings);
    }

    protected void loadCustomCardStrings() {
        loadCustomNonBaseStrings(PREF_CARDS + JSON_EXT, PCLResources::loadGroupedCardStrings);
    }

    public void loadCustomCardStrings(String name) {
        loadCustomNonBaseStrings(PREF_CARDS + name + JSON_EXT, PCLResources::loadGroupedCardStrings);
    }

    public void loadCustomNonBaseStrings(String path, ActionT1<String> loadFunc) {
        FileHandle fallback = getFallbackFile(path);
        if (fallback != null) {
            String json = getFallbackFile(path).readString(StandardCharsets.UTF_8.name());
            loadFunc.invoke(json);

            FileHandle file = getFile(Settings.language, path);
            if (file.exists()) {
                String json2 = file.readString(StandardCharsets.UTF_8.name());
                loadFunc.invoke(json);
            }
        }
    }

    protected void loadCustomStrings(Class<?> type) {
        loadCustomStrings(type, getFallbackFile(type.getSimpleName() + JSON_EXT));
        loadCustomStrings(type, getFile(Settings.language, type.getSimpleName() + JSON_EXT));
    }

    public void loadCustomStrings(Class<?> type, String name) {
        loadCustomStrings(type, getFallbackFile(type.getSimpleName() + name + JSON_EXT));
        loadCustomStrings(type, getFile(Settings.language, type.getSimpleName() + name + JSON_EXT));
    }

    protected void loadCustomStrings(Class<?> type, FileHandle file) {
        if (file.exists()) {
            BaseMod.loadCustomStrings(type, file.readString(String.valueOf(StandardCharsets.UTF_8)));
        }
        else {
            EUIUtils.logWarning(this, "File not found: " + file.path());
        }
    }

    protected void loadKeywords() {
        String kw = PREF_KEYWORDS + JSON_EXT;
        EUI.registerKeywords(getFallbackFile(kw));
        EUI.registerKeywords(getFile(Settings.language, kw));
    }

    protected void loadLoadoutStrings() {
        loadCustomNonBaseStrings(PREF_LOADOUTS + JSON_EXT, PGR::addLoadoutStrings);
    }

    public void loadLoadoutStrings(String id) {
        loadCustomNonBaseStrings(PREF_LOADOUTS + id + JSON_EXT, PGR::addLoadoutStrings);
    }

    protected void postInitialize() {
        tooltips.initializeIcons();
        if (data != null) {
            data.initialize();
        }
    }

    public void receiveEditCharacters() {
        if (data != null) {
            data.registerCharacter();
        }
    }

    public void receiveEditKeywords() {
        loadKeywords();
        setupTooltips();
    }

    public void receiveEditStrings() {
        loadLoadoutStrings();
        loadCustomStrings(CharacterStrings.class);
        loadCustomCardStrings();
        loadCustomStrings(RelicStrings.class);
        loadCustomStrings(PowerStrings.class);
        loadCustomStrings(PotionStrings.class);
        loadCustomStrings(BlightStrings.class);
        loadCustomStrings(MonsterStrings.class);
        loadCustomStrings(UIStrings.class);
    }

    public final void receivePostInitialize() {
        postInitialize();
        this.isLoaded = true;
    }

    public void reload() {
        if (data != null) {
            data.reload();
        }
    }

    public void setupTooltips() {
        tooltips = getTooltips();
        strings = getStrings();
    }

    public abstract T getData();

    public abstract W getStrings();

    public abstract V getTooltips();
}
