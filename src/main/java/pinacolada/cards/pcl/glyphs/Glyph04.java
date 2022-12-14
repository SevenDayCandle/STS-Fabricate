package pinacolada.cards.pcl.glyphs;

import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.PTrigger;
import pinacolada.skills.skills.base.conditions.PCond_IfHasAffinity;
import pinacolada.skills.skills.base.traits.PTrait_Tag;

public class Glyph04 extends Glyph
{
    public static final PCLCardData DATA = registerInternal(Glyph04.class);

    public Glyph04()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addGainPower(PTrigger.passive(new PCond_IfHasAffinity(randomAffinity()),
                new PTrait_Tag(PCLCardTag.Ethereal).setCustomUpgrade((s, f, u) -> {
                    if (u >= 60 && !s.tags.contains(PCLCardTag.Purge))
                    {
                        s.addTag(PCLCardTag.Purge);
                    }
                    else if (u >= 30 && !s.tags.contains(PCLCardTag.Exhaust))
                    {
                        s.addTag(PCLCardTag.Exhaust);
                    }
                })));
    }
}