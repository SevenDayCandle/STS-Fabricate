package pinacolada.powers.common;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.Dark;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.utilities.ColoredString;
import pinacolada.interfaces.subscribers.OnChannelOrbSubscriber;
import pinacolada.interfaces.subscribers.OnEvokeOrbSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.powers.PCLPower;
import pinacolada.utilities.GameUtilities;

public class BlindedPower extends PCLPower implements OnChannelOrbSubscriber, OnEvokeOrbSubscriber
{
    public static final String POWER_ID = createFullID(BlindedPower.class);
    public static final int DAMAGE_REDUCTION_LV1 = 3;
    public static final int DAMAGE_REDUCTION_LV2 = 4;

    private final AbstractCreature source;

    public BlindedPower(AbstractCreature owner, AbstractCreature source, int amount)
    {
        super(owner, POWER_ID);

        this.source = source;

        initialize(amount, PowerType.DEBUFF, true);
    }

    public static int getDamageReduction()
    {
        return (GameUtilities.hasOrb(Dark.ORB_ID) ? DAMAGE_REDUCTION_LV2 : DAMAGE_REDUCTION_LV1) + (int) CombatManager.getEffectBonus(POWER_ID);
    }

    @Override
    public String getUpdatedDescription()
    {
        return formatDescription(0, getDamageReduction());
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new BlindedPower(owner, source, amount);
    }

    @Override
    public void onInitialApplication()
    {
        super.onInitialApplication();

        CombatManager.onChannelOrb.subscribe(this);
        CombatManager.onEvokeOrb.subscribe(this);
    }

    @Override
    public void onRemove()
    {
        super.onRemove();

        CombatManager.onChannelOrb.unsubscribe(this);
        CombatManager.onEvokeOrb.unsubscribe(this);
    }

    @Override
    protected ColoredString getSecondaryAmount(Color c)
    {
        return new ColoredString(-1 * getDamageReduction(), Color.RED, c.a);
    }

    @Override
    public void onChannelOrb(AbstractOrb orb)
    {
        if (Dark.ORB_ID.equals(orb.ID))
        {
            AbstractDungeon.onModifyPower();
            updateDescription();
        }
    }

    @Override
    public void onEvokeOrb(AbstractOrb orb)
    {
        if (Dark.ORB_ID.equals(orb.ID))
        {
            AbstractDungeon.onModifyPower();
            updateDescription();
        }
    }

    @Override
    public float atDamageGive(float damage, DamageInfo.DamageType type)
    {
        return super.atDamageGive(type == DamageInfo.DamageType.NORMAL ? (damage - getDamageReduction()) : damage, type);
    }

    @Override
    public void atEndOfTurn(boolean isPlayer)
    {
        super.atEndOfTurn(isPlayer);

        reducePower(1);
    }
}
