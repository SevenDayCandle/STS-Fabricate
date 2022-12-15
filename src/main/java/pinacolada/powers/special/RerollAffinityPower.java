package pinacolada.powers.special;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.core.Settings;
import extendedui.utilities.ColoredString;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.misc.CombatManager;
import pinacolada.misc.PCLHotkeys;
import pinacolada.powers.PCLPower;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;

public class RerollAffinityPower extends PCLPower
{
    public static final String POWER_ID = PGR.core.createID(RerollAffinityPower.class.getSimpleName());
    public boolean canChoose;

    public RerollAffinityPower(int amount)
    {
        super(null, POWER_ID);
        createTrigger((a, b) -> this.onClick(), amount, true, true);
        mainTip.subHeader = new ColoredString();
        this.hideAmount = true;
        this.img = PGR.core.images.core.leftArrow.texture();

        initialize(amount, NeutralPowertypePatch.NEUTRAL, false);
    }

    public void advance()
    {
        advance(PCLAffinity.getAvailableAffinities());
    }

    public void advance(PCLAffinity... choices)
    {
        PCLAffinity next = CombatManager.playerSystem.getActiveMeter().getNextAffinity();
        PCLActions.bottom.rerollAffinity(1).setAffinityChoices(choices).setOptions(!canChoose, true);
        PCLActions.bottom.rerollAffinity(0).setAffinityChoices(next).setOptions(true, true);
    }

    public String getCurrentDescription()
    {
        PCLAffinity af = CombatManager.playerSystem.getAffinity(0);
        return formatDescription(1, af.getTooltip(),
                CombatManager.playerSystem.getRerollDescription(),
                CombatManager.playerSystem.getRerollDescription2());
    }

    public String getNextDescription()
    {
        PCLAffinity af = CombatManager.playerSystem.getAffinity(1);
        return formatDescription(2, af.getTooltip());
    }

    @Override
    public String getUpdatedDescription()
    {
        PCLAffinity af = CombatManager.playerSystem.getAffinity(0);
        PCLAffinity af2 = CombatManager.playerSystem.getAffinity(1);
        if (af != null && af2 != null)
        {
            return formatDescription(0, af.getTooltip(), af2.getTooltip(), PCLHotkeys.rerollCurrent.getKeyString());
        }
        return "";
    }

    @Override
    public void updateDescription()
    {
        this.description = getUpdatedDescription();
        mainTip.setDescription(this.description);

        int uses = triggerCondition.pool.uses;
        if (uses >= 0 && PGR.isLoaded())
        {
            mainTip.subHeader.color = uses == 0 ? Settings.RED_TEXT_COLOR : Settings.GREEN_TEXT_COLOR;
            mainTip.subHeader.text = uses + "/" + triggerCondition.pool.baseUses + " " + PGR.core.strings.combat.rerolls;
        }
    }

    protected void findTooltipsFromText(String text)
    {
        // No-oping so that no additional tooltips get added
    }

    public void onClick()
    {
        triggerCondition.refresh(false, true);
        if (triggerCondition.interactable())
        {
            advance();
            this.triggerCondition.refresh(false, true);
            updateDescription();
        }
    }

    public void render(SpriteBatch sb)
    {
        PCLRenderHelpers.drawGrayscaleIf(sb, s -> PCLRenderHelpers.drawCentered(sb, Color.WHITE, this.img, hb.cX, hb.cY, ICON_SIZE, ICON_SIZE, 1, 0), !(enabled && triggerCondition.canUse()));
    }

    public RerollAffinityPower setCanChoose(boolean canChoose)
    {
        this.canChoose = canChoose;
        return this;
    }

}