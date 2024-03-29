package pinacolada.rewards;

import basemod.abstracts.CustomReward;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import extendedui.ui.tooltips.EUITooltip;
import pinacolada.resources.PGR;

// Copied and modified from STS-AnimatorMod
public abstract class PCLReward extends CustomReward {
    protected static final EUITooltip warningTip = new EUITooltip("", "");

    public PCLReward(String id, String text, RewardType type) {
        super(new Texture(PGR.getRewardImage(id)), text, type);
    }

    public PCLReward(Texture rewardImage, String text, RewardType type) {
        super(rewardImage, text, type);
    }

    public void render(SpriteBatch sb) {
        if (this.hb.hovered) {
            sb.setColor(new Color(0.4F, 0.6F, 0.6F, 1.0F));
        }
        else {
            sb.setColor(new Color(0.5F, 0.6F, 0.6F, 0.8F));
        }

        if (this.hb.clickStarted) {
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.xScale * 0.98F, Settings.scale * 0.98F, 0.0F, 0, 0, 464, 98, false, false);
        }
        else {
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.xScale, Settings.scale, 0.0F, 0, 0, 464, 98, false, false);
        }

        if (this.flashTimer != 0.0F) {
            sb.setColor(0.6F, 1.0F, 1.0F, this.flashTimer * 1.5F);
            sb.setBlendFunction(770, 1);
            sb.draw(ImageMaster.REWARD_SCREEN_ITEM, Settings.WIDTH / 2.0F - 232.0F, this.y - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.scale * 1.03F, Settings.scale * 1.15F, 0.0F, 0, 0, 464, 98, false, false);
            sb.setBlendFunction(770, 771);
        }

        sb.setColor(Color.WHITE);
        this.renderIcon(sb);

        FontHelper.renderSmartText(sb, FontHelper.cardDescFont_N, this.text, Settings.WIDTH * 0.434F, this.y + 5.0F * Settings.scale, 1000.0F * Settings.scale, 0.0F, this.hb.hovered ? Settings.GOLD_COLOR : Settings.CREAM_COLOR);
        if (!this.hb.hovered) {
            for (AbstractGameEffect e : this.effects) {
                e.render(sb);
            }
        }

        if (relicLink != null) {
            if (relicLink.hb.y > this.hb.y) {
                this.renderRelicLink(sb);
            }
            if (this.hb.justHovered) {
                warningTip.setText(this.text, TEXT[8] + FontHelper.colorString(this.relicLink.text + TEXT[9], "y"));
            }
            if (this.hb.hovered) {
                EUITooltip.queueTooltip(warningTip);
            }
        }


        this.hb.render(sb);
    }

    protected void renderIcon(SpriteBatch sb) {
        sb.draw(this.icon, RewardItem.REWARD_ITEM_X - 32.0F, this.y - 32.0F - 2.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
    }

    protected void renderRelicLink(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        sb.draw(ImageMaster.RELIC_LINKED, this.hb.cX - 64.0F, this.y - 64.0F + 52.0F * Settings.scale, 64.0F, 64.0F, 128.0F, 128.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 128, 128, false, false);
    }
}