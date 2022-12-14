package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.AffinityTokenData;
import pinacolada.cards.base.PCLAffinity;

public class AffinityToken_Star extends AffinityToken
{
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Star.class, PCLAffinity.Star);

    public AffinityToken_Star()
    {
        super(DATA);
    }
}