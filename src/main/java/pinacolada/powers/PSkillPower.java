package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;
import pinacolada.cards.base.AffinityReactions;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.PCLUseInfo;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.subscribers.*;
import pinacolada.misc.CombatManager;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrigger;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PSkillPower extends PCLPower implements OnAllyDeathSubscriber, OnAllySummonSubscriber, OnAllyTriggerSubscriber, OnAllyWithdrawSubscriber, OnCardCreatedSubscriber,
                                                     OnCardDiscardedSubscriber, OnCardPurgedSubscriber, OnCardReshuffledSubscriber, OnChannelOrbSubscriber, OnElementReactSubscriber,
                                                     OnMatchSubscriber, OnNotMatchSubscriber, OnOrbApplyFocusSubscriber, OnOrbPassiveEffectSubscriber, OnPCLClickableUsedSubscriber, OnShuffleSubscriber, OnIntensifySubscriber
{
    public final ArrayList<PTrigger> ptriggers = new ArrayList<>();

    public PSkillPower(AbstractCreature owner, int turns, PTrigger... effects)
    {
        this(owner, turns, Arrays.asList(effects));
    }

    public PSkillPower(AbstractCreature owner, int turns, List<PTrigger> effects)
    {
        super(owner, owner);
        this.powerStrings = new PowerStrings();

        for (PTrigger effect : effects)
        {
            this.ptriggers.add(effect.makeCopy());
            effect.resetUses();

            if (this.powerStrings.NAME == null)
            {
                this.ID = createPowerID(effect);
                if (effect.sourceCard instanceof EditorCard)
                {
                    this.region48 = PCLRenderHelpers.generateIcon(((EditorCard) effect.sourceCard).getPortraitImageTexture());
                    this.powerStrings.NAME = effect.sourceCard.name;
                }
                else
                {
                    this.img = EUIRM.getTexture(PGR.core.createID("UnknownPower"));
                    this.powerStrings.NAME = effect.source != null ? effect.source.getName() : effect.effectID != null ? effect.effectID : "UnknownPower";
                }
            }

            if (effect instanceof PTrigger_Interactable)
            {
                triggerCondition = new PCLClickableUse(this, effect.getChild(), effect.amount <= 0 ? -1 : effect.amount, !effect.fields.not, true);
            }
        }

        setupDescription();
        if (turns > 0)
        {
            initialize(turns, NeutralPowertypePatch.NEUTRAL, true);
        }
        else
        {
            initialize(-1, NeutralPowertypePatch.NEUTRAL, false);
        }
    }

    public static String createPowerID(PSkill effect)
    {
        return effect != null ? deriveID(effect.source != null ? effect.source.getID() + effect.source.getPowerEffects().indexOf(effect) : effect.effectID) : null;
    }

    @Override
    protected ColoredString getSecondaryAmount(Color c)
    {
        for (PTrigger trigger : ptriggers)
        {
            int uses = trigger.getUses();
            if (!(trigger instanceof PTrigger_Interactable) && uses >= 0)
            {
                return new ColoredString(uses, uses > 0 ? Color.GREEN : Color.GRAY, c.a);
            }
        }
        return null;
    }

    @Override
    public String getUpdatedDescription()
    {
        this.powerStrings.DESCRIPTIONS = EUIUtils.mapAsNonnull(ptriggers, PSkill::getPowerText).toArray(new String[]{});
        return EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, this.powerStrings.DESCRIPTIONS);
    }

    @Override
    protected void onSamePowerApplied(AbstractPower power)
    {
        PSkillPower po = EUIUtils.safeCast(power, PSkillPower.class);
        if (po != null && this.ID.equals(po.ID))
        {
            // The effects of identical cards will always be in the same order
            for (int i = 0; i < Math.min(ptriggers.size(), po.ptriggers.size()); i++)
            {
                ptriggers.get(i).stack(po.ptriggers.get(i));
            }
        }
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new PSkillPower(owner, amount, EUIUtils.map(ptriggers, PTrigger::makeCopy));
    }

    public void atStartOfTurn()
    {
        super.atStartOfTurn();
        for (PTrigger effect : ptriggers)
        {
            effect.resetUses();
        }
        if (isTurnBased)
        {
            reducePower(1);
        }
    }

    public void onRemove()
    {
        super.onRemove();
        CombatManager.onCardCreated.unsubscribe(this);
        CombatManager.onCardDiscarded.unsubscribe(this);
        CombatManager.onCardPurged.unsubscribe(this);
        CombatManager.onCardReshuffled.unsubscribe(this);
        CombatManager.onChannelOrb.unsubscribe(this);
        CombatManager.onElementReact.unsubscribe(this);
        CombatManager.onMatch.unsubscribe(this);
        CombatManager.onNotMatch.unsubscribe(this);
        CombatManager.onOrbApplyFocus.unsubscribe(this);
        CombatManager.onOrbPassiveEffect.unsubscribe(this);
        CombatManager.onPCLClickablePowerUsed.unsubscribe(this);
        CombatManager.onShuffle.unsubscribe(this);
        CombatManager.onIntensify.unsubscribe(this);
    }

    public void onInitialApplication()
    {
        super.onInitialApplication();
        CombatManager.onCardCreated.subscribe(this);
        CombatManager.onCardDiscarded.subscribe(this);
        CombatManager.onCardPurged.subscribe(this);
        CombatManager.onCardReshuffled.subscribe(this);
        CombatManager.onChannelOrb.subscribe(this);
        CombatManager.onElementReact.subscribe(this);
        CombatManager.onMatch.subscribe(this);
        CombatManager.onNotMatch.subscribe(this);
        CombatManager.onOrbApplyFocus.subscribe(this);
        CombatManager.onOrbPassiveEffect.subscribe(this);
        CombatManager.onPCLClickablePowerUsed.subscribe(this);
        CombatManager.onShuffle.subscribe(this);
        CombatManager.onIntensify.subscribe(this);
    }

    @Override
    public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source)
    {
        super.onApplyPower(power, target, source);

        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnApplyPower(source, target, power)))
        {
            flash();
        }
    }

    public PSkillPower makeCopyOnTarget(AbstractCreature m, int amount)
    {
        return new PSkillPower(m, amount, EUIUtils.map(ptriggers, tr -> (PTrigger) tr.makeCopy()));
    }

    @Override
    public void onAllyDeath(PCLCard returned, PCLCardAlly ally)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnAllyDeath(returned, ally)))
        {
            flash();
        }
    }

    @Override
    public void onAllySummon(PCLCard card, PCLCardAlly ally)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnAllySummon(card, ally)))
        {
            flash();
        }
    }

    @Override
    public void onAllyTrigger(PCLCard card, PCLCardAlly ally)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnAllyTrigger(card, ally)))
        {
            flash();
        }
    }

    @Override
    public void onAllyWithdraw(PCLCard returned, PCLCardAlly ally)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnAllyWithdraw(returned, ally)))
        {
            flash();
        }
    }

    @Override
    public void onApplyFocus(AbstractOrb o)
    {
        for (PTrigger effect : ptriggers)
        {
            effect.triggerOnOrbFocus(o);
        }
    }

    @Override
    public void onCardCreated(AbstractCard c, boolean b)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnCreate(c, b)))
        {
            flash();
        }
    }

    @Override
    public void onCardDiscarded(AbstractCard c)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnDiscard(c)))
        {
            flash();
        }
    }

    @Override
    public void onCardReshuffled(AbstractCard c, CardGroup cardGroup)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnReshuffle(c, cardGroup)))
        {
            flash();
        }
    }

    @Override
    public void onChannelOrb(AbstractOrb o)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnOrbChannel(o)))
        {
            flash();
        }
    }

    @Override
    public boolean onClickablePowerUsed(PCLClickableUse c, AbstractMonster target, int uses)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnPCLPowerUsed(c)))
        {
            flash();
        }
        return true;
    }

    @Override
    public void onElementReact(AffinityReactions reactions, AbstractCreature m)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnElementReact(reactions, m)))
        {
            flash();
        }
    }

    @Override
    public void onIntensify(PCLAffinity button)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnIntensify(button)))
        {
            flash();
        }
    }

    @Override
    public void onMatch(AbstractCard c, PCLUseInfo info)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnMatch(c, info)))
        {
            flash();
        }
    }

    @Override
    public void onNotMatch(AbstractCard c, PCLUseInfo info)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnMismatch(c, info)))
        {
            flash();
        }
    }

    @Override
    public void onOrbPassiveEffect(AbstractOrb o)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnOrbTrigger(o)))
        {
            flash();
        }
    }

    @Override
    public void onPurge(AbstractCard c)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnPurge(c)))
        {
            flash();
        }
    }

    @Override
    public void onShuffle(boolean triggerRelics)
    {
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnShuffle(triggerRelics)))
        {
            flash();
        }
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card)
    {
        for (PTrigger effect : ptriggers)
        {
            damage = effect.atDamageGive(owner, damage, type, card);
        }
        return super.atDamageGive(damage, type, card);
    }

    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card)
    {
        for (PTrigger effect : ptriggers)
        {
            damage = effect.atDamageReceive(owner, damage, type, card);
        }
        return super.atDamageReceive(damage, type, card);
    }

    public void atStartOfTurnPostDraw()
    {
        super.atStartOfTurnPostDraw();
        if (EUIUtils.any(ptriggers, PSkill::triggerOnStartOfTurn))
        {
            flash();
        }
    }

    public void atEndOfTurn(boolean isPlayer)
    {
        super.atEndOfTurn(isPlayer);
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnEndOfTurn(true)))
        {
            flash();
        }
    }

    public void onScry()
    {
        super.onScry();
        if (EUIUtils.any(ptriggers, PSkill::triggerOnScry))
        {
            flash();
        }
    }

    public int onAttacked(DamageInfo info, int damageAmount)
    {
        for (PTrigger effect : ptriggers)
        {
            damageAmount = effect.triggerOnAttacked(info, damageAmount);
        }
        return super.onAttacked(info, damageAmount);
    }

    public void onAttack(DamageInfo info, int damageAmount, AbstractCreature target)
    {
        for (PTrigger effect : ptriggers)
        {
            damageAmount = effect.triggerOnAttack(info, damageAmount, target);
        }
        super.onAttack(info, damageAmount, target);
    }

    public void onEvokeOrb(AbstractOrb c)
    {
        super.onEvokeOrb(c);
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnOrbEvoke(c)))
        {
            flash();
        }
    }

    public void onCardDraw(AbstractCard c)
    {
        super.onCardDraw(c);
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnDraw(c)))
        {
            flash();
        }
    }

    public void onPlayCard(AbstractCard c, AbstractMonster m)
    {
        super.onPlayCard(c, m);
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnOtherCardPlayed(c)))
        {
            flash();
        }
    }

    public void onExhaust(AbstractCard c)
    {
        super.onExhaust(c);
        if (EUIUtils.any(ptriggers, effect -> effect.triggerOnExhaust(c)))
        {
            flash();
        }
    }

    public float modifyBlock(float damage, AbstractCard card)
    {
        for (PTrigger effect : ptriggers)
        {
            damage = effect.atBlockGain(owner, damage, card);
        }
        return super.modifyBlock(damage, card);
    }

    public boolean canPlayCard(AbstractCard card)
    {
        for (PTrigger effect : ptriggers)
        {
            effect.refresh(EUIUtils.safeCast(owner, AbstractMonster.class), card, true);
        }
        return super.canPlayCard(card);
    }
}
