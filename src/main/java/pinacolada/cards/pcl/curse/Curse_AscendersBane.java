package pinacolada.cards.pcl.curse;

import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;

public class Curse_AscendersBane extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_AscendersBane.class)
            .setCurse(-2, PCLCardTarget.None, true)
            .setTags(PCLCardTag.Ethereal, PCLCardTag.Soulbound, PCLCardTag.Unplayable);

    public Curse_AscendersBane()
    {
        super(DATA);
    }
}