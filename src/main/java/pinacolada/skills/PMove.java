package pinacolada.skills;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import extendedui.EUIUtils;
import pinacolada.cards.base.*;
import pinacolada.cards.base.fields.PCLCardTag;
import pinacolada.interfaces.markers.PointerProvider;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.skills.fields.PField;
import pinacolada.skills.skills.base.moves.*;
import pinacolada.skills.skills.special.moves.PMove_DealCardDamage;
import pinacolada.skills.skills.special.moves.PMove_Stun;
import pinacolada.stances.PCLStanceHelper;

public abstract class PMove<T extends PField> extends PSkill<T>
{

    public PMove(PSkillSaveData content)
    {
        super(content);
    }

    public PMove(PSkillData<T> data)
    {
        super(data);
    }

    public PMove(PSkillData<T> data, PCLCardTarget target, int amount)
    {
        super(data, target, amount);
    }

    public PMove(PSkillData<T> data, PCLCardTarget target, int amount, int extra)
    {
        super(data, target, amount, extra);
    }

    public static PMove_AddLevel addLevel(int amount, PCLAffinity... affinities)
    {
        return new PMove_AddLevel(amount, affinities);
    }

    public static PMove_AddPowerBonus addPowerBonus(int amount, PCLPowerHelper... p)
    {
        return new PMove_AddPowerBonus(amount, p);
    }

    public static PMove_StackPower apply(PCLCardTarget target, int amount, PCLPowerHelper... powers)
    {
        return new PMove_StackPower(target, amount, powers);
    }

    public static PMove_StackPower apply(PCLCardTarget target, PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return (PMove_StackPower) new PMove_StackPower(target, 0, powers)
                .setSource(card, valueSource)
                .setAmountFromCard();
    }

    public static PMove_StackPower applyToAllies(int amount, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.AllAlly, amount, powers);
    }

    public static PMove_StackPower applyToAllies(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.AllAlly, card, valueSource, powers);
    }

    public static PMove_StackPower applyToEnemies(int amount, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.AllEnemy, amount, powers);
    }

    public static PMove_StackPower applyToEnemies(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.AllEnemy, card, valueSource, powers);
    }

    public static PMove_StackPower applyToEveryone(int amount, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.All, amount, powers);
    }

    public static PMove_StackPower applyToRandom(int amount, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.RandomEnemy, amount, powers);
    }

    public static PMove_StackPower applyToRandom(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.RandomEnemy, card, valueSource, powers);
    }

    public static PMove_StackPower applyToSingle(int amount, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.Single, amount, powers);
    }

    public static PMove_StackPower applyToSingle(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.Single, card, valueSource, powers);
    }

    public static PMove_StackPower applyToTeam(int amount, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.Team, amount, powers);
    }

    public static PMove_StackPower applyToTeam(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return apply(PCLCardTarget.Team, card, valueSource, powers);
    }

    public static PMove_ChannelOrb channelOrb(int amount, PCLOrbHelper... orb)
    {
        return new PMove_ChannelOrb(amount, orb);
    }

    public static PMove_ChannelOrb channelOrb(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLOrbHelper... orb)
    {
        return (PMove_ChannelOrb) new PMove_ChannelOrb(0, orb)
                .setSource(card, valueSource);
    }

    public static PMove_Cycle cycle(int amount)
    {
        return new PMove_Cycle(amount);
    }

    public static PMove_Cycle cycleRandom(int amount)
    {
        return (PMove_Cycle) new PMove_Cycle(amount).edit(f -> f.setRandom(true));
    }

    public static PMove_DealCardDamage dealCardDamage(PCLCard card)
    {
        return dealCardDamage(card, AbstractGameAction.AttackEffect.NONE);
    }

    public static PMove_DealCardDamage dealCardDamage(PCLCard card, AbstractGameAction.AttackEffect attackEffect)
    {
        return new PMove_DealCardDamage(card, attackEffect);
    }

    public static PMove_DealDamage dealDamage(int amount)
    {
        return dealDamage(amount, AbstractGameAction.AttackEffect.NONE);
    }

    public static PMove_DealDamage dealDamage(int amount, AbstractGameAction.AttackEffect attackEffect)
    {
        return new PMove_DealDamage(amount, attackEffect);
    }

    public static PMove_DealDamage dealDamageToAll(int amount)
    {
        return dealDamageToAll(amount, AbstractGameAction.AttackEffect.NONE);
    }

    public static PMove_DealDamage dealDamageToAll(int amount, AbstractGameAction.AttackEffect attackEffect)
    {
        return new PMove_DealDamage(amount, attackEffect, PCLCardTarget.AllEnemy);
    }

    public static PMove_DealDamage dealDamageToAll(PCLCard card)
    {
        return dealDamageToAll(card, PSkill.PCLCardValueSource.Damage, AbstractGameAction.AttackEffect.NONE);
    }

    public static PMove_DealDamage dealDamageToAll(PCLCard card, AbstractGameAction.AttackEffect attackEffect)
    {
        return dealDamageToAll(card, PSkill.PCLCardValueSource.Damage, attackEffect);
    }

    public static PMove_DealDamage dealDamageToAll(PCLCard card, PSkill.PCLCardValueSource valueSource, AbstractGameAction.AttackEffect attackEffect)
    {
        return (PMove_DealDamage) new PMove_DealDamage(0, attackEffect, PCLCardTarget.AllEnemy)
                .setSource(card, valueSource);
    }

    public static PMove_DealDamage dealDamageToRandom(int amount)
    {
        return dealDamageToRandom(amount, AbstractGameAction.AttackEffect.NONE);
    }

    public static PMove_DealDamage dealDamageToRandom(int amount, AbstractGameAction.AttackEffect attackEffect)
    {
        return new PMove_DealDamage(amount, attackEffect, PCLCardTarget.RandomEnemy);
    }

    public static PMove_Discard discard(int amount)
    {
        return new PMove_Discard(amount, PCLCardGroupHelper.Hand);
    }

    public static PMove_Discard discard(int amount, PCLCardGroupHelper... groups)
    {
        return new PMove_Discard(amount, groups);
    }

    public static PMove_Discard discardRandom(int amount)
    {
        return (PMove_Discard) new PMove_Discard(amount, PCLCardGroupHelper.Hand).edit(f -> f.setRandom(true));
    }

    public static PMove_Discard discardRandom(int amount, PCLCardGroupHelper... groups)
    {
        return (PMove_Discard) new PMove_Discard(amount, groups).edit(f -> f.setRandom(true));
    }

    public static PMove_Draw draw(int amount)
    {
        return new PMove_Draw(amount);
    }

    public static PMove_Draw draw(PCLCard card, PSkill.PCLCardValueSource valueSource)
    {
        return (PMove_Draw) new PMove_Draw(0)
                .setSource(card, valueSource);
    }

    public static PMove_EnterStance enterStance(PCLStanceHelper... helper)
    {
        return new PMove_EnterStance(helper);
    }

    public static PMove_EvokeOrb evokeOrb(int amount, PCLOrbHelper... orb)
    {
        return new PMove_EvokeOrb(amount, 1, orb);
    }

    public static PMove_EvokeOrb evokeOrb(int amount, int extra, PCLOrbHelper... orb)
    {
        return new PMove_EvokeOrb(amount, extra, orb);
    }

    public static PMove_Exhaust exhaust(int amount)
    {
        return new PMove_Exhaust(amount, PCLCardGroupHelper.Hand);
    }

    public static PMove_Exhaust exhaust(int amount, PCLCardGroupHelper... groups)
    {
        return new PMove_Exhaust(amount, groups);
    }

    public static PMove_Fetch fetch(int amount, PCLCardGroupHelper... groups)
    {
        return new PMove_Fetch(amount, groups);
    }

    public static PMove_StackPower gain(int amount, PCLPowerHelper... powers)
    {
        return new PMove_StackPower(PCLCardTarget.Self, amount, powers);
    }

    public static PMove_StackPower gain(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return (PMove_StackPower) new PMove_StackPower(PCLCardTarget.Self, 0, powers)
                .setSource(card, valueSource);
    }

    public static PMove_GainBlock gainBlock(int amount)
    {
        return new PMove_GainBlock(amount);
    }

    public static PMove_GainBlock gainBlock(PCLCardTarget target, int amount)
    {
        return new PMove_GainBlock(target, amount);
    }

    public static PMove_GainBlock gainBlock(PCLCard card)
    {
        return gainBlock(card, PSkill.PCLCardValueSource.Block);
    }

    public static PMove_GainBlock gainBlock(PCLCard card, PSkill.PCLCardValueSource valueSource)
    {
        return (PMove_GainBlock) new PMove_GainBlock(0)
                .setSource(card, valueSource);
    }

    public static PMove_GainEnergy gainEnergy(int amount)
    {
        return new PMove_GainEnergy(amount);
    }

    public static PMove_GainGold gainGold(int amount)
    {
        return new PMove_GainGold(amount);
    }

    public static PMove_GainOrbSlots gainOrbSlots(int amount)
    {
        return new PMove_GainOrbSlots(amount);
    }

    public static PMove_GainOrbSlots gainOrbSlots(PCLCard card, PSkill.PCLCardValueSource valueSource)
    {
        return (PMove_GainOrbSlots) new PMove_GainOrbSlots(0)
                .setSource(card, valueSource);
    }

    public static PMove_GainTempHP gainTempHP(int amount)
    {
        return new PMove_GainTempHP(amount);
    }

    public static PMove_GainTempHP gainTempHP(PCLCardTarget target, int amount)
    {
        return new PMove_GainTempHP(target, amount);
    }

    public static PMove_GainTempHP gainTempHP(PCLCard card)
    {
        return gainTempHP(card, PSkill.PCLCardValueSource.MagicNumber);
    }

    public static PMove_GainTempHP gainTempHP(PCLCard card, PSkill.PCLCardValueSource valueSource)
    {
        return (PMove_GainTempHP) new PMove_GainTempHP(0)
                .setSource(card, valueSource);
    }

    public static PMove_StackTemporaryPower gainTemporary(int amount, PCLPowerHelper... powers)
    {
        return new PMove_StackTemporaryPower(PCLCardTarget.Self, amount, powers);
    }

    public static PMove_StackTemporaryPower gainTemporary(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return (PMove_StackTemporaryPower) new PMove_StackTemporaryPower(PCLCardTarget.Self, 0, powers)
                .setSource(card, valueSource);
    }

    public static PMove_Heal heal(int amount)
    {
        return new PMove_Heal(amount);
    }

    public static PMove_Heal heal(PCLCardTarget target, int amount)
    {
        return new PMove_Heal(target, amount);
    }

    public static PMove_LoseHP loseHp(int amount)
    {
        return new PMove_LoseHP(amount);
    }

    public static PMove_LoseHP loseHp(PCLCardTarget target, int amount)
    {
        return new PMove_LoseHP(target, amount);
    }

    public static PMove_ModifyAffinity modifyAffinity(PCLAffinity... tag)
    {
        return new PMove_ModifyAffinity(1, tag);
    }

    public static PMove_ModifyAffinity modifyAffinity(int amount, PCLAffinity... affinity)
    {
        return new PMove_ModifyAffinity(amount, affinity);
    }

    public static PMove_ModifyAffinity modifyAffinity(int amount, int level, PCLAffinity... affinity)
    {
        return new PMove_ModifyAffinity(amount, level, affinity);
    }

    public static PMove_ModifyBlock modifyBlock(int block)
    {
        return new PMove_ModifyBlock(1, block);
    }

    public static PMove_ModifyBlock modifyBlock(int amount, int block, PCLCardGroupHelper... groups)
    {
        return new PMove_ModifyBlock(amount, block, groups);
    }

    public static PMove_ModifyCost modifyCost(int block)
    {
        return new PMove_ModifyCost(1, block);
    }

    public static PMove_ModifyCost modifyCost(int amount, int cost, PCLCardGroupHelper... groups)
    {
        return new PMove_ModifyCost(amount, cost, groups);
    }

    public static PMove_ModifyDamage modifyDamage(int damage)
    {
        return new PMove_ModifyDamage(1, damage);
    }

    public static PMove_ModifyDamage modifyDamage(int amount, int damage, PCLCardGroupHelper... groups)
    {
        return new PMove_ModifyDamage(amount, damage, groups);
    }

    public static PMove_ModifyTag modifyTag(PCLCardTag... tag)
    {
        return new PMove_ModifyTag(1, 1, tag);
    }

    public static PMove_ModifyTag modifyTag(int amount, int extra, PCLCardTag... tag)
    {
        return new PMove_ModifyTag(amount, extra, tag);
    }

    public static PMove_Obtain obtain(String... cardData)
    {
        return new PMove_Obtain(1, cardData);
    }

    public static PMove_Obtain obtain(int copies, String... cardData)
    {
        return new PMove_Obtain(copies, cardData);
    }

    public static PMove_Obtain obtain(PCLCardData... cardData)
    {
        return obtain(1, cardData);
    }

    public static PMove_Obtain obtain(int copies, PCLCardData... cardData)
    {
        return new PMove_Obtain(copies, EUIUtils.map(cardData, cd -> cd.ID));
    }

    public static PMove_Obtain obtainDiscardPile(int copies, PCLCardData... cardData)
    {
        return (PMove_Obtain) new PMove_Obtain(copies, EUIUtils.map(cardData, cd -> cd.ID)).edit(f -> f.setCardGroup(PCLCardGroupHelper.DiscardPile));
    }

    public static PMove_Obtain obtainDrawPile(int copies, PCLCardData... cardData)
    {
        return (PMove_Obtain) new PMove_Obtain(copies, EUIUtils.map(cardData, cd -> cd.ID)).edit(f -> f.setCardGroup(PCLCardGroupHelper.DrawPile));
    }

    public static PMove_ObtainRandomCard obtainRandom(int copies, int choices, PCLCardGroupHelper... cardgroup)
    {
        return new PMove_ObtainRandomCard(copies, choices, cardgroup);
    }

    public static PMove_Play play(int copies, PCLCardTarget target, PCLCardGroupHelper... g)
    {
        return new PMove_Play(copies, target, g);
    }

    public static PMove_PlayCopy playCopy(int copies, PCLCardTarget target, String... cardData)
    {
        return new PMove_PlayCopy(copies, target, cardData);
    }

    public static PMove purge(int amount)
    {
        return new PMove_Purge(amount, PCLCardGroupHelper.Hand);
    }

    public static PMove purge(int amount, PCLCardGroupHelper... groups)
    {
        return new PMove_Purge(amount, groups);
    }

    public static PMove reduceCooldown(int amount, int cooldown, PCLCardGroupHelper... groups)
    {
        return new PMove_ReduceCooldown(amount, cooldown, groups);
    }

    public static PMove remove(PCLCardTarget target, PCLPowerHelper... powers)
    {
        return new PMove_RemovePower(target, powers);
    }

    public static PMove removeSelf(PCLPowerHelper... powers)
    {
        return new PMove_RemovePower(PCLCardTarget.Self, powers);
    }

    public static PMove reshuffle(int amount)
    {
        return new PMove_Reshuffle(amount, PCLCardGroupHelper.Hand);
    }

    public static PMove reshuffle(int amount, PCLCardGroupHelper... groups)
    {
        return new PMove_Reshuffle(amount, groups);
    }

    public static PMove retain(int amount)
    {
        return new PMove_Retain(amount, PCLCardGroupHelper.Hand);
    }

    public static PMove scout(int amount)
    {
        return new PMove_Scout(amount);
    }

    public static PMove scry(int amount)
    {
        return new PMove_Scry(amount);
    }

    public static PMove scry(PCLCard card, PSkill.PCLCardValueSource valueSource)
    {
        return new PMove_Scry(0)
                .setSource(card, valueSource);
    }

    public static PMove selfExhaust()
    {
        return new PMove_Exhaust();
    }

    public static PMove selfPurge()
    {
        return new PMove_Purge();
    }

    public static PMove selfRetain()
    {
        return new PMove_Retain();
    }

    public static PMove selfTransform(PCLCardData cardData)
    {
        return new PMove_Transform(cardData.ID);
    }

    public static PMove stabilize(PCLCardTarget target, PCLPowerHelper... helpers)
    {
        return new PMove_StabilizePower(target, helpers);
    }

    public static PMove stun(int amount)
    {
        return new PMove_Stun(amount);
    }

    public static PMove_DealDamage takeDamage(int amount)
    {
        return takeDamage(amount, AbstractGameAction.AttackEffect.NONE);
    }

    public static PMove_DealDamage takeDamage(int amount, AbstractGameAction.AttackEffect attackEffect)
    {
        return new PMove_DealDamage(amount, attackEffect, PCLCardTarget.Self);
    }

    public static PMove triggerAlly(int amount)
    {
        return new PMove_TriggerAlly(amount);
    }

    public static PMove triggerAlly(PCLCardTarget target, int amount)
    {
        return new PMove_TriggerAlly(target, amount);
    }

    public static PMove triggerOrb(int amount, PCLOrbHelper... orb)
    {
        return new PMove_TriggerOrb(amount, 1, orb);
    }

    public static PMove triggerOrb(int amount, int extra, PCLOrbHelper... orb)
    {
        return new PMove_TriggerOrb(amount, extra, orb);
    }

    public static PMove triggerOrb(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLOrbHelper... orb)
    {
        return new PMove_TriggerOrb(0, orb)
                .setSource(card, valueSource);
    }

    public static PMove withdrawAlly(int amount)
    {
        return new PMove_WithdrawAlly(amount);
    }

    public static PMove withdrawAlly(PCLCardTarget target, int amount)
    {
        return new PMove_WithdrawAlly(target, amount);
    }

    @Override
    public PMove setAmountFromCard()
    {
        super.setAmountFromCard();
        return this;
    }

    @Override
    public PMove setSource(PointerProvider card)
    {
        super.setSource(card);
        return this;
    }

    @Override
    public PMove setSource(PointerProvider card, PCLCardValueSource valueSource)
    {
        super.setSource(card, valueSource);
        return this;
    }

}
