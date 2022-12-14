package pinacolada.cards.pcl.tokens;

import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.random.Random;
import extendedui.EUIRM;
import extendedui.utilities.ColoredTexture;
import pinacolada.actions.pileSelection.SelectFromPile;
import pinacolada.cards.base.*;
import pinacolada.interfaces.markers.Hidden;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.ArrayList;

public abstract class AffinityToken extends PCLCard implements Hidden
{
    public static final String ID = PGR.core.createID(AffinityToken.class.getSimpleName());

    protected static final ArrayList<PCLCardData> cards = new ArrayList<>();

    protected AffinityToken(AffinityTokenData cardData)
    {
        super(cardData);

        this.cropPortrait = false;
        this.portraitForeground = portraitImg;
        this.portraitImg = new ColoredTexture(EUIRM.getTexture(PGR.getCardImage(ID), true), cardData.affinity.getAlternateColor(0.55f));
    }

    public static String backgroundPath()
    {
        return PGR.getCardImage(ID);
    }

    public static CardGroup createTokenGroup(int amount, Random rng, boolean upgrade)
    {
        final CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        final RandomizedList<PCLCardData> temp = new RandomizedList<>(getCards());
        while (amount > 0 && temp.size() > 0)
        {
            group.group.add(temp.retrieve(rng, true).makeCopy(upgrade));
            amount -= 1;
        }

        return group;
    }

    public static PCLAffinity getAffinityFromCardID(String ID)
    {
        if (AffinityToken_Red.DATA.ID.equals(ID))
        {
            return PCLAffinity.Red;
        }
        else if (AffinityToken_Green.DATA.ID.equals(ID))
        {
            return PCLAffinity.Green;
        }
        else if (AffinityToken_Blue.DATA.ID.equals(ID))
        {
            return PCLAffinity.Blue;
        }
        else if (AffinityToken_Orange.DATA.ID.equals(ID))
        {
            return PCLAffinity.Orange;
        }
        else if (AffinityToken_Light.DATA.ID.equals(ID))
        {
            return PCLAffinity.Light;
        }
        else if (AffinityToken_Dark.DATA.ID.equals(ID))
        {
            return PCLAffinity.Dark;
        }
        else if (AffinityToken_Silver.DATA.ID.equals(ID))
        {
            return PCLAffinity.Silver;
        }
        return PCLAffinity.Star;
    }

    public static AffinityToken getCard(PCLAffinity affinity)
    {
        return (AffinityToken) getCardData(affinity).createNewInstance();
    }

    public static AffinityTokenData getCardData(PCLAffinity affinity)
    {
        switch (affinity)
        {
            case Red:
                return AffinityToken_Red.DATA;
            case Green:
                return AffinityToken_Green.DATA;
            case Blue:
                return AffinityToken_Blue.DATA;
            case Orange:
                return AffinityToken_Orange.DATA;
            case Light:
                return AffinityToken_Light.DATA;
            case Dark:
                return AffinityToken_Dark.DATA;
            case Silver:
                return AffinityToken_Silver.DATA;
            case Star:
                return AffinityToken_Star.DATA;

            default:
            {
                throw new RuntimeException("Affinity token not supported for " + affinity);
            }
        }
    }

    public static ArrayList<PCLCardData> getCards()
    {
        if (cards.isEmpty())
        {
            for (PCLAffinity affinity : PCLAffinity.extended())
            {
                cards.add(getCardData(affinity));
            }
        }

        return cards;
    }

    public static AffinityToken getCopy(PCLAffinity affinity, boolean upgraded)
    {
        return (AffinityToken) getCardData(affinity).makeCopy(upgraded);
    }

    protected static AffinityTokenData registerAffinityToken(Class<? extends PCLCard> type, PCLAffinity affinity)
    {
        return (AffinityTokenData) PCLCard.registerCardData(new AffinityTokenData(type, affinity)).setSkill(0, CardRarity.SPECIAL, PCLCardTarget.None).setColorless();
    }

    public static SelectFromPile selectTokenAction(String name, int amount)
    {
        return selectTokenAction(name, amount, cards.size());
    }

    public static SelectFromPile selectTokenAction(String name, int amount, int size)
    {
        return selectTokenAction(name, amount, size, false);
    }

    public static SelectFromPile selectTokenAction(String name, int amount, int size, boolean upgrade)
    {
        return new SelectFromPile(name, amount, createTokenGroup(size, GameUtilities.getRNG(), upgrade));
    }

    public PCLAffinity getAffinity()
    {
        return ((AffinityTokenData) cardData).affinity;
    }
}