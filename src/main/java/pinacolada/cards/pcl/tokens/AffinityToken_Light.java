package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.AffinityTokenData;
import pinacolada.cards.base.PCLAffinity;

public class AffinityToken_Light extends AffinityToken
{
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Light.class, PCLAffinity.Light);

    public AffinityToken_Light()
    {
        super(DATA);
    }
}