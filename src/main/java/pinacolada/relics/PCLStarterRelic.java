package pinacolada.relics;

import basemod.abstracts.CustomSavable;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import extendedui.EUIInputManager;
import extendedui.EUIUtils;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.augments.PCLAugmentWeights;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.screen.PCLAugmentSelectionEffect;
import pinacolada.interfaces.listeners.OnReceiveRewardsListener;
import pinacolada.resources.PGR;
import pinacolada.rewards.pcl.AugmentReward;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.WeightedList;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class PCLStarterRelic extends PCLRelic implements OnReceiveRewardsListener, CustomSavable<String>
{

    public static final String ID = createFullID(PCLStarterRelic.class);
    protected static final float AUGMENT_CHANCE = 0.05f;
    protected Class<? extends PCLStarterRelic> previousClass;
    public PCLAugment augment;

    public PCLStarterRelic(String id, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass pc)
    {
        this(id, tier, sfx, pc, null);
    }

    public PCLStarterRelic(String id, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass pc, Class<? extends PCLStarterRelic> previousClass)
    {
        super(id, tier, sfx, pc);
        this.previousClass = previousClass;
    }

    public PCLStarterRelic(String id, Texture texture, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass pc)
    {
        this(id, texture, tier, sfx, pc, null);
    }

    public PCLStarterRelic(String id, Texture texture, RelicTier tier, LandingSound sfx, AbstractPlayer.PlayerClass pc, Class<? extends PCLStarterRelic> previousClass)
    {
        super(id, texture, tier, sfx, pc);
        this.previousClass = previousClass;
    }

    protected String getFullDescription()
    {
        RelicStrings baseStrings = PGR.getRelicStrings(ID);
        return getUpdatedDescription() + EUIUtils.DOUBLE_SPLIT_LINE + (augment == null ? baseStrings.DESCRIPTIONS[0] : EUIUtils.format(baseStrings.DESCRIPTIONS[1], augment.getName()));
    }

    @Override
    public void onReceiveRewards(ArrayList<RewardItem> rewards, boolean b)
    {
        if (GameUtilities.inBossRoom() || rng.randomBoolean(GameUtilities.inEliteOrBossRoom() ? AUGMENT_CHANCE * 5 : AUGMENT_CHANCE))
        {
            WeightedList<PCLAugmentData> picks = PCLAugment.getWeightedList(new PCLAugmentWeights(PCLAffinity.Red, PCLAffinity.Green, PCLAffinity.Blue, PCLAffinity.Orange));
            PCLAugmentData firstData = picks.retrieve(rng);
            AugmentReward first = firstData != null ? new AugmentReward(firstData.create()) : null;
            if (first != null)
            {
                rewards.add(first);
            }
        }
    }

    public void updateDescription()
    {
        if (mainTooltip != null)
        {
            mainTooltip.setDescription(getFullDescription());
        }
    }

    @Override
    public void onEquip()
    {
        super.onEquip();
        updateDescription();
    }

    @Override
    public String onSave()
    {
        return augment != null ? augment.ID : null;
    }

    @Override
    public void onLoad(String s)
    {
        PCLAugmentData data = PCLAugment.get(s);
        if (data != null)
        {
            augment = data.create();
        }
    }

    @Override
    public Type savedType()
    {
        return new TypeToken<String>()
        {
        }.getType();
    }

    @Override
    public void update()
    {
        super.update();

        if (hb.hovered && EUIInputManager.rightClick.isJustPressed())
        {
            if (augment == null)
            {
                PCLEffects.Queue.callback(new PCLAugmentSelectionEffect(this)
                        .addCallback((augment -> {
                            if (augment != null)
                            {
                                PGR.core.dungeon.addAugment(augment.ID, -1);
                                this.augment = augment;
                                updateDescription();
                            }
                        })));
            }
            else
            {
                PGR.core.dungeon.addAugment(augment.ID, 1);
                augment = null;
                updateDescription();
            }
        }
    }

    @Override
    public boolean canSpawn()
    {
        return super.canSpawn() && (previousClass == null || EUIUtils.any(player.relics, r -> previousClass.isInstance(r)));
    }

    @Override
    public void obtain()
    {
        if (previousClass != null)
        {
            ArrayList<AbstractRelic> relics = player.relics;
            for (int i = 0; i < relics.size(); i++)
            {
                PCLStarterRelic relic = EUIUtils.safeCast(relics.get(i), previousClass);
                if (relic != null)
                {
                    instantObtain(player, i, true);
                    augment = relic.augment;
                    setCounter(relic.counter);
                    return;
                }
            }
        }

        super.obtain();
    }
}
