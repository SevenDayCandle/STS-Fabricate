package pinacolada.resources;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import basemod.devcommands.ConsoleCommand;
import basemod.helpers.RelicType;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleCard;
import pinacolada.annotations.VisiblePotion;
import pinacolada.annotations.VisiblePower;
import pinacolada.annotations.VisibleRelic;
import pinacolada.augments.AugmentStrings;
import pinacolada.commands.*;
import pinacolada.resources.pcl.PCLCoreResources;
import pinacolada.rewards.pcl.AugmentReward;
import pinacolada.skills.PSkill;
import pinacolada.utilities.GameUtilities;

import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Pattern;

// Copied and modified from STS-AnimatorMod
public class PGR
{
    public static final String BASE_PREFIX = "pcl";

    protected static final HashMap<AbstractCard.CardColor, PCLResources<?,?,?>> colorResourceMap = new HashMap<>();
    protected static final HashMap<AbstractPlayer.PlayerClass, PCLResources<?,?,?>> playerResourceMap = new HashMap<>();

    public static PCLCoreResources core;

    public static void registerResource(PCLResources<?,?,?> resources)
    {
        if (core == null)
        {
            throw new RuntimeException("No core present");
        }
        colorResourceMap.put(resources.cardColor, resources);
        playerResourceMap.put(resources.playerClass, resources);
        initialize(resources);
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
        PCLResources<?,?,?> resources = colorResourceMap.getOrDefault(cardColor, null);
        return resources != null ? resources.getCharacterStrings() : null;
    }

    public static CharacterStrings getCharacterStrings(AbstractPlayer.PlayerClass player)
    {
        PCLResources<?,?,?> resources = playerResourceMap.getOrDefault(player, null);
        return resources != null ? resources.getCharacterStrings() : null;
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

    public static Collection<PCLResources<?,?,?>> getAllResources()
    {
        return colorResourceMap.values();
    }

    public static PCLResources<?,?,?> getResources(AbstractCard.CardColor cardColor)
    {
        return colorResourceMap.getOrDefault(cardColor, core);
    }

    public static PCLResources<?,?,?> getResources(AbstractPlayer.PlayerClass playerClass)
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

    protected static void initialize(PCLResources<?,?,?> resources)
    {
        resources.initializeColor();

        BaseMod.subscribe(resources);
    }

    public static boolean isLoaded()
    {
        return core != null && core.isLoaded && EUIUtils.all(getAllResources(), r -> r.isLoaded);
    }

    public static void loadCustomCards()
    {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleCard.class))
        {
            try
            {
                AbstractCard card = (AbstractCard) ct.getConstructor().newInstance();
                if (UnlockTracker.isCardLocked(card.cardID))
                {
                    UnlockTracker.unlockCard(card.cardID);
                    card.isLocked = false;
                }

                BaseMod.addCard(card);
            }
            catch (Exception e)
            {
                EUIUtils.logError(PGR.class, "Failed to load potion " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void loadCustomPotions()
    {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisiblePotion.class))
        {
            try
            {
                AbstractPotion potion = (AbstractPotion) ct.getConstructor().newInstance();
                // TODO get color from potion and add it to the proper pool
                BaseMod.addPotion(potion.getClass(), potion.liquidColor, potion.hybridColor, potion.spotsColor, potion.ID);
            }
            catch (Exception e)
            {
                EUIUtils.logError(PGR.class, "Failed to load potion " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void loadCustomPowers()
    {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisiblePower.class))
        {
            try
            {
                String id = ReflectionHacks.getPrivateStatic(ct, "POWER_ID");
                BaseMod.addPower((Class<? extends AbstractPower>) ct, id != null ? id : PGR.core.createID(ct.getSimpleName()));
            }
            catch (Exception e)
            {
                EUIUtils.logError(PSkill.class, "Failed to load power " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void loadCustomRelics()
    {
        for (Class<?> ct : GameUtilities.getClassesWithAnnotation(VisibleRelic.class))
        {
            try
            {
                AbstractRelic relic = (AbstractRelic) ct.getConstructor().newInstance();
                // TODO get color from relic and add it to the proper pool
                BaseMod.addRelic(relic, RelicType.SHARED);
            }
            catch (Exception e)
            {
                EUIUtils.logError(PGR.class, "Failed to load relic " + ct.getName() + ": " + e.getLocalizedMessage());
            }
        }
    }

    public static void registerCommands()
    {
        ConsoleCommand.addCommand("augment", AugmentCommand.class);
        ConsoleCommand.addCommand("effekseer", EffekseerCommand.class);
        ConsoleCommand.addCommand("exportcsv", ExportCSVCommand.class);
        ConsoleCommand.addCommand("obtain", ObtainCommand.class);
        ConsoleCommand.addCommand("obtaincustom", ObtainCustomCommand.class);
        ConsoleCommand.addCommand("reloadcustom", ReloadCustomCommand.class);
        ConsoleCommand.addCommand("unlockall", UnlockAllCommand.class);
    }

    public static void registerRewards()
    {
        AugmentReward.Serializer augmentSerializer = new AugmentReward.Serializer();
        BaseMod.registerCustomReward(PCLEnum.Rewards.AUGMENT, augmentSerializer, augmentSerializer);
    }
}
