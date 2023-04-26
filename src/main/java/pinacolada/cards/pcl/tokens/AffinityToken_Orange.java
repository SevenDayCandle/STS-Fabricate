package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.fields.AffinityTokenData;
import pinacolada.cards.base.fields.PCLAffinity;

public class AffinityToken_Orange extends AffinityToken {
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Orange.class, PCLAffinity.Orange);

    public AffinityToken_Orange() {
        super(DATA);
    }
}