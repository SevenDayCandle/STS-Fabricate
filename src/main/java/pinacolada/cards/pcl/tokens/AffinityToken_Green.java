package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.AffinityTokenData;
import pinacolada.cards.base.PCLAffinity;

public class AffinityToken_Green extends AffinityToken
{
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Green.class, PCLAffinity.Green);

    public AffinityToken_Green()
    {
        super(DATA);
    }
}