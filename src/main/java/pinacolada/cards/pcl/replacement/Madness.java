package pinacolada.cards.pcl.replacement;

import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.skills.skills.PSpecialSkill;
import pinacolada.utilities.GameActions;

public class Madness extends PCLCard
{
    public static final PCLCardData DATA = register(Madness.class)
            .setSkill(0, CardRarity.SPECIAL, PCLCardTarget.None)
            .setAffinities(PCLAffinity.Star)
            .setTags(PCLCardTag.Haste.make(0, -1))
            .setColorless();

    public Madness()
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
        GameActions.bottom.modifyCost(player.hand, move.amount, -1, false, false)
                .addCallback(c -> {
                    if (c.size() > 0)
                    {
                        GameActions.bottom.modifyCost(player.drawPile, move.amount, 1, false, false);
                    }
                });
    }
}