package pinacolada.resources;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
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
import org.apache.commons.lang3.StringUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.misc.AugmentStrings;
import pinacolada.misc.LoadoutStrings;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// Copied and modified from STS-AnimatorMod
public abstract class PCLResources<T extends PCLAbstractPlayerData, U extends PCLImages, V extends PCLTooltips, W extends PCLStrings>
        implements EditCharactersSubscriber, EditKeywordsSubscriber, EditStringsSubscriber, PostInitializeSubscriber
{
    private static final Type AUGMENT_TYPE = new TypeToken<Map<String, Map<String, AugmentStrings>>>() {}.getType();
    private static final Type GROUPED_CARD_TYPE = new TypeToken<Map<String, Map<String, CardStrings>>>() {}.getType();
    private static final Type LOADOUT_TYPE = new TypeToken<Map<String, Map<String, LoadoutStrings>>>() {}.getType();
    public static final String JSON_AUGMENTS = "AugmentStrings.json";
    public static final String JSON_CARDS = "CardStrings.json";
    public static final String JSON_KEYWORDS = "KeywordStrings.json";
    public static final String JSON_LOADOUTS = "LoadoutStrings.json";
    public final AbstractCard.CardColor cardColor;
    public final AbstractPlayer.PlayerClass playerClass;
    public final boolean usePCLFrame;
    public final T data;
    public final U images;
    public V tooltips;
    public W strings;
    protected CharacterStrings characterStrings;
    protected final String id;
    protected boolean isLoaded;

    protected PCLResources(String id, AbstractCard.CardColor color, AbstractPlayer.PlayerClass playerClass, U images)
    {
        this(id, color, playerClass, images, true);
    }

    protected PCLResources(String id, AbstractCard.CardColor color, AbstractPlayer.PlayerClass playerClass, U images, boolean usePCLFrame)
    {
        this.id = id;
        this.usePCLFrame = usePCLFrame;
        this.cardColor = color;
        this.playerClass = playerClass;
        this.images = images;
        this.data = getData();
    }

    public static void loadAugmentStrings(String jsonString)
    {
        final Type typeToken = new TypeToken<Map<String, AugmentStrings>>()
        {
        }.getType();
        AugmentStrings.STRINGS.putAll(new HashMap<String, AugmentStrings>(EUIUtils.deserialize(jsonString, typeToken)));
    }

    public static void loadGroupedCardStrings(String jsonString)
    {
        final Map<String, CardStrings> localizationStrings = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "cards");
        final Map<String, CardStrings> cardStrings = new HashMap<>();
        try
        {
            final Map<String, Map<String, CardStrings>> map = new HashMap<>(new Gson().fromJson(jsonString, GROUPED_CARD_TYPE));

            for (String key1 : map.keySet())
            {
                final Map<String, CardStrings> map3 = (map.get(key1));
                for (String key2 : map3.keySet())
                {
                    cardStrings.put(key2, map3.get(key2));
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            EUIUtils.getLogger(PGR.class).error("Loading card strings failed. Using default method.");
            BaseMod.loadCustomStrings(CardStrings.class, jsonString);
            return;
        }

        localizationStrings.putAll(cardStrings);
    }

    public static void loadLoadoutStrings(String jsonString)
    {
        final Type typeToken = new TypeToken<Map<String, LoadoutStrings>>()
        {
        }.getType();
        LoadoutStrings.STRINGS.putAll(new HashMap<String, LoadoutStrings>(EUIUtils.deserialize(jsonString, typeToken)));
    }

    public String createID(String suffix)
    {
        return PGR.createID(id, suffix);
    }

    public CharacterStrings getCharacterStrings()
    {
        if (characterStrings == null)
        {
            characterStrings = PGR.getCharacterStrings(StringUtils.capitalize(id));
        }
        return characterStrings;
    }

    public FileHandle getFallbackFile(String fileName)
    {
        return Gdx.files.internal("localization/" + id.toLowerCase() + "/eng/" + fileName);
    }

    public <Z> Z getFallbackStrings(String fileName, Type typeOfT)
    {
        FileHandle file = getFallbackFile(fileName);
        if (!file.exists())
        {
            EUIUtils.logWarning(this, "File not found: " + file.path());
            return null;
        }

        String json = file.readString(String.valueOf(StandardCharsets.UTF_8));
        return EUIUtils.deserialize(json, typeOfT);
    }

    public FileHandle getFile(Settings.GameLanguage language, String fileName)
    {
        return Gdx.files.internal("localization/" + id.toLowerCase() + "/" + language.name().toLowerCase() + "/" + fileName);
    }

    public int getUnlockCost()
    {
        return getUnlockCost(0, true);
    }

    public int getUnlockCost(int level, boolean relative)
    {
        if (relative)
        {
            level += getUnlockLevel();
        }

        return level <= 4 ? 300 + (level * 500) : 1000 + (level * 300);
    }

    public int getUnlockLevel()
    {
        return UnlockTracker.getUnlockLevel(playerClass);
    }

    public UIStrings getUIStrings(String stringID)
    {
        return PGR.getLanguagePack().getUIString(PGR.createID(id, stringID));
    }

    protected void initializeColor()
    {
    }

    public boolean isSelected()
    {
        return GameUtilities.isPlayerClass(playerClass);
    }

    protected void loadAugmentStrings()
    {
        loadCustomNonBaseStrings(JSON_AUGMENTS, PCLResources::loadAugmentStrings);
    }

    protected void loadCustomCardStrings()
    {
        loadCustomNonBaseStrings(JSON_CARDS, PCLResources::loadGroupedCardStrings);
    }

    protected void loadLoadoutStrings()
    {
        loadCustomNonBaseStrings(JSON_LOADOUTS, PCLResources::loadLoadoutStrings);
    }

    protected void loadCustomNonBaseStrings(String path, ActionT1<String> loadFunc)
    {
        String json = getFallbackFile(path).readString(StandardCharsets.UTF_8.name());
        loadFunc.invoke(json);

        FileHandle file = getFile(Settings.language, path);
        if (file.exists())
        {
            String json2 = file.readString(StandardCharsets.UTF_8.name());
            loadFunc.invoke(json);
        }
    }

    protected void loadCustomStrings(Class<?> type)
    {
        loadCustomStrings(type, getFallbackFile(type.getSimpleName() + ".json"));
        loadCustomStrings(type, getFile(Settings.language, type.getSimpleName() + ".json"));
    }

    protected void loadCustomStrings(Class<?> type, FileHandle file)
    {
        if (file.exists())
        {
            BaseMod.loadCustomStrings(type, file.readString(String.valueOf(StandardCharsets.UTF_8)));
        }
        else
        {
            EUIUtils.logWarning(this, "File not found: " + file.path());
        }
    }

    protected void loadKeywords()
    {
        EUI.registerKeywords(getFallbackFile(JSON_KEYWORDS));
        EUI.registerKeywords(getFile(Settings.language, JSON_KEYWORDS));
    }

    protected void postInitialize()
    {
        tooltips.initializeIcons();
        data.initialize();
    }

    @Override
    public void receiveEditCharacters()
    {
    }

    @Override
    public void receiveEditKeywords()
    {
        loadKeywords();
        setupTooltips();
    }

    @Override
    public void receiveEditStrings()
    {
        loadLoadoutStrings();
        loadCustomStrings(CharacterStrings.class);
        loadCustomCardStrings();
        loadCustomStrings(RelicStrings.class);
        loadCustomStrings(PowerStrings.class);
        loadCustomStrings(PotionStrings.class);
        loadCustomStrings(UIStrings.class);
    }

    @Override
    public final void receivePostInitialize()
    {
        postInitialize();
        this.isLoaded = true;
    }

    public void setupTooltips()
    {
        tooltips = getTooltips();
        strings = getStrings();
    }

    // The colorless pool is filled with ALL colorless cards by default. This will determine whether a colorless card is allowed when playing as a PCL character
    public boolean containsColorless(AbstractCard card)
    {
        return card instanceof PCLCard;
    }

    // The colorless pool is filled with ALL colorless cards by default. This will determine whether a colorless card should be removed when playing as a non-PCL character
    public boolean filterColorless(AbstractCard card)
    {
        return card instanceof PCLCard;
    }

    // Intercepts CardLibrary's getCopy to return a different card
    public PCLCardData getReplacement(String cardID)
    {
        return null;
    }

    // Intercepts the creation of Ascender's Bane upon starting a run
    public PCLCardData getAscendersBane() {return null;}

    public abstract T getData();

    public abstract V getTooltips();

    public abstract W getStrings();
}
