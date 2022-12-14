package pinacolada.cards.pcl.curse;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLCardData;
import pinacolada.cards.base.PCLCardTarget;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.interfaces.subscribers.OnApplyPowerSubscriber;
import pinacolada.interfaces.subscribers.OnPhaseChangedSubscriber;
import pinacolada.misc.CombatManager;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.skills.PCustomCond;
import pinacolada.actions.PCLActions;
import pinacolada.utilities.GameUtilities;

import java.util.HashMap;
import java.util.UUID;

public class Curse_Normality extends PCLCard
{
    public static final PCLCardData DATA = register(Curse_Normality.class)
            .setCurse(-2, PCLCardTarget.None, false, false)
            .setTags(PCLCardTag.Unplayable);

    public Curse_Normality()
    {
        super(DATA);
    }

    public void setup(Object input)
    {
        addUseMove(new NormalityMove(DATA));
    }

    public static class NormalityMove extends PCustomCond implements OnApplyPowerSubscriber, OnPhaseChangedSubscriber
    {
        protected static final HashMap<AbstractCreature, HashMap<String, Integer>> POWERS = new HashMap<>();
        protected static UUID battleID;
        protected static int turnCache;

        public NormalityMove(PCLCardData data)
        {
            super(data);
        }

        protected static void checkForNewBattle()
        {
            if (CombatManager.battleID != battleID)
            {
                battleID = CombatManager.battleID;
                turnCache = -1;
                POWERS.clear();
            }
        }

        protected static boolean hasNormality()
        {
            return EUIUtils.any(player.hand.group, c -> c instanceof PointerProvider && EUIUtils.any(((PointerProvider) c).getSkills().onUseEffects, e -> e instanceof NormalityMove));
        }

        @Override
        public boolean triggerOnCreate(AbstractCard c, boolean startOfBattle)
        {
            tryActivate();
            CombatManager.onApplyPower.subscribe(this);
            CombatManager.onPhaseChanged.subscribe(this);
            return false;
        }

        // Apply the power then remove it, to allow effects that trigger on power application to activate
        @Override
        public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source)
        {
            PCLActions.last.callback(() -> {
                if (hasNormality() && (GameUtilities.isCommonBuff(power) || GameUtilities.isCommonDebuff(power)))
                {
                    negatePower(power, target);
                }
            });
        }

        @Override
        public void onPhaseChanged(GameActionManager.Phase phase)
        {
            tryActivate();
        }

        @Override
        public void refresh(AbstractCreature m, AbstractCard c, boolean conditionMet)
        {
            super.refresh(m, c, conditionMet);
            if (GameUtilities.inBattle())
            {
                tryActivate();
            }
        }

        protected void storePower(AbstractPower power, AbstractCreature owner, int amount)
        {
            HashMap<String, Integer> targetSet = POWERS.getOrDefault(owner, new HashMap<>());
            targetSet.merge(power.ID, amount, Integer::sum);
            POWERS.put(owner, targetSet);
        }

        protected void negatePower(AbstractPower power, AbstractCreature owner)
        {
            checkForNewBattle();
            storePower(power, owner, power.amount);
            PCLActions.bottom.callback(-power.amount, (a, __) -> {
                GameUtilities.applyPowerInstantly(owner, power, a);
            });
        }

        protected void tryActivate()
        {
            if (GameUtilities.isPlayerTurn(true))
            {
                checkForNewBattle();
                boolean has = hasNormality();
                if (has && turnCache < 0)
                {
                    for (AbstractCreature c : GameUtilities.getAllCharacters(true))
                    {
                        if (c.powers != null)
                        {
                            for (AbstractPower po : c.powers)
                            {
                                if ((GameUtilities.isCommonBuff(po) || GameUtilities.isCommonDebuff(po)))
                                {
                                    negatePower(po, c);
                                }
                            }
                        }
                    }
                    turnCache = GameActionManager.turn;
                }
                else if (!has && turnCache >= 0)
                {
                    for (AbstractCreature owner : POWERS.keySet())
                    {
                        if (owner != null)
                        {
                            HashMap<String, Integer> targetSet = POWERS.getOrDefault(owner, new HashMap<>());
                            for (String powerID : targetSet.keySet())
                            {
                                int amount = targetSet.getOrDefault(powerID, 0);
                                PCLPowerHelper ph = PCLPowerHelper.get(powerID);

                                if (turnCache < GameActionManager.turn)
                                {
                                    int diff = GameActionManager.turn - turnCache;
                                    // Turn based powers cannot go below 0
                                    if (ph.endTurnBehavior == PCLPowerHelper.Behavior.TurnBased)
                                    {
                                        amount = Math.max(0, amount - diff);
                                    }
                                    else if (ph.endTurnBehavior == PCLPowerHelper.Behavior.SingleTurn)
                                    {
                                        amount = 0;
                                    }
                                }

                                if (ph != null && amount != 0)
                                {
                                    PCLActions.bottom.callback(amount, (a, __) -> {
                                        GameUtilities.applyPowerInstantly(owner, ph, a);
                                    });

                                }
                                targetSet.put(powerID, 0);
                            }

                        }
                    }
                    POWERS.clear();
                    turnCache = -1;
                }
            }
        }

        /*        @Override
        public boolean TryApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source, AbstractGameAction action)
        {
            if (HasNormality() && (PCLGameUtilities.IsCommonBuff(power) || PCLGameUtilities.IsCommonDebuff(power)))
            {
                StorePower(power, target, power.amount);
                return false;
            }
            return true;
        }*/
    }
}