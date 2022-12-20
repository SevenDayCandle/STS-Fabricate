package pinacolada.resources;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.patches.EUIKeyword;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.augments.AugmentStrings;
import pinacolada.ui.characterSelection.PCLLoadoutsContainer;
import pinacolada.utilities.GameUtilities;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class PCLResources<T extends AbstractConfig, U extends PCLImages, V extends PCLTooltips>
        implements EditCharactersSubscriber, EditCardsSubscriber, EditKeywordsSubscriber,
                   EditRelicsSubscriber, EditStringsSubscriber, PostInitializeSubscriber,
                   AddAudioSubscriber
{
    public static final String JSON_AUGMENTS = "AugmentStrings.json";
    public static final String JSON_CARDS = "CardStrings.json";
    public static final String JSON_KEYWORDS = "KeywordStrings.json";
    public final AbstractCard.CardColor cardColor;
    public final AbstractPlayer.PlayerClass playerClass;
    public final PCLAbstractPlayerData data;
    public final T config;
    public final U images;
    public V tooltips;
    protected CharacterStrings characterStrings;
    protected final FileHandle testFolder;
    protected final String id;
    protected String defaultLanguagePath;
    protected boolean isLoaded;

    protected PCLResources(String id, AbstractCard.CardColor color, AbstractPlayer.PlayerClass playerClass, T config, U images, FuncT1<PCLAbstractPlayerData, PCLResources<T, U, V>> dataFunc)
    {
        this.id = id;
        this.cardColor = color;
        this.playerClass = playerClass;
        this.config = config;
        this.images = images;
        this.testFolder = new FileHandle("c:/temp/" + id + "-localization/");
        this.data = dataFunc != null ? dataFunc.invoke(this) : null;
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
        final Map localizationStrings = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "cards");
        final Map cardStrings = new HashMap<>();
        try
        {
            final Type typeToken = new TypeToken<Map<String, Map<String, CardStrings>>>()
            {
            }.getType();
            final Map map = new HashMap<>((Map) new Gson().fromJson(jsonString, typeToken));

            for (Object key1 : map.keySet())
            {
                final Map map3 = ((Map<Object, CardStrings>) map.get(key1));
                for (Object key2 : map3.keySet())
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

    protected ArrayList<String> getClassNamesFromJarFile(String prefix)
    {
        return GameUtilities.getClassNamesFromJarFile(this.getClass(), prefix);
    }

    public FileHandle getFallbackFile(String fileName)
    {
        return Gdx.files.internal("localization/" + id.toLowerCase() + "/eng/" + fileName);
    }

    public <T> T getFallbackStrings(String fileName, Type typeOfT)
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
        if (isBetaTranslation() && new File(testFolder.path() + "/" + fileName).isFile())
        {
            return Gdx.files.internal(testFolder.path() + "/" + fileName);
        }
        else
        {
            if (!PGR.isTranslationSupported(language))
            {
                language = Settings.GameLanguage.ENG;
            }

            return Gdx.files.internal("localization/" + id.toLowerCase() + "/" + language.name().toLowerCase() + "/" + fileName);
        }
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

    protected void initializeEvents()
    {
    }

    protected void initializeInternal()
    {
    }

    protected void initializeMonsters()
    {
    }

    protected void initializePotions()
    {
    }

    protected void initializePowers()
    {
    }

    protected void initializeRewards()
    {
    }

    public boolean isBetaTranslation()
    {
        return testFolder.isDirectory();
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

    protected void loadCustomCard(Class<?> type)
    {
        if (!PGR.canInstantiate(type))
        {
            return;
        }

        AbstractCard card;
        String id;

        try
        {
            card = (AbstractCard) type.getConstructor().newInstance();
            id = card.cardID;
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            e.printStackTrace();
            return;
        }

        if (UnlockTracker.isCardLocked(id))
        {
            UnlockTracker.unlockCard(id);
            card.isLocked = false;
        }

        BaseMod.addCard(card);
    }

    protected void loadCustomCards()
    {
        final String fullPrefix = PGR.PREFIX_CARDS + id;
        for (String s : getClassNamesFromJarFile(PGR.PREFIX_CARDS))
        {
            if (s.startsWith(fullPrefix))
            {
                try
                {
                    loadCustomCard(Class.forName(s));
                }
                catch (ClassNotFoundException e)
                {
                    EUIUtils.logWarning(null, "Class not found : " + s);
                }
            }
        }
    }

    protected void loadCustomNonBaseStrings(String path, ActionT1<String> loadFunc)
    {
        String json = getFallbackFile(path).readString(StandardCharsets.UTF_8.name());
        loadFunc.invoke(json);

        if (testFolder.isDirectory() || PGR.isTranslationSupported(Settings.language))
        {
            FileHandle file = getFile(Settings.language, path);
            if (file.exists())
            {
                String json2 = file.readString(StandardCharsets.UTF_8.name());
                loadFunc.invoke(json);
            }
        }
    }

    protected void loadCustomPotion(Class<?> type, AbstractPlayer.PlayerClass playerClass)
    {
        if (!PGR.canInstantiate(type))
        {
            return;
        }

        AbstractPotion potion;
        try
        {
            potion = (AbstractPotion) type.getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            e.printStackTrace();
            return;
        }

        BaseMod.addPotion(potion.getClass(), potion.liquidColor, potion.hybridColor, potion.spotsColor, potion.ID, playerClass);
    }

    protected void loadCustomPotions()
    {
        final String potionPrefix = PGR.PREFIX_POTIONS + id;
        for (String s : getClassNamesFromJarFile(PGR.PREFIX_POTIONS))
        {
            if (s.startsWith(potionPrefix))
            {
                try
                {
                    loadCustomPotion(Class.forName(s), playerClass);
                }
                catch (ClassNotFoundException e)
                {
                    EUIUtils.logWarning(null, "Class not found : " + s);
                }
            }
        }
    }

    protected void loadCustomPowers()
    {
        final String fullPrefix = PGR.PREFIX_POWERS + id;

        for (String s : getClassNamesFromJarFile(PGR.PREFIX_POWERS))
        {
            if (s.startsWith(fullPrefix))
            {
                try
                {
                    Class<?> type = Class.forName(s);
                    if (PGR.canInstantiate(type))
                    {
                        BaseMod.addPower((Class<AbstractPower>) type, createID(type.getSimpleName()));
                    }
                }
                catch (ClassNotFoundException e)
                {
                    EUIUtils.logWarning(null, "Class not found : " + s);
                }
            }
        }
    }

    protected void loadCustomRelic(Class<?> type, AbstractCard.CardColor color)
    {
        if (!PGR.canInstantiate(type))
        {
            return;
        }

        AbstractRelic relic;
        try
        {
            relic = (AbstractRelic) type.getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            e.printStackTrace();
            return;
        }

        if (color != null && color != AbstractCard.CardColor.COLORLESS)
        {
            BaseMod.addRelicToCustomPool(relic, color);
        }
        else
        {
            BaseMod.addRelic(relic, RelicType.SHARED);
        }
    }

    protected void loadCustomRelics()
    {
        final String fullPrefix = PGR.PREFIX_RELIC + id;

        for (String s : getClassNamesFromJarFile(PGR.PREFIX_RELIC))
        {
            if (s.startsWith(fullPrefix))
            {
                try
                {
                    //logger.info("Adding: " + s);

                    loadCustomRelic(Class.forName(s), cardColor);
                }
                catch (ClassNotFoundException e)
                {
                    EUIUtils.logWarning(PGR.class, "Class not found : " + s);
                }
            }
        }
    }

    protected void loadCustomStrings(Class<?> type)
    {
        loadCustomStrings(type, getFallbackFile(type.getSimpleName() + ".json"));

        if (isBetaTranslation() || PGR.isTranslationSupported(Settings.language))
        {
            loadCustomStrings(type, getFile(Settings.language, type.getSimpleName() + ".json"));
        }
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
        loadKeywords(getFallbackFile(JSON_KEYWORDS));

        if (isBetaTranslation() || PGR.isTranslationSupported(Settings.language))
        {
            loadKeywords(getFile(Settings.language, JSON_KEYWORDS));
        }
    }

    protected void loadKeywords(FileHandle file)
    {
        if (!file.exists())
        {
            EUIUtils.logWarning(this, "File not found: " + file.path());
            return;
        }

        String json = file.readString(String.valueOf(StandardCharsets.UTF_8));
        Map<String, EUIKeyword> items = EUIUtils.deserialize(json, new TypeToken<Map<String, EUIKeyword>>()
        {
        }.getType());

        for (Map.Entry<String, EUIKeyword> pair : items.entrySet())
        {
            String id = pair.getKey();
            EUIKeyword keyword = pair.getValue();
            EUITooltip tooltip = new EUITooltip(keyword);

            EUITooltip.registerID(id, tooltip);

            for (String name : keyword.NAMES)
            {
                EUITooltip.registerName(name, tooltip);
            }
        }
    }

    protected void postInitialize()
    {
        tooltips.initializeIcons();
        data.initialize();
        PCLLoadoutsContainer.preloadResources(data);
    }

    @Override
    public void receiveAddAudio()
    {
    }

    @Override
    public void receiveEditCards()
    {
    }

    @Override
    public void receiveEditCharacters()
    {
    }

    @Override
    public void receiveEditKeywords()
    {
        loadKeywords();
    }

    @Override
    public void receiveEditRelics()
    {
    }

    @Override
    public void receiveEditStrings()
    {
        loadCustomStrings(CharacterStrings.class);
        loadCustomCardStrings();
        loadCustomStrings(RelicStrings.class);
        loadCustomStrings(PowerStrings.class);
        loadCustomStrings(PotionStrings.class);
    }

    @Override
    public final void receivePostInitialize()
    {
        initializeEvents();
        initializeMonsters();
        initializePotions();
        initializeRewards();
        initializePowers();
        postInitialize();
        this.isLoaded = true;
    }
}
