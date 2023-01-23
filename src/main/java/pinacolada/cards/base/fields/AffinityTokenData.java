package pinacolada.cards.base.fields;

import com.megacrit.cardcrawl.cards.AbstractCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.resources.PGR;

import java.util.HashMap;

public class AffinityTokenData extends PCLCardData
{

    public final PCLAffinity affinity;
    protected HashMap<AbstractCard.CardColor, String> pathMap = new HashMap<>();

    public AffinityTokenData(Class<? extends PCLCard> type, PCLAffinity affinity)
    {
        super(type, PGR.core);
        this.affinity = affinity;
        setAffinities(affinity);
    }

    public String getImagePath(AbstractCard.CardColor color)
    {
        String path = pathMap.get(color);
        if (path == null)
        {
            pathMap.put(color, PGR.getCardImage(PGR.getResources(color).createID(type.getSimpleName())));
        }
        return pathMap.getOrDefault(color, imagePath);
    }
}
