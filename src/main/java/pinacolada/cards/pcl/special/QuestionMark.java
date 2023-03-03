package pinacolada.cards.pcl.special;

import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

// Placeholder used in the event that an invalid card is created
public class QuestionMark extends PCLCard
{
    public static final PCLCardData DATA = register(QuestionMark.class)
            .setSkill(-2, CardRarity.SPECIAL, PCLCardTarget.None)
            .setTags(PCLCardTag.Haste.make(-1), PCLCardTag.Ephemeral.make())
            .setMaxUpgrades(0)
            .setColorless();

    public QuestionMark()
    {
        super(DATA);
    }
}