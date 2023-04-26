package pinacolada.cards.pcl.tokens;

import pinacolada.cards.base.fields.AffinityTokenData;
import pinacolada.cards.base.fields.PCLAffinity;

public class AffinityToken_Purple extends AffinityToken {
    public static final AffinityTokenData DATA = registerAffinityToken(AffinityToken_Purple.class, PCLAffinity.Purple);

    public AffinityToken_Purple() {
        super(DATA);
    }
}