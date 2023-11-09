package pinacolada.rewards.pcl;

import basemod.BaseMod;
import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardSave;
import extendedui.EUIRenderHelpers;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.effects.PCLSFX;
import pinacolada.resources.PCLEnum;
import pinacolada.resources.PGR;
import pinacolada.rewards.PCLReward;

import java.util.ArrayList;

public class AugmentReward extends PCLReward {
    public final PCLAugment augment;

    public AugmentReward(PCLAugment augment) {
        super(augment.getTextureBase(), augment.getName(), PCLEnum.Rewards.AUGMENT);

        this.augment = augment;
    }

    @Override
    public boolean claimReward() {
        PCLSFX.play(PCLSFX.RELIC_DROP_MAGICAL);
        PGR.dungeon.addAugment(augment.save);
        return true;
    }

    @Override
    public void renderIcon(SpriteBatch sb) {
        EUIRenderHelpers.drawCentered(sb, Color.WHITE, augment.getTextureBase(), RewardItem.REWARD_ITEM_X, this.y - 2.0F * Settings.scale, 64f, 64f, 1f, 0);
        Texture tex = augment.getTexture();
        if (tex != null) {
            EUIRenderHelpers.drawCentered(sb, Color.WHITE, tex, RewardItem.REWARD_ITEM_X, this.y - 2.0F * Settings.scale, 64f, 64f, 1f, 0);
        }
        EUIRenderHelpers.drawCentered(sb, Color.WHITE, this.icon, RewardItem.REWARD_ITEM_X, this.y - 2.0F * Settings.scale, 64f, 64f, 1f, 0);
    }

    @Override
    public void update() {
        super.update();
        if (this.hb.hovered) {
            EUITooltip.queueTooltip(augment.getTooltip());
        }
    }

    public static class Serializer implements BaseMod.LoadCustomReward, BaseMod.SaveCustomReward {
        @Override
        public CustomReward onLoad(RewardSave rewardSave) {
            PCLAugment.SaveData save = EUIUtils.deserialize(rewardSave.id, PCLAugment.SaveData.class);
            if (save != null) {
                return new AugmentReward(save.create());
            }
            ArrayList<PCLAugmentData> available = PCLAugmentData.getAvailable();
            if (available.size() > 0) {
                return new AugmentReward(available.get(0).create());
            }
            return null;
        }

        @Override
        public RewardSave onSave(CustomReward customReward) {
            AugmentReward reward = EUIUtils.safeCast(customReward, AugmentReward.class);
            if (reward != null) {
                return new RewardSave(reward.type.toString(), EUIUtils.serialize(reward.augment.save), 1, 0);
            }

            return null;
        }
    }
}