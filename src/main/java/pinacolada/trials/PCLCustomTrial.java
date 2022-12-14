package pinacolada.trials;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PCLCustomTrial extends CustomTrial
{
    protected static final String DAILY_MODS = "Daily Mods";
    protected static final String MOD_BINARY = "Binary";
    protected static final String MOD_ONE_HIT_WONDER = "One Hit Wonder";
    protected static final String MOD_PRAISE_SNECKO = "Praise Snecko";
    protected static final String MOD_INCEPTION = "Inception";
    protected static final String MOD_MY_TRUE_FORM = "My True Form";
    protected static final String MOD_STARTER_DECK = "Starter Deck";
    private final ArrayList<String> cardIds = new ArrayList();
    private final ArrayList<String> modIds = new ArrayList();
    private final ArrayList<String> relicIds = new ArrayList();
    private Integer maxHpOverride = null;
    private boolean finalActAvailable;
    private boolean isEndless;
    private boolean isKeepingStarterCards = true;
    private boolean isKeepingStarterRelic = true;
    public boolean allowCustomCards;


    public PCLCustomTrial()
    {
        super();
    }

    // TODO Add custom mods
    public void addMod(CustomMod mod)
    {
        // Handle non daily mods. Ignoring Blight Chests BECAUSE IT IS ALREADY A DAILY MOD WTF
        switch (mod.ID)
        {
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

    public void addMods(List<CustomMod> mods)
    {
        for (CustomMod mod : mods)
        {
            addMod(mod);
        }
    }

    public void addStarterCards(String... moreCardIds)
    {
        addStarterCards(Arrays.asList(moreCardIds));
    }

    public void addStarterRelics(String... relicIds)
    {
        addStarterRelics(Arrays.asList(relicIds));
    }

    public void setMaxHpOverride(int maxHp)
    {
        this.maxHpOverride = maxHp;
    }

    public void addStarterCards(List<String> moreCardIds)
    {
        this.cardIds.addAll(moreCardIds);
    }

    public void setStarterCards(List<String> starterCards)
    {
        this.cardIds.clear();
        this.cardIds.addAll(starterCards);
        this.isKeepingStarterCards = false;
    }

    public void addStarterRelics(List<String> moreRelics)
    {
        this.relicIds.addAll(moreRelics);
    }

    public void setStarterRelics(List<String> starterRelics)
    {
        this.relicIds.clear();
        this.relicIds.addAll(starterRelics);
        this.isKeepingStarterRelic = false;
    }

    // TODO Use custom stuff (i.e. glyphs)
    public AbstractPlayer setupPlayer(AbstractPlayer player)
    {
        if (this.maxHpOverride != null)
        {
            player.maxHealth = this.maxHpOverride;
            player.currentHealth = this.maxHpOverride;
        }

        return player;
    }

    @Override
    public boolean keepStarterRelic()
    {
        return this.isKeepingStarterRelic;
    }

    @Override
    public List<String> extraStartingRelicIDs()
    {
        return this.relicIds;
    }

    @Override
    public boolean keepsStarterCards()
    {
        return this.isKeepingStarterCards;
    }

    @Override
    public List<String> extraStartingCardIDs()
    {
        return this.cardIds;
    }

    @Override
    public ArrayList<String> dailyModIDs()
    {
        return this.modIds;
    }

    public void setStarterCards(String... moreCardIds)
    {
        setStarterCards(Arrays.asList(moreCardIds));
    }

    public void setStarterRelics(String... starterRelics)
    {
        setStarterRelics(Arrays.asList(starterRelics));
    }

}
