package pinacolada.dungeon;

import com.megacrit.cardcrawl.cards.blue.EchoForm;
import com.megacrit.cardcrawl.cards.green.WraithForm;
import com.megacrit.cardcrawl.cards.purple.DevaForm;
import com.megacrit.cardcrawl.cards.red.DemonForm;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.BustedCrown;
import com.megacrit.cardcrawl.relics.SneckoEye;
import com.megacrit.cardcrawl.relics.UnceasingTop;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.trials.CustomTrial;
import pinacolada.patches.creature.AbstractPlayerPatches;
import pinacolada.resources.loadout.FakeLoadout;

import java.util.*;

public class PCLCustomTrial extends CustomTrial {
    protected static final String DAILY_MODS = "Daily Mods";
    protected static final String MOD_BINARY = "Binary";
    protected static final String MOD_ONE_HIT_WONDER = "One Hit Wonder";
    protected static final String MOD_PRAISE_SNECKO = "Praise Snecko";
    protected static final String MOD_INCEPTION = "Inception";
    protected static final String MOD_MY_TRUE_FORM = "My True Form";
    protected static final String MOD_STARTER_DECK = "Starter Deck";
    public final ArrayList<String> cardIds = new ArrayList<>();
    public final ArrayList<String> modIds = new ArrayList<>();
    public final ArrayList<String> relicIds = new ArrayList<>();
    public final HashSet<String> bannedAugments;
    public final HashSet<String> bannedCards;
    public final HashSet<String> bannedRelics;
    protected Integer maxHpOverride = null;
    protected boolean isKeepingStarterCards = true;
    protected boolean isKeepingStarterRelic = true;
    public boolean allowAugments;
    public boolean allowCustomBlights;
    public boolean allowCustomCards;
    public boolean allowCustomPotions;
    public boolean allowCustomRelics;
    public int augmentChance;
    public FakeLoadout fakeLoadout;

    public PCLCustomTrial(HashSet<String> bannedCards, HashSet<String> bannedRelics, HashSet<String> bannedAugments) {
        super();
        this.bannedCards = bannedCards;
        this.bannedRelics = bannedRelics;
        this.bannedAugments = bannedAugments;
    }

    public void addMod(CustomMod mod) {
        // Handle non daily mods. Ignoring Blight Chests BECAUSE IT IS ALREADY A DAILY MOD WTF
        switch (mod.ID) {
            case DAILY_MODS:
                setRandomDailyMods();
                return;
            case MOD_ONE_HIT_WONDER:
                maxHpOverride = 1;
                return;
            case MOD_PRAISE_SNECKO:
                setStarterRelics(SneckoEye.ID);
                return;
            case MOD_INCEPTION:
                setStarterRelics(UnceasingTop.ID);
                return;
            case MOD_MY_TRUE_FORM:
                addStarterCards(DemonForm.ID, WraithForm.ID, EchoForm.ID, DevaForm.ID);
                return;
            case MOD_STARTER_DECK:
                addStarterRelics(BustedCrown.ID);
                modIds.add(MOD_BINARY);
                return;
        }
        modIds.add(mod.ID);
    }

    public void addMods(Collection<CustomMod> mods) {
        for (CustomMod mod : mods) {
            addMod(mod);
        }
    }

    public void addStarterCards(String... moreCardIds) {
        addStarterCards(Arrays.asList(moreCardIds));
    }

    public void addStarterCards(List<String> moreCardIds) {
        this.cardIds.addAll(moreCardIds);
    }

    public void addStarterRelics(String... relicIds) {
        addStarterRelics(Arrays.asList(relicIds));
    }

    public void addStarterRelics(List<String> moreRelics) {
        this.relicIds.addAll(moreRelics);
    }

    @Override
    public ArrayList<String> dailyModIDs() {
        return this.modIds;
    }

    @Override
    public List<String> extraStartingCardIDs() {
        return this.cardIds;
    }

    @Override
    public List<String> extraStartingRelicIDs() {
        return this.relicIds;
    }

    @Override
    public boolean keepStarterRelic() {
        return this.isKeepingStarterRelic;
    }

    @Override
    public boolean keepsStarterCards() {
        return this.isKeepingStarterCards;
    }

    public void setMaxHpOverride(int maxHp) {
        this.maxHpOverride = maxHp;
    }

    public void setStarterCards(List<String> starterCards) {
        this.cardIds.clear();
        this.cardIds.addAll(starterCards);
        this.isKeepingStarterCards = false;
    }

    public void setStarterCards(String... moreCardIds) {
        setStarterCards(Arrays.asList(moreCardIds));
    }

    public void setStarterRelics(List<String> starterRelics) {
        this.relicIds.clear();
        this.relicIds.addAll(starterRelics);
        this.isKeepingStarterRelic = false;
    }

    public void setStarterRelics(String... starterRelics) {
        setStarterRelics(Arrays.asList(starterRelics));
    }

    // TODO Use custom stuff (i.e. glyphs)
    public AbstractPlayer setupPlayer(AbstractPlayer player) {
        if (fakeLoadout != null) {

            player.currentHealth = player.startingMaxHP = player.maxHealth = fakeLoadout.getHP();
            player.masterMaxOrbs = fakeLoadout.getOrbSlots();
            player.energy.energyMaster = fakeLoadout.getEnergy();
            player.gameHandSize = player.masterHandSize = fakeLoadout.getDraw();
            player.displayGold = player.gold = fakeLoadout.getGold();

            AbstractPlayerPatches.AbstractPlayerFields.overrideCards.set(player, fakeLoadout.getStartingDeck());
            // Loadout relics are added in PCLDungeon
        }

        if (this.maxHpOverride != null) {
            player.maxHealth = this.maxHpOverride;
            player.currentHealth = this.maxHpOverride;
        }

        return player;
    }

}
