package pinacolada.cards.pcl.special;

import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.cards.base.tags.PCLCardTag;

// Placeholder used in the event that an invalid card is created
public class QuestionMark extends PCLCard {
    public static final PCLCardData DATA = register(QuestionMark.class)
            .setSkill(-2, CardRarity.SPECIAL, PCLCardTarget.None)
            .setTags(PCLCardTag.Haste.make(-1), PCLCardTag.Ephemeral.make())
            .setMaxUpgrades(0)
            .setColorless();

    public QuestionMark() {
        super(DATA);
    }

    // Need to copy base properties to avoid LOOP DEE LOOP in da merchant
    public PCLCard makeCopy() {
        PCLCard copy = super.makeCopy();
        copy.cardID = this.cardID;
        copy.color = this.color;
        copy.type = this.type;
        copy.rarity = this.rarity;
        return copy;
    }

    public PCLCard makeStatEquivalentCopy() {
        PCLCard copy = super.makeStatEquivalentCopy();
        copy.cardID = this.cardID;
        copy.color = this.color;
        copy.type = this.type;
        copy.rarity = this.rarity;
        return copy;
    }
}