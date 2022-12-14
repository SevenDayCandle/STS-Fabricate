package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.AffinityTokenData;
import pinacolada.cards.base.PCLAffinity;

public class AffinityToken_Red extends AffinityToken
{
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Red.class, PCLAffinity.Red);

    public AffinityToken_Red()
    {
        super(DATA);
    }
}