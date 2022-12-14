package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.AffinityTokenData;
import pinacolada.cards.base.PCLAffinity;

public class AffinityToken_Silver extends AffinityToken
{
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Silver.class, PCLAffinity.Silver);

    public AffinityToken_Silver()
    {
        super(DATA);
    }
}