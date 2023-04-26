package pinacolada.ui.customRun;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.daily.mods.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import extendedui.interfaces.delegates.FuncT1;
import extendedui.ui.AbstractMenuScreen;
import extendedui.ui.cardFilter.CustomCardLibraryScreen;
import pinacolada.cards.base.PCLCustomCardSlot;
import pinacolada.interfaces.providers.RunAttributesProvider;
import pinacolada.resources.PCLResources;
import pinacolada.resources.PGR;
import pinacolada.trials.PCLCustomTrial;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PCLCustomRunScreen extends AbstractMenuScreen implements RunAttributesProvider {
    public static final HashMap<String, AbstractCard.CardColor> COLOR_MOD_MAPPING = new HashMap<>();
    protected final PCLCustomRunCanvas canvas;
    public HashSet<String> bannedCards = new HashSet<>();
    public HashSet<String> bannedRelics = new HashSet<>();
    public List<CustomMod> activeMods = new ArrayList<>();
    public boolean allowCustomCards;
    public boolean isAscensionMode;
    public boolean isEndless;
    public boolean isFinalActAvailable;
    public int ascensionLevel;
    protected CharacterOption currentOption;
    protected String currentSeed = GameUtilities.EMPTY_STRING;
    protected boolean initialized;

    public PCLCustomRunScreen() {
        canvas = new PCLCustomRunCanvas(this);
    }

    @Override
    public int ascensionLevel() {
        return ascensionLevel;
    }

    @Override
    public void disableConfirm(boolean value) {
        canvas.confirmButton.isDisabled = value;
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
        AbstractDungeon.isAscensionMode = this.ascensionLevel == 0;
        AbstractDungeon.ascensionLevel = this.ascensionLevel;

        if (this.currentSeed.isEmpty()) {
            long sourceTime = System.nanoTime();
            Random rng = new Random(sourceTime);
            Settings.seed = SeedHelper.generateUnoffensiveSeed(rng);
        }

        AbstractDungeon.generateSeeds();
        PCLCustomTrial trial = new PCLCustomTrial(new HashSet<>(bannedCards), new HashSet<>());
        trial.addMods(activeMods);
        trial.allowCustomCards = allowCustomCards;
        Settings.isEndless = isEndless;
        CustomModeScreen.finalActAvailable = isFinalActAvailable;
        CardCrawlGame.trial = trial;
        AbstractPlayer.customMods = CardCrawlGame.trial.dailyModIDs();
        canvas.confirmButton.hide();
    }

    public CardGroup getAllPossibleCards() {
        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        AbstractCard.CardColor color = currentOption.c.getCardColor();
        HashMap<String, AbstractPlayer> moddedChars = new HashMap<>();

        // Always include the player's color and the colorless colors
        // Using CustomCardLibraryScreen card lists because those are already grouped by color
        addCardsForGroup(group, color);
        addColorlessCardsForGroup(group, color);

        for (CustomMod mod : activeMods) {
            // Diverse means that we put all possible cards into the pool
            if (Diverse.ID.equals(mod.ID)) {
                group.group.clear();
                for (CardGroup cGroup : CustomCardLibraryScreen.CardLists.values()) {
                    for (AbstractCard c : cGroup.group) {
                        group.group.add(c.makeCopy());
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

    private void addCardsForGroup(CardGroup group, AbstractCard.CardColor color) {
        addCardsFromGroup(group, color, c -> c.rarity != AbstractCard.CardRarity.SPECIAL);
        if (allowCustomCards) {
            for (PCLCustomCardSlot slot : PCLCustomCardSlot.getCards(color)) {
                if (AbstractCard.CardRarity.valueOf(slot.rarity) != AbstractCard.CardRarity.SPECIAL) {
                    group.group.add(slot.getBuilder(0).createImplWithForms(false));
                }
            }
        }
    }

    private void addColorlessCardsForGroup(CardGroup group, AbstractCard.CardColor color) {
        PCLResources<?, ?, ?, ?> resources = PGR.getResources(color);
        addCardsFromGroup(group, AbstractCard.CardColor.COLORLESS, c -> resources.containsColorless(c) && c.rarity != AbstractCard.CardRarity.SPECIAL);
        addCardsFromGroup(group, AbstractCard.CardColor.CURSE, c -> resources.containsColorless(c) && c.rarity != AbstractCard.CardRarity.SPECIAL);

        if (allowCustomCards) {
            for (PCLCustomCardSlot slot : PCLCustomCardSlot.getCards(AbstractCard.CardColor.COLORLESS)) {
                if (AbstractCard.CardRarity.valueOf(slot.rarity) != AbstractCard.CardRarity.SPECIAL) {
                    group.group.add(slot.getBuilder(0).createImplWithForms(false));
                }
            }
        }
    }

    private void addCardsFromGroup(CardGroup group, AbstractCard.CardColor color, FuncT1<Boolean, AbstractCard> evalFunc) {
        for (AbstractCard c : CustomCardLibraryScreen.CardLists.get(color).group) {
            if (evalFunc.invoke(c)) {
                group.group.add(c.makeSameInstanceOf());
            }
        }
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

    public void setCharacter(CharacterOption c) {
        this.currentOption = c;
        canvas.setCharacter(c);
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

    public void updateImpl() {
        super.updateImpl();
        canvas.updateImpl();
    }

    public void setAscension(int i) {
        this.ascensionLevel = i;
        canvas.setAscension(i);
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
}
