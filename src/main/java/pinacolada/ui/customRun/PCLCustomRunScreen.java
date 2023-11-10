package pinacolada.ui.customRun;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.screens.CustomCardLibraryScreen;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.dungeon.PCLDungeon;
import pinacolada.interfaces.providers.RunAttributesProvider;
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.resources.loadout.FakeLoadout;
import pinacolada.trials.PCLCustomTrial;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PCLCustomRunScreen extends AbstractMenuScreen implements RunAttributesProvider {
    public static final HashMap<String, AbstractCard.CardColor> COLOR_MOD_MAPPING = new HashMap<>();
    protected final PCLCustomRunCanvas canvas;
    protected CharacterOption currentOption;
    protected String currentSeed = EUIUtils.EMPTY_STRING;
    protected boolean initialized;
    public HashSet<String> bannedAugments = new HashSet<>();
    public HashSet<String> bannedCards = new HashSet<>();
    public HashSet<String> bannedRelics = new HashSet<>();
    public List<CustomMod> activeMods = new ArrayList<>();
    public boolean allowAugments;
    public boolean allowCustomBlights = true;
    public boolean allowCustomCards = true;
    public boolean allowCustomPotions = true;
    public boolean allowCustomRelics = true;
    public boolean allowLoadout;
    public boolean isAscensionMode;
    public boolean isEndless;
    public boolean isFinalActAvailable;
    public int ascensionLevel;
    public int augmentChance = 100;
    public FakeLoadout fakeLoadout = new FakeLoadout();

    public PCLCustomRunScreen() {
        canvas = new PCLCustomRunCanvas(this);
    }

    private void addCardsForGroup(ArrayList<AbstractCard> group, AbstractCard.CardColor color) {
        addCardsFromGroup(group, color, c -> isCardEligible(c.rarity));
        if (allowCustomCards) {
            for (PCLCustomCardSlot slot : PCLCustomCardSlot.getCards(color)) {
                if (AbstractCard.CardRarity.valueOf(slot.rarity) != AbstractCard.CardRarity.SPECIAL) {
                    group.add(slot.make());
                }
            }
        }
    }

    private void addCardsFromGroup(ArrayList<AbstractCard> group, AbstractCard.CardColor color, FuncT1<Boolean, AbstractCard> evalFunc) {
        for (AbstractCard c : CustomCardLibraryScreen.getCards(color)) {
            if (evalFunc.invoke(c)) {
                group.add(c.makeSameInstanceOf());
            }
        }
    }

    private void addColorlessCardsForGroup(ArrayList<AbstractCard> group, AbstractCard.CardColor color) {
        PCLResources<?, ?, ?, ?> resources = PGR.getResources(color);
        addCardsFromGroup(group, AbstractCard.CardColor.COLORLESS, c -> resources.containsColorless(c) && isCardEligible(c.rarity));
        addCardsFromGroup(group, AbstractCard.CardColor.CURSE, c -> resources.containsColorless(c) && isCardEligible(c.rarity));

        if (allowCustomCards) {
            for (PCLCustomCardSlot slot : PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS)) {
                if (isCardEligible(AbstractCard.CardRarity.valueOf(slot.rarity))) {
                    group.add(slot.make());
                }
            }
        }
    }

    private void addRelicsForGroup(ArrayList<AbstractRelic> group, AbstractCard.CardColor color) {
        for (AbstractRelic c : GameUtilities.getRelics(color).values()) {
            if (isRelicEligible(c.tier) && !UnlockTracker.isRelicLocked(c.relicId)) {
                AbstractRelic relic = c.makeCopy();
                relic.isSeen = relic.isSeen || UnlockTracker.isRelicSeen(relic.relicId);
                group.add(relic);
            }
        }
        if (allowCustomRelics) {
            for (PCLCustomRelicSlot slot : PCLCustomRelicSlot.getRelics(color)) {
                if (isRelicEligible(AbstractRelic.RelicTier.valueOf(slot.tier))) {
                    AbstractRelic relic = slot.make();
                    relic.isSeen = relic.isSeen || UnlockTracker.isRelicSeen(relic.relicId);
                    group.add(relic);
                }
            }
        }
    }

    @Override
    public int ascensionLevel() {
        return ascensionLevel;
    }

    public void confirm() {
        CardCrawlGame.chosenCharacter = currentOption != null ? currentOption.c.chosenClass : null;
        if (CardCrawlGame.chosenCharacter == null) {
            CardCrawlGame.chosenCharacter = AbstractPlayer.PlayerClass.IRONCLAD;
        }

        CardCrawlGame.mainMenuScreen.isFadingOut = true;
        CardCrawlGame.mainMenuScreen.fadeOutMusic();
        Settings.isTrial = true;
        Settings.isDailyRun = false;
        AbstractDungeon.isAscensionMode = this.ascensionLevel >= 0;
        AbstractDungeon.ascensionLevel = this.ascensionLevel;

        if (this.currentSeed.isEmpty()) {
            long sourceTime = System.nanoTime();
            Random rng = new Random(sourceTime);
            Settings.seed = SeedHelper.generateUnoffensiveSeed(rng);
        }
        AbstractDungeon.generateSeeds();

        PCLCustomTrial trial = new PCLCustomTrial(new HashSet<>(bannedCards), new HashSet<>(bannedRelics), new HashSet<>(bannedAugments));
        if (allowLoadout) {
            trial.fakeLoadout = fakeLoadout;
        }

        trial.addMods(activeMods);
        trial.allowAugments = allowAugments;
        trial.allowCustomBlights = allowCustomBlights;
        trial.allowCustomCards = allowCustomCards;
        trial.allowCustomPotions = allowCustomPotions;
        trial.allowCustomRelics = allowCustomRelics;
        trial.augmentChance = augmentChance;
        Settings.isEndless = isEndless;
        CustomModeScreen.finalActAvailable = isFinalActAvailable;
        CardCrawlGame.trial = trial;
        AbstractPlayer.customMods = CardCrawlGame.trial.dailyModIDs();
        canvas.confirmButton.hide();
    }

    @Override
    public void disableConfirm(boolean value) {
        canvas.confirmButton.isDisabled = value;
    }

    public ArrayList<AbstractCard> getAllPossibleCards() {
        ArrayList<AbstractCard> group = new ArrayList<>();
        AbstractCard.CardColor color = currentOption.c.getCardColor();
        HashMap<String, AbstractPlayer> moddedChars = new HashMap<>();

        // Always include the player's color and the colorless colors
        // Using CustomCardLibraryScreen card lists because those are already grouped by color
        addCardsForGroup(group, color);
        addColorlessCardsForGroup(group, color);

        for (CustomMod mod : activeMods) {
            // Diverse means that we put all possible cards into the pool
            if (Diverse.ID.equals(mod.ID)) {
                group.clear();
                for (ArrayList<AbstractCard> cGroup : CustomCardLibraryScreen.getAllCardLists()) {
                    for (AbstractCard c : cGroup) {
                        group.add(c.makeCopy());
                    }
                }
                break;
            }
            else {
                AbstractCard.CardColor foundColor = COLOR_MOD_MAPPING.get(mod.ID);
                if (foundColor != null && color != foundColor) {
                    addCardsForGroup(group, foundColor);
                }
            }
        }
        return group;
    }

    public ArrayList<AbstractRelic> getAllPossibleRelics() {
        ArrayList<AbstractRelic> group = new ArrayList<>();
        AbstractCard.CardColor color = currentOption.c.getCardColor();

        // Always include the player's color and the colorless colors
        addRelicsForGroup(group, AbstractCard.CardColor.COLORLESS);
        addRelicsForGroup(group, color);

        return group;
    }

    public void initialize(CustomModeScreen screen) {
        if (!initialized) {
            canvas.setup(screen);
            // Custom color mappings need to be handled in BaseModPatches_PublishAddCustomModeMods
            COLOR_MOD_MAPPING.put(RedCards.ID, AbstractCard.CardColor.RED);
            COLOR_MOD_MAPPING.put(GreenCards.ID, AbstractCard.CardColor.GREEN);
            COLOR_MOD_MAPPING.put(BlueCards.ID, AbstractCard.CardColor.BLUE);
            COLOR_MOD_MAPPING.put(PurpleCards.ID, AbstractCard.CardColor.PURPLE);

            // Start off with Ironclad to avoid crashing at startup
            setCharacter(canvas.characters.get(0).character);
            initialized = true;
        }
        canvas.resetPositions();
    }

    private boolean isCardEligible(AbstractCard.CardRarity rarity) {
        switch (rarity) {
            case COMMON:
            case UNCOMMON:
            case RARE:
            case CURSE:
                return true;
        }
        return false;
    }

    private boolean isRelicEligible(AbstractRelic.RelicTier tier) {
        switch (tier) {
            case COMMON:
            case UNCOMMON:
            case RARE:
            case BOSS:
            case SHOP:
                return true;
        }
        return false;
    }

    public void open() {
        canvas.open();
        if (!currentSeed.isEmpty()) {
            canvas.seedInput.setTextAndCommit(currentSeed);
        }
        else {
            Settings.seed = null;
            Settings.specialSeed = null;
        }
    }

    public void renderImpl(SpriteBatch sb) {
        canvas.renderImpl(sb);
    }

    public void setCharacter(CharacterOption c) {
        this.currentOption = c;
        canvas.setCharacter(c);
        fakeLoadout.onSelect(currentOption); // Refresh character option
    }

    public void setSeed(String s) {
        try {
            SeedHelper.setSeed(s);
        }
        catch (NumberFormatException var2) {
            Settings.seed = 9223372036854775807L;
        }
        this.currentSeed = SeedHelper.getUserFacingSeedString();
        canvas.seedInput.setLabel(this.currentSeed);
    }

    public void updateImpl() {
        super.updateImpl();
        canvas.updateImpl();
    }
}
