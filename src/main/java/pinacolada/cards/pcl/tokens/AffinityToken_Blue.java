package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.fields.AffinityTokenData;
import pinacolada.cards.base.fields.PCLAffinity;

public class AffinityToken_Blue extends AffinityToken {
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Blue.class, PCLAffinity.Blue);

    public AffinityToken_Blue() {
        super(DATA);
    }
}