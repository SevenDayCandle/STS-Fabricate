package pinacolada.interfaces.markers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.misc.CombatStats;

public interface CooldownProvider
{
    final static Color COOLDOWN_INCOMPLETE_COLOR = Settings.GREEN_TEXT_COLOR.cpy().lerp(Settings.CREAM_COLOR, 0.5f);
    public int getCooldown();
    public int getBaseCooldown();
    public void setCooldown(int value);
    public void activate(AbstractCard card, AbstractCreature m);
    public default void progressCooldownAndTrigger(AbstractCard card, AbstractCreature m, int amount)
    {
        boolean canProgress = CombatStats.onCooldownTriggered(card, m, this);
        if (canProgress)
        {
            int value = getCooldown();
            if (value <= 0)
            {
                reset();
                activate(card, m);
            }
            else
            {
                setCooldown(Math.max(0, value - amount));
            }
        }
    }

    public default void reset()
    {
        setCooldown(getBaseCooldown());
    }

    public default ColoredString getCooldownString()
    {
        int amount = getCooldown();
        if (amount < getBaseCooldown())
        {
            if (amount > 0)
            {
                return new ColoredString(amount, COOLDOWN_INCOMPLETE_COLOR);
            }
            else
            {
                return new ColoredString(amount, Settings.GREEN_TEXT_COLOR);
            }
        }
        else
        {
            return new ColoredString(amount, Settings.CREAM_COLOR);
        }
    }
}
