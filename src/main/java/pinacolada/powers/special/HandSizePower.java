package pinacolada.powers.special;

import basemod.BaseMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.FontHelper;
import pinacolada.powers.PCLPower;

public class HandSizePower extends PCLPower
{
    public static final String POWER_ID = createFullID(HandSizePower.class);

    public HandSizePower(AbstractCreature owner, int amount)
    {
        super(owner, POWER_ID);
        this.initialize(amount, amount > 0 ? PowerType.BUFF : PowerType.DEBUFF, false);
    }

    public String getUpdatedDescription()
    {
        return this.formatDescription(0, BaseMod.MAX_HAND_SIZE);
    }

    public void renderAmount(SpriteBatch sb, float x, float y, Color c)
    {
        FontHelper.renderFontRightTopAligned(sb, FontHelper.powerAmountFont, Integer.toString(BaseMod.MAX_HAND_SIZE), x, y, this.fontScale, c);
    }

    protected void onAmountChanged(int previousAmount, int difference)
    {
        super.onAmountChanged(previousAmount, difference);
        BaseMod.MAX_HAND_SIZE += difference;
        if (this.amount >= 10)
        {
            this.type = PowerType.BUFF;
        }
        else
        {
            this.type = PowerType.DEBUFF;
        }

    }

    public void onVictory()
    {
        super.onVictory();
        this.onRemove();
    }
}
