package pinacolada.resources;

import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.utilities.GameUtilities;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public abstract class PCLResources<T extends AbstractConfig, U extends PCLImages, V extends PCLTooltips> extends PGR
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
    protected final FileHandle testFolder;
    protected final String prefix;
    protected String defaultLanguagePath;
    protected boolean isLoaded;

    protected PCLResources(String prefix, AbstractCard.CardColor color, AbstractPlayer.PlayerClass playerClass, PCLAbstractPlayerData data, T config, U images)
    {
        this.prefix = prefix;
        this.cardColor = color;
        this.playerClass = playerClass;
        this.config = config;
        this.images = images;
        this.testFolder = new FileHandle("c:/temp/" + prefix + "-localization/");
        this.data = data;
        if (this.data != null)
        {
            this.data.resources = this;
        }
    }

    public String createID(String suffix)
    {
        return createID(prefix, suffix);
    }

    public FileHandle getFallbackFile(String fileName)
    {
        return Gdx.files.internal("localization/" + prefix.toLowerCase() + "/eng/" + fileName);
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
            if (!isTranslationSupported(language))
            {
                language = Settings.GameLanguage.ENG;
            }

            return Gdx.files.internal("localization/" + prefix.toLowerCase() + "/" + language.name().toLowerCase() + "/" + fileName);
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
        return getLanguagePack().getUIString(PGR.createID(prefix, stringID));
    }

    protected void initializeAudio()
    {
    }

    protected void initializeCards()
    {
    }

    protected void initializeCharacter()
    {
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

    protected void initializeKeywords()
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

    protected void initializeRelics()
    {
    }

    protected void initializeRewards()
    {
    }

    protected void initializeStrings()
    {
    }

    protected void initializeTextures()
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
        loadCustomNonBaseStrings(JSON_AUGMENTS, PGR::loadAugmentStrings);
    }

    protected void loadCustomCardStrings()
    {
        loadCustomNonBaseStrings(JSON_CARDS, PGR::loadGroupedCardStrings);
    }

    protected void loadCustomCards()
    {
        super.loadCustomCards(prefix);
    }

    protected void loadCustomNonBaseStrings(String path, ActionT1<String> loadFunc)
    {
        String json = getFallbackFile(path).readString(StandardCharsets.UTF_8.name());
        loadFunc.invoke(json);

        if (testFolder.isDirectory() || isTranslationSupported(Settings.language))
        {
            FileHandle file = getFile(Settings.language, path);
            if (file.exists())
            {
                String json2 = file.readString(StandardCharsets.UTF_8.name());
                loadFunc.invoke(json);
            }
        }
    }

    protected void loadCustomPotions()
    {
        super.loadCustomPotions(prefix, playerClass);
    }

    protected void loadCustomPowers()
    {
        super.loadCustomPowers(prefix);
    }

    protected void loadCustomRelics()
    {
        super.loadCustomRelics(prefix, cardColor);
    }

    protected void loadCustomStrings(Class<?> type)
    {
        super.loadCustomStrings(type, getFallbackFile(type.getSimpleName() + ".json"));

        if (isBetaTranslation() || isTranslationSupported(Settings.language))
        {
            super.loadCustomStrings(type, getFile(Settings.language, type.getSimpleName() + ".json"));
        }
    }

    protected void loadKeywords()
    {
        super.loadKeywords(getFallbackFile(JSON_KEYWORDS));

        if (isBetaTranslation() || isTranslationSupported(Settings.language))
        {
            super.loadKeywords(getFile(Settings.language, JSON_KEYWORDS));
        }
    }

    protected void postInitialize()
    {
    }

    @Override
    public final void receiveAddAudio()
    {
        initializeAudio();
    }

    @Override
    public final void receiveEditCards()
    {
        initializeCards();
    }

    @Override
    public final void receiveEditCharacters()
    {
        initializeCharacter();
    }

    @Override
    public final void receiveEditKeywords()
    {
        initializeKeywords();
    }

    @Override
    public final void receiveEditRelics()
    {
        initializeRelics();
    }

    @Override
    public final void receiveEditStrings()
    {
        initializeStrings();
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
