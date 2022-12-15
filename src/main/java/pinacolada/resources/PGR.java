package pinacolada.resources;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.RelicType;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.patches.EUIKeyword;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.augments.AugmentStrings;
import pinacolada.commands.*;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.resources.pcl.PCLCoreResources;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PGR
{
    public static final String BASE_PREFIX = "pcl";
    public static final String PREFIX_CARDS = "pinacolada.cards.";
    public static final String PREFIX_POTIONS = "pinacolada.potions.";
    public static final String PREFIX_POWERS = "pinacolada.powers.";
    public static final String PREFIX_RELIC = "pinacolada.relics.";

    protected static final ArrayList<String> cardClassNames = getClassNamesFromJarFile(PREFIX_CARDS);
    protected static final ArrayList<String> potionClassNames = getClassNamesFromJarFile(PREFIX_POTIONS);
    protected static final ArrayList<String> powerClassNames = getClassNamesFromJarFile(PREFIX_POWERS);
    protected static final ArrayList<String> relicClassNames = getClassNamesFromJarFile(PREFIX_RELIC);

    protected static final HashMap<AbstractCard.CardColor, PCLResources> colorResourceMap = new HashMap<>();
    protected static final HashMap<AbstractPlayer.PlayerClass, PCLResources> playerResourceMap = new HashMap<>();

    public static PCLCoreResources core;
    public static boolean simpleModePreview;

    public static void registerResource(PCLResources resources)
    {
        if (core == null)
        {
            throw new RuntimeException("No core present");
        }
        colorResourceMap.put(resources.cardColor, resources);
        playerResourceMap.put(resources.playerClass, resources);
        initialize(resources);
    }

    public static boolean canInstantiate(Class<?> type)
    {
        return !Hidden.class.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers());
    }

    public static String createID(String prefix, String suffix)
    {
        return prefix + ":" + suffix;
    }

    public static AugmentStrings getAugmentStrings(String stringID)
    {
        return AugmentStrings.STRINGS.get(stringID);
    }

    public static String getBlightImage(String id)
    {
        return getPng(id, "blights");
    }

    public static String getBlightOutlineImage(String id)
    {
        return getPng(id, "blights/outline");
    }

    public static BlightStrings getBlightStrings(String blightID)
    {
        return getLanguagePack().getBlightString(blightID);
    }

    public static String getCardImage(String id)
    {
        return getPng(id, "cards");
    }

    public static CardStrings getCardStrings(String cardID)
    {
        return getLanguagePack().getCardStrings(cardID);
    }

    public static CharacterStrings getCharacterStrings(String characterID)
    {
        return getLanguagePack().getCharacterString(characterID);
    }

    public static CharacterStrings getCharacterStrings(AbstractCard.CardColor cardColor)
    {
        return null;
    }

    public static CharacterStrings getCharacterStrings(AbstractPlayer.PlayerClass cardColor)
    {
        return null;
    }

    public static ArrayList<String> getClassNamesFromJarFile(String prefix)
    {
        return GameUtilities.getClassNamesFromJarFile(PGR.class, prefix);
    }

    public static EventStrings getEventStrings(String eventID)
    {
        return getLanguagePack().getEventString(eventID);
    }

    public static LocalizedStrings getLanguagePack()
    {
        return CardCrawlGame.languagePack;
    }

    public static String getMonsterImage(String id)
    {
        return getPng(id, "monsters");
    }

    public static MonsterStrings getMonsterStrings(String monsterID)
    {
        return getLanguagePack().getMonsterStrings(monsterID);
    }

    public static OrbStrings getOrbStrings(String orbID)
    {
        return getLanguagePack().getOrbString(orbID);
    }

    public static PCLAbstractPlayerData getPlayerData(AbstractCard.CardColor playerClass)
    {
        return getResources(playerClass).data;
    }

    public static PCLAbstractPlayerData getPlayerData(AbstractPlayer.PlayerClass playerClass)
    {
        return getResources(playerClass).data;
    }

    public static Collection<PCLResources> getAllResources()
    {
        return colorResourceMap.values();
    }

    public static PCLResources getResources(AbstractCard.CardColor cardColor)
    {
        return colorResourceMap.getOrDefault(cardColor, core);
    }

    public static PCLResources getResources(AbstractPlayer.PlayerClass playerClass)
    {
        return playerResourceMap.getOrDefault(playerClass, core);
    }

    public static String getPng(String id, String subFolder)
    {
        String[] s = id.split(Pattern.quote(":"), 2);
        return "images/" + s[0] + "/" + subFolder + "/" + s[1].replace(":", "_") + ".png";
    }

    public static String getPowerImage(String id)
    {
        return getPng(id, "powers");
    }

    public static PowerStrings getPowerStrings(String powerID)
    {
        return getLanguagePack().getPowerStrings(powerID);
    }

    public static String getRelicImage(String id)
    {
        return getPng(id, "relics");
    }

    public static RelicStrings getRelicStrings(String relicID)
    {
        return getLanguagePack().getRelicStrings(relicID);
    }

    public static String getRewardImage(String id)
    {
        return getPng(id, "ui/rewards");
    }

    public static RunModStrings getRunModStrings(String stringID)
    {
        return getLanguagePack().getRunModString(stringID);
    }

    public static StanceStrings getStanceString(String stanceID)
    {
        return getLanguagePack().getStanceString(stanceID);
    }

    public static void initialize()
    {
        if (core != null)
        {
            throw new RuntimeException("Already Initialized");
        }

        core = new PCLCoreResources();
        initialize(core);
    }

    protected static void initialize(PCLResources resources)
    {
        resources.initializeInternal();
        resources.initializeColor();

        BaseMod.subscribe(resources);
    }

    public static boolean isLoaded()
    {
        return core != null && core.isLoaded && EUIUtils.all(getAllResources(), r -> r.isLoaded);
    }

    public static boolean isTranslationSupported(Settings.GameLanguage language)
    {
        //This should be set only for beta branches.  Do not merge this into master.
        return false;//language == Settings.GameLanguage.RUS; // language == Settings.GameLanguage.ZHS || language == Settings.GameLanguage.ZHT;
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

    public static void registerCommands()
    {
        ConsoleCommand.addCommand("augment", AugmentCommand.class);
        ConsoleCommand.addCommand("effekseer", EffekseerCommand.class);
        ConsoleCommand.addCommand("exportcsv", ExportCSVCommand.class);
        ConsoleCommand.addCommand("obtain", ObtainCommand.class);
        ConsoleCommand.addCommand("obtaincustom", ObtainCustomCommand.class);
        ConsoleCommand.addCommand("reloadcustom", ReloadCustomCommand.class);
    }

    protected void loadCustomCard(Class<?> type)
    {
        if (!canInstantiate(type))
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

    protected void loadCustomCards(String character)
    {
        final String prefix = PREFIX_CARDS + character;
        for (String s : cardClassNames)
        {
            if (s.startsWith(prefix))
            {
                try
                {
                    //PCLUtils.LogInfoIfDebug(null, "Adding: " + s);

                    loadCustomCard(Class.forName(s));
                }
                catch (ClassNotFoundException e)
                {
                    EUIUtils.logWarning(null, "Class not found : " + s);
                }
            }
        }
    }

    protected void loadCustomPotion(Class<?> type, AbstractPlayer.PlayerClass playerClass)
    {
        if (!canInstantiate(type))
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

    protected void loadCustomPotions(String character, AbstractPlayer.PlayerClass playerClass)
    {
        final String prefix = PREFIX_POTIONS + character;

        for (String s : potionClassNames)
        {
            if (s.startsWith(prefix))
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

    protected void loadCustomPowers(String character)
    {
        final String prefix = PREFIX_CARDS + character;

        for (String s : cardClassNames)
        {
            if (s.startsWith(prefix))
            {
                try
                {
                    Class<?> type = Class.forName(s);
                    if (canInstantiate(type))
                    {
                        BaseMod.addPower((Class<AbstractPower>) type, createID(character, type.getSimpleName()));
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
        if (!canInstantiate(type))
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

    protected void loadCustomRelics(String character, AbstractCard.CardColor color)
    {
        final String prefix = PREFIX_RELIC + character;

        for (String s : relicClassNames)
        {
            if (s.startsWith(prefix))
            {
                try
                {
                    //logger.info("Adding: " + s);

                    loadCustomRelic(Class.forName(s), color);
                }
                catch (ClassNotFoundException e)
                {
                    EUIUtils.logWarning(PGR.class, "Class not found : " + s);
                }
            }
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
}
