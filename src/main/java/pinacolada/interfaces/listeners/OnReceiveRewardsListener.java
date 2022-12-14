package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.rewards.RewardItem;

import java.util.ArrayList;

public interface OnReceiveRewardsListener
{
    void onReceiveRewards(ArrayList<RewardItem> rewards, boolean normalRewards);
}
