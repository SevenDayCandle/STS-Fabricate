package pinacolada.resources.pcl;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.pcl.special.QuestionMark;

import java.util.ArrayList;

// Copied and modified from STS-AnimatorMod
public class FakeLoadout extends PCLLoadout
{
    public FakeLoadout()
    {
        super(AbstractCard.CardColor.COLORLESS, -1, 0);
    }

    @Override
    public ArrayList<String> getStartingDeck()
    {
        return startingDeck;
    }

    @Override
    protected PCLCardData getDefend()
    {
        return pinacolada.cards.pcl.replacement.Miracle.DATA;
    }

    @Override
    protected PCLCardData getStrike()
    {
        return pinacolada.cards.pcl.replacement.Miracle.DATA;
    }

    @Override
    public PCLCardData getSymbolicCard()
    {
        return QuestionMark.DATA;
    }

    @Override
    public PCLTrophies getTrophies()
    {
        return null;
    }

    @Override
    public void onVictory(int ascensionLevel, int trophyLevel, int score)
    {
        //
    }
}
