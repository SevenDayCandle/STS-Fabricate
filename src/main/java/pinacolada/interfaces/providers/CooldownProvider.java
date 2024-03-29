package pinacolada.interfaces.providers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import pinacolada.dungeon.CombatManager;

public interface CooldownProvider {
    Color COOLDOWN_INCOMPLETE_COLOR = Settings.GREEN_TEXT_COLOR.cpy().lerp(Settings.CREAM_COLOR, 0.5f);

    default boolean canActivate() {
        return getCooldown() <= 0;
    }

    default Color getCooldownColor() {
        int amount = getCooldown();
        if (isDisplayingUpgrade()) {
            return Settings.GREEN_TEXT_COLOR;
        }
        if (amount < getBaseCooldown()) {
            if (amount > 0) {
                return COOLDOWN_INCOMPLETE_COLOR;
            }
            else {
                return Settings.GREEN_TEXT_COLOR;
            }
        }
        else {
            return Settings.CREAM_COLOR;
        }
    }

    default boolean progressCooldownAndTrigger(AbstractCreature source, AbstractCreature m, int amount) {
        boolean canProgress = CombatManager.onCooldownTriggered(source, m, this);
        if (canProgress) {
            int value = getCooldown();
            if (value <= 0) {
                reset();
                return true;
            }
            else {
                setCooldown(Math.max(0, value - amount));
            }
        }
        return false;
    }

    default void reset() {
        setCooldown(getBaseCooldown());
    }

    int getBaseCooldown();

    int getCooldown();

    boolean isDisplayingUpgrade();

    void setCooldown(int value);
}
