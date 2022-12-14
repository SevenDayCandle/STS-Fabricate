package pinacolada.characters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Madness;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import com.megacrit.cardcrawl.screens.stats.CharStat;

import java.util.ArrayList;

// Used for AbstractPower icons and rendering abstractbox forms
public class FakeCharacter extends PCLCharacter
{
    public static FakeCharacter instance = new FakeCharacter();

    public FakeCharacter()
    {
        super("LEAVE ME ALONE", PlayerClass.IRONCLAD);
    }

    @Override
    public String getPortraitImageName()
    {
        return name;
    }

    @Override
    public ArrayList<String> getStartingDeck()
    {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> getStartingRelics()
    {
        return new ArrayList<>();
    }

    @Override
    public CharSelectInfo getLoadout()
    {
        return new CharSelectInfo(name, name, 0, 0, 0, 0, 0, this, new ArrayList<>(), new ArrayList<>(), false);
    }

    @Override
    public String getTitle(PlayerClass playerClass)
    {
        return "LEAVE ME ALONE";
    }

    @Override
    public AbstractCard.CardColor getCardColor()
    {
        return AbstractCard.CardColor.COLORLESS;
    }

    @Override
    public Color getCardRenderColor()
    {
        return Color.BROWN.cpy();
    }

    @Override
    public String getAchievementKey()
    {
        return "...";
    }

    @Override
    public ArrayList<AbstractCard> getCardPool(ArrayList<AbstractCard> arrayList)
    {
        return arrayList;
    }

    @Override
    public AbstractCard getStartCardForEvent()
    {
        return new Madness();
    }

    @Override
    public Color getCardTrailColor()
    {
        return Color.BROWN.cpy();
    }

    @Override
    public String getLeaderboardCharacterName()
    {
        return "LEAVE ME ALONE";
    }

    @Override
    public Texture getEnergyImage()
    {
        return getCustomModeCharacterButtonImage();
    }

    @Override
    public int getAscensionMaxHPLoss()
    {
        return 9000;
    }

    @Override
    public BitmapFont getEnergyNumFont()
    {
        return FontHelper.dungeonTitleFont;
    }

    @Override
    public void renderOrb(SpriteBatch spriteBatch, boolean b, float v, float v1)
    {

    }

    @Override
    public void updateOrb(int i)
    {

    }

    @Override
    public Prefs getPrefs()
    {
        return new Prefs();
    }

    @Override
    public void loadPrefs()
    {

    }

    @Override
    public CharStat getCharStat()
    {
        return new CharStat(this);
    }

    @Override
    public int getUnlockedCardCount()
    {
        return 0;
    }

    @Override
    public int getSeenCardCount()
    {
        return 0;
    }

    @Override
    public int getCardCount()
    {
        return 0;
    }

    @Override
    public boolean saveFileExists()
    {
        return false;
    }

    @Override
    public String getWinStreakKey()
    {
        return "...";
    }

    @Override
    public String getLeaderboardWinStreakKey()
    {
        return "...";
    }

    @Override
    public void renderStatScreen(SpriteBatch spriteBatch, float v, float v1)
    {

    }

    @Override
    public void doCharSelectScreenSelectEffect()
    {

    }

    @Override
    public String getCustomModeCharacterButtonSoundKey()
    {
        return "TINGSHA";
    }

    @Override
    public Texture getCustomModeCharacterButtonImage()
    {
        return new Texture(0, 0, Pixmap.Format.Alpha);
    }

    @Override
    public CharacterStrings getCharacterString()
    {
        return new CharacterStrings();
    }

    @Override
    public String getLocalizedCharacterName()
    {
        return name;
    }

    @Override
    public void refreshCharStat()
    {

    }

    @Override
    public AbstractPlayer newInstance()
    {
        return new FakeCharacter();
    }

    @Override
    public TextureAtlas.AtlasRegion getOrb()
    {
        return new TextureAtlas.AtlasRegion(getCustomModeCharacterButtonImage(), 0, 0, 0, 0);
    }

    @Override
    public String getSpireHeartText()
    {
        return "LEAVE ME ALONE";
    }

    @Override
    public Color getSlashAttackColor()
    {
        return Color.BROWN.cpy();
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect()
    {
        return new AbstractGameAction.AttackEffect[0];
    }

    @Override
    public String getVampireText()
    {
        return "LEAVE ME ALONE";
    }
}
