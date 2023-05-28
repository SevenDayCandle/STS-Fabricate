package pinacolada.interfaces.listeners;

import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

public interface OnReceiveRewardsListener {
    void onReceiveRewards(ArrayList<RewardItem> rewards, AbstractRoom room);
}
