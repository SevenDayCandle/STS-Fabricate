package pinacolada.cards.pcl.glyphs;

import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.traits.PTrait_Tag;
import pinacolada.skills.skills.special.conditions.PCond_IfHasAffinity;

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
                    if (u >= 60 && !s.fields.tags.contains(PCLCardTag.Purge))
                    {
                        s.fields.addTag(PCLCardTag.Purge);
                    }
                    else if (u >= 30 && !s.fields.tags.contains(PCLCardTag.Exhaust))
                    {
                        s.fields.addTag(PCLCardTag.Exhaust);
                    }
                })));
    }
}