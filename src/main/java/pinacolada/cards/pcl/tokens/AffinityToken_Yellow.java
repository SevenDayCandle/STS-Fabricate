package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.AffinityTokenData;
import pinacolada.cards.base.PCLAffinity;

public class AffinityToken_Yellow extends AffinityToken
{
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Yellow.class, PCLAffinity.Yellow);

    public AffinityToken_Yellow()
    {
        super(DATA);
    }
}