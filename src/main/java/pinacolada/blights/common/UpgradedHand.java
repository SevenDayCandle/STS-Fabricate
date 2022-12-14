package pinacolada.blights.common;

import basemod.BaseMod;
import pinacolada.blights.PCLBlight;

public class UpgradedHand extends PCLBlight
{
    public static final String ID = createFullID(UpgradedHand.class);

    public UpgradedHand()
    {
        this(1);
    }

    public UpgradedHand(int turns)
    {
        super(ID, turns);
    }

    public void addAmount(int amount)
    {
        this.counter += amount;
        BaseMod.MAX_HAND_SIZE += amount;
    }

    @Override
    public void onEquip()
    {
        BaseMod.MAX_HAND_SIZE += this.counter;
    }

    public void reset()
    {
        BaseMod.MAX_HAND_SIZE -= this.counter;
        this.counter = 0;
    }
}