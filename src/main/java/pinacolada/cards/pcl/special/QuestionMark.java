package pinacolada.cards.pcl.special;

import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.interfaces.markers.Hidden;

// Placeholder used in the event that an invalid card is created
public class QuestionMark extends PCLCard implements Hidden
{
    public static final PCLCardData DATA = register(QuestionMark.class)
            .setSkill(-2, CardRarity.SPECIAL, PCLCardTarget.None)
            .setTags(PCLCardTag.Haste, PCLCardTag.Purge)
            .setColorless();

    public QuestionMark()
    {
        super(DATA);
    }
}