package pinacolada.orbs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.localization.OrbStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.FocusPower;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.EUIColors;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.SFX;
import pinacolada.effects.vfx.OrbEvokeParticle;
import pinacolada.effects.vfx.megacritCopy.OrbFlareEffect2;
import pinacolada.misc.CombatManager;
import pinacolada.resources.PGR;
import pinacolada.utilities.GameUtilities;

import java.lang.reflect.InvocationTargetException;

// Copied and modified from STS-AnimatorMod
// TODO Make into a PointerProvider
public abstract class PCLOrb extends AbstractOrb
{
    @Deprecated
    public enum Timing
    {
        EndOfTurn,
        StartOfTurn
    }

    public static final int IMAGE_SIZE = 96;
    public final PCLAffinity affinity;
    public final boolean canOrbApplyFocusToEvoke;
    public final boolean canOrbApplyFocusToPassive;
    public boolean clickable;
    public EUITooltip tooltip;
    protected final OrbStrings orbStrings;

    public PCLOrb(String id, PCLAffinity affinity)
    {
        this(id, affinity, true, true);
    }

    public PCLOrb(String id, PCLAffinity affinity, boolean canOrbApplyFocusToEvoke)
    {
        this(id, affinity, canOrbApplyFocusToEvoke, true);
    }

    public PCLOrb(String id, PCLAffinity affinity, boolean canOrbApplyFocusToEvoke, boolean canOrbApplyFocusToPassive)
    {
        this.orbStrings = PGR.getOrbStrings(id);
        this.ID = id;
        this.name = orbStrings.NAME;
        this.affinity = affinity;
        this.tooltip = new EUITooltip(name, description);
        this.canOrbApplyFocusToEvoke = canOrbApplyFocusToEvoke;
        this.canOrbApplyFocusToPassive = canOrbApplyFocusToPassive;
    }

    public static String createFullID(Class<? extends PCLOrb> type)
    {
        return PGR.core.createID(type.getSimpleName());
    }

    public static int getFocus()
    {
        return GameUtilities.getPowerAmount(AbstractDungeon.player, FocusPower.POWER_ID);
    }

    public void onChannel()
    {
    }

    @Override
    public void triggerEvokeAnimation()
    {
        for (int i = 0; i < 4; i++)
        {
            PCLEffects.Queue.add(new OrbEvokeParticle(this.cX, this.cY, EUIColors.lerp(getColor1(), getColor2(), MathUtils.random(0, 0.5f))));
        }
    }

    @Override
    public AbstractOrb makeCopy()
    {
        try
        {
            return getClass().getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onEvoke()
    {
        evoke();
    }

    public void passive()
    {
        final OrbFlareEffect2 effect = getOrbFlareEffect();
        if (effect != null)
        {
            PCLActions.bottom.playVFX(effect, Settings.FAST_MODE ? 0 : (0.6F / (float)AbstractDungeon.player.orbs.size()));
        }

        CombatManager.onOrbPassiveEffect(this);
    }

    public void evoke()
    {
        // Orb Evoke event is already broadcast
    }

    protected OrbFlareEffect2 getOrbFlareEffect()
    {
        return new OrbFlareEffect2(this.cX, this.cY).setColors(getColor1(), getColor2());
    }

    protected Color getColor1()
    {
        return Color.WHITE;
    }

    protected Color getColor2()
    {
        return Color.LIGHT_GRAY;
    }

    protected String formatDescription(int index, Object... args)
    {
        if (orbStrings.DESCRIPTION == null || orbStrings.DESCRIPTION.length <= index)
        {
            EUIUtils.logError(this, "orbStrings.Description does not exist, " + this.name);
            return "";
        }
        return EUIUtils.format(orbStrings.DESCRIPTION[index], args);
    }

    public int getBaseEvokeAmount()
    {
        return this.baseEvokeAmount;
    }

    public int getBasePassiveAmount()
    {
        return this.basePassiveAmount;
    }

    public String getUpdatedDescription()
    {
        return formatDescription(0);
    }

    public void setBaseEvokeAmount(int amount, boolean relative)
    {
        this.baseEvokeAmount = relative ? this.baseEvokeAmount + amount : amount;
        applyFocus();
        this.updateDescription();
    }

    public void setBasePassiveAmount(int amount, boolean relative)
    {
        this.basePassiveAmount = relative ? this.basePassiveAmount + amount : amount;
        applyFocus();
        this.updateDescription();
    }

    @Override
    public void updateDescription()
    {
        this.applyFocus();
        this.description = getUpdatedDescription();
        tooltip.setDescription(this.description);
    }

    @Override
    public void applyFocus()
    {
        int focus = getFocus();
        if (canOrbApplyFocusToPassive)
        {
            this.passiveAmount = Math.max(0, this.basePassiveAmount + focus);
            if (canOrbApplyFocusToEvoke)
            {
                this.evokeAmount = Math.max(0, this.baseEvokeAmount + focus);
            }
        }
        CombatManager.onOrbApplyFocus(this);
    }


    @Override
    public void update()
    {
        hb.update();
        if (hb.hovered)
        {
            EUITooltip.queueTooltip(tooltip, InputHelper.mX + hb.width, InputHelper.mY + (hb.height * 0.5f));
        }
        this.fontScale = MathHelper.scaleLerpSnap(this.fontScale, 0.7F);

        if (clickable)
        {
            if (InputHelper.justClickedLeft)
            {
                hb.clickStarted = true;
                SFX.play(SFX.UI_CLICK_1);
            }
            else if (hb.clicked)
            {
                hb.clicked = false;
                evoke();
            }
        }
    }
}
