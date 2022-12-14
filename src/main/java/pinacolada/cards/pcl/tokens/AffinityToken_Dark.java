package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.AffinityTokenData;
import pinacolada.cards.base.PCLAffinity;

public class AffinityToken_Dark extends AffinityToken
{
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Dark.class, PCLAffinity.Dark);

    public AffinityToken_Dark()
    {
        super(DATA);
    }
}