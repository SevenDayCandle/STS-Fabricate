package pinacolada.rewards.pcl;

import basemod.BaseMod;
import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.augments.PCLAugment;
import pinacolada.effects.SFX;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.rewards.PCLReward;

public class AugmentReward extends PCLReward
{
    public static final String ID = createFullID(AugmentReward.class);

    public final PCLAugment augment;

    public AugmentReward(PCLAugment augment)
    {
        super(augment.getTexture(), augment.getName(), PCLEnum.Rewards.AUGMENT);

        this.augment = augment;
    }

    @Override
    public boolean claimReward()
    {
        SFX.play(SFX.RELIC_DROP_MAGICAL);
        PGR.core.dungeon.addAugment(augment.ID, 1);
        this.isDone = true;
        return true;
    }

    @Override
    public void update()
    {
        super.update();
        if (this.hb.hovered)
        {
            EUITooltip.queueTooltip(augment.getTip());
        }
    }

    @Override
    public void renderIcon(SpriteBatch sb)
    {
        EUIRenderHelpers.drawColorized(sb, s -> EUIRenderHelpers.drawCentered(sb, augment.getColor(), this.icon, RewardItem.REWARD_ITEM_X, this.y - 2.0F * Settings.scale, 64f, 64f, 1f, 0));
    }

    public static class Serializer implements BaseMod.LoadCustomReward, BaseMod.SaveCustomReward
    {
        @Override
        public CustomReward onLoad(RewardSave rewardSave)
        {
            return new AugmentReward(PCLAugment.get(rewardSave.id).create());
        }

        @Override
        public RewardSave onSave(CustomReward customReward)
        {
            AugmentReward reward = EUIUtils.safeCast(customReward, AugmentReward.class);
            if (reward != null)
            {
                return new RewardSave(reward.type.toString(), reward.augment.ID, 1, 0);
            }

            return null;
        }
    }
}