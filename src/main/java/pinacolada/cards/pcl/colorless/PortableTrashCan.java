package pinacolada.cards.pcl.colorless;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleCard;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameUtilities;

@VisibleCard
public class PortableTrashCan extends PCLCard
{
    public static final PCLCardData DATA = register(PortableTrashCan.class)
            .setSkill(0, CardRarity.RARE, PCLCardTarget.None)
            .setTags(PCLCardTag.Fleeting.make(), PCLCardTag.Retain.make(0, -1))
            .setColorless();

    public PortableTrashCan()
    {
        super(DATA);
    }

    @Override
    public void setup(Object input)
    {
        addSpecialMove(0, this::action, 1);
    }

    public void action(PSpecialSkill move, PCLUseInfo info)
    {
        PCLActions.bottom.purgeFromPile(name, move.amount, player.hand)
                .setFilter(GameUtilities::canRemoveFromDeck)
                .addCallback(cards -> {
                    for (AbstractCard card : cards)
                    {
                        for (AbstractCard copy : GameUtilities.getAllInBattleInstances(card.uuid))
                        {
                            PCLActions.bottom.purge(copy);
                        }
                        AbstractCard masterCopy = GameUtilities.getMasterDeckInstance(card.uuid);
                        if (masterCopy != null)
                        {
                            AbstractDungeon.player.masterDeck.removeCard(masterCopy);
                        }
                    }
                });
    }
}