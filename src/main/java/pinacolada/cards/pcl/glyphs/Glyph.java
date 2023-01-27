package pinacolada.cards.pcl.glyphs;

import extendedui.EUIRM;
import extendedui.utilities.ColoredTexture;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public abstract class Glyph extends PCLCard
{
    public static final String ID = PGR.core.createID(Glyph.class.getSimpleName());
    private static final ArrayList<Glyph> cards = new ArrayList<>();

    protected Glyph(PCLCardData cardData)
    {
        super(cardData);

        this.cropPortrait = false;
        this.portraitForeground = portraitImg;
        this.portraitImg = new ColoredTexture(EUIRM.getTexture(PGR.getCardImage(ID), true));
        this.showTypeText = false;
    }

    public static Glyph getCard(int index, int upgradeLevel)
    {
        ArrayList<Glyph> glyphs = getCards();
        if (index >= 0 && index < glyphs.size())
        {
            Glyph result = glyphs.get(index);
            for (int i = 0; i < upgradeLevel; i++)
            {
                result.upgrade();
            }
            return result;
        }

        throw new IndexOutOfBoundsException("Glyph not found at index: " + index);
    }

    // TODO use different cards for different pinacolada.characters
    public static ArrayList<Glyph> getCards()
    {
        if (cards.isEmpty())
        {
            cards.add(new Glyph01());
            cards.add(new Glyph02());
            cards.add(new Glyph03());
            cards.add(new Glyph04());
            cards.add(new Glyph05());
            cards.add(new Glyph06());
            cards.add(new Glyph07());
            cards.add(new Glyph08());
            cards.add(new Glyph09());
        }

        return cards;
    }

    public static PCLAffinity randomAffinity()
    {
        return GameUtilities.getRandomElement(PCLAffinity.getAvailableAffinities(player.getCardColor()));
    }

    public static PCLCardData registerInternal(Class<? extends PCLCard> type)
    {
        return register(type)
                .setCurse(-2, PCLCardTarget.None, true)
                .setMaxUpgrades(-1);
    }

}