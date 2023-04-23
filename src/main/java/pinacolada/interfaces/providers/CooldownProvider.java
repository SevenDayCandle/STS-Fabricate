package pinacolada.interfaces.providers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.dungeon.CombatManager;

public interface CooldownProvider
{
    Color COOLDOWN_INCOMPLETE_COLOR = Settings.GREEN_TEXT_COLOR.cpy().lerp(Settings.CREAM_COLOR, 0.5f);
    boolean isDisplayingUpgrade();
    int getCooldown();
    int getBaseCooldown();
    void setCooldown(int value);
    default boolean progressCooldownAndTrigger(AbstractCreature source, AbstractCreature m, int amount)
    {
        boolean canProgress = CombatManager.onCooldownTriggered(source, m, this);
        if (canProgress)
        {
            int value = getCooldown();
            if (value <= 0)
            {
                reset();
                return true;
            }
            else
            {
                setCooldown(Math.max(0, value - amount));
            }
        }
        return false;
    }
    default void reset()
    {
        setCooldown(getBaseCooldown());
    }
    default boolean canActivate()
    {
        return getCooldown() <= 0;
    }
    default ColoredString getCooldownString()
    {
        int amount = getCooldown();
        if (isDisplayingUpgrade())
        {
            return new ColoredString(amount, Settings.GREEN_TEXT_COLOR);
        }
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
