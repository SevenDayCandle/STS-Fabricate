package pinacolada.actions.affinity;

import com.megacrit.cardcrawl.core.Settings;
import extendedui.EUIUtils;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.misc.CombatManager;
import pinacolada.ui.combat.PCLPlayerMeter;
import pinacolada.utilities.GameUtilities;

import java.util.Arrays;

public class RerollAffinity extends PCLAction<PCLAffinity>
{
    public boolean isRandom;
    public boolean showEffect;
    protected PCLPlayerMeter meter;
    protected PCLAffinity[] affinityChoices;
    protected int target;

    public RerollAffinity(int target)
    {
        super(ActionType.POWER, Settings.ACTION_DUR_XFAST);

        this.target = target;
        this.isRandom = true;
        this.meter = CombatManager.playerSystem.getActiveMeter();
    }

    @Override
    protected void firstUpdate()
    {
        if (isRandom)
        {
            complete(meter.set(GameUtilities.getRandomElement(getAffinityChoices()), target));
        }
        else
        {
            PCLActions.top.tryChooseAffinity(name, 1, source, null, Arrays.asList(getAffinityChoices())).addCallback(choices -> {
                if (choices.size() > 0)
                {
                    complete(meter.set(choices.get(0).value, target));
                }
            });
        }

        if (showEffect)
        {
            meter.flash(target);
        }
    }

    protected PCLAffinity[] getAffinityChoices()
    {
        if (affinityChoices != null && affinityChoices.length > 0)
        {
            return affinityChoices;
        }
        PCLAffinity[] possiblePicks = PCLAffinity.getAvailableAffinities();
        if (possiblePicks.length == 0)
        {
            possiblePicks = PCLAffinity.basic();
        }
        return isRandom ? EUIUtils.filter(possiblePicks, a -> meter.getCurrentAffinity() != a).toArray(new PCLAffinity[]{}) : possiblePicks;
    }

    public RerollAffinity setAffinityChoices(PCLAffinity... affinities)
    {
        this.affinityChoices = affinities;

        return this;
    }

    public RerollAffinity setOptions(boolean isRandom, boolean showEffect)
    {
        this.isRandom = isRandom;
        this.showEffect = showEffect;

        return this;
    }
}
