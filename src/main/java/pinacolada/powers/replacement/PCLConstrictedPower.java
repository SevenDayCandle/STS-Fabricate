package pinacolada.powers.replacement;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.powers.interfaces.HealthBarRenderPower;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ConstrictedPower;
import pinacolada.utilities.GameUtilities;

// Variant of Constricted that shows health being decreased and that is cloneable
public class PCLConstrictedPower extends ConstrictedPower implements CloneablePowerInterface, HealthBarRenderPower
{
    private static final Color healthBarColor = Color.PURPLE.cpy();

    public PCLConstrictedPower(AbstractCreature owner, AbstractCreature source, int amount)
    {
        super(owner, source, amount);
    }

    @Override
    public int getHealthBarAmount()
    {
        return GameUtilities.getHealthBarAmount(owner, amount, true, true);
    }

    @Override
    public Color getColor()
    {
        return healthBarColor;
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new PCLConstrictedPower(owner, source, amount);
    }
}
