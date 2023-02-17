package pinacolada.ui.customRun;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.custom.CustomMod;
import com.megacrit.cardcrawl.screens.custom.CustomModeScreen;
import extendedui.ui.AbstractMenuScreen;
import pinacolada.interfaces.markers.RunAttributesProvider;
import pinacolada.trials.PCLCustomTrial;

import java.util.ArrayList;
import java.util.List;

// TODO fix dropdown
public class PCLCustomRunScreen extends AbstractMenuScreen implements RunAttributesProvider
{
    protected final PCLCustomRunCanvas canvas;
    public boolean isAscensionMode;
    public int ascensionLevel;
    public List<CustomMod> activeMods = new ArrayList<>();
    public boolean isEndless;
    public boolean isFinalActAvailable;
    public boolean allowCustomCards;
    protected boolean initialized;
    protected CharacterOption currentOption;
    protected String currentSeed = "";

    public PCLCustomRunScreen()
    {
        canvas = new PCLCustomRunCanvas(this);
    }

    @Override
    public int ascensionLevel()
    {
        return ascensionLevel;
    }

    @Override
    public void disableConfirm(boolean value)
    {
        canvas.confirmButton.isDisabled = value;
    }

    public void confirm()
    {
        CardCrawlGame.chosenCharacter = currentOption != null ? currentOption.c.chosenClass : null;
        if (CardCrawlGame.chosenCharacter == null)
        {
            CardCrawlGame.chosenCharacter = AbstractPlayer.PlayerClass.IRONCLAD;
        }

        CardCrawlGame.mainMenuScreen.isFadingOut = true;
        CardCrawlGame.mainMenuScreen.fadeOutMusic();
        Settings.isTrial = true;
        Settings.isDailyRun = false;
        AbstractDungeon.isAscensionMode = this.ascensionLevel == 0;
        AbstractDungeon.ascensionLevel = this.ascensionLevel;

        if (this.currentSeed.isEmpty())
        {
            long sourceTime = System.nanoTime();
            Random rng = new Random(sourceTime);
            Settings.seed = SeedHelper.generateUnoffensiveSeed(rng);
        }

        AbstractDungeon.generateSeeds();
        PCLCustomTrial trial = new PCLCustomTrial();
        trial.addMods(activeMods);
        trial.allowCustomCards = allowCustomCards;
        Settings.isEndless = isEndless;
        CustomModeScreen.finalActAvailable = isFinalActAvailable;
        CardCrawlGame.trial = trial;
        AbstractPlayer.customMods = CardCrawlGame.trial.dailyModIDs();
    }

    public void initialize(CustomModeScreen screen)
    {
        if (!initialized)
        {
            canvas.setup(screen);
            initialized = true;
        }
        canvas.resetPositions();
    }

    public void open()
    {
        canvas.open();
        if (!currentSeed.isEmpty())
        {
            canvas.seedInput.setTextAndCommit(currentSeed);
        }
        else
        {
            Settings.seed = null;
            Settings.specialSeed = null;
        }
    }

    public void updateImpl()
    {
        super.updateImpl();
        canvas.updateImpl();
    }

    public void renderImpl(SpriteBatch sb)
    {
        canvas.renderImpl(sb);
    }

    public void setAscension(int i)
    {
        this.ascensionLevel = i;
        canvas.setAscension(i);
    }

    public void setCharacter(CharacterOption c)
    {
        this.currentOption = c;
        canvas.setCharacter(c);
    }

    public void setSeed(String s)
    {
        try
        {
            SeedHelper.setSeed(s);
        }
        catch (NumberFormatException var2)
        {
            Settings.seed = 9223372036854775807L;
        }
        this.currentSeed = SeedHelper.getUserFacingSeedString();
        canvas.seedInput.setLabel(this.currentSeed);
    }
}
