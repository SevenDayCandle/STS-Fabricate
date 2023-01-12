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

    public static PMove dealDamageToRandom(int amount)
    {
        return dealDamageToRandom(amount, AbstractGameAction.AttackEffect.NONE);
    }

    public static PMove dealDamageToRandom(int amount, AbstractGameAction.AttackEffect attackEffect)
    {
        return new PMove_DealDamage(amount, attackEffect, PCLCardTarget.RandomEnemy);
    }

    public static PMove discard(int amount)
    {
        return new PMove_Discard(amount, PCLCardGroupHelper.Hand);
    }

    public static PMove discard(int amount, PCLCardGroupHelper... groups)
    {
        return new PMove_Discard(amount, groups);
    }

    public static PMove draw(int amount)
    {
        return new PMove_Draw(amount);
    }

    public static PMove draw(PCLCard card, PSkill.PCLCardValueSource valueSource)
    {
        return new PMove_Draw(0)
                .setSource(card, valueSource);
    }

    public static PMove enterStance(PCLStanceHelper... helper)
    {
        return new PMove_EnterStance(helper);
    }

    public static PMove evokeOrb(int amount, PCLOrbHelper... orb)
    {
        return new PMove_EvokeOrb(amount, 1, orb);
    }

    public static PMove evokeOrb(int amount, int extra, PCLOrbHelper... orb)
    {
        return new PMove_EvokeOrb(amount, extra, orb);
    }

    public static PMove exhaust(int amount)
    {
        return new PMove_Exhaust(amount, PCLCardGroupHelper.Hand);
    }

    public static PMove exhaust(int amount, PCLCardGroupHelper... groups)
    {
        return new PMove_Exhaust(amount, groups);
    }

    public static PMove fetch(int amount, PCLCardGroupHelper... groups)
    {
        return new PMove_Fetch(amount, groups);
    }

    public static PMove gain(int amount, PCLPowerHelper... powers)
    {
        return new PMove_StackPower(PCLCardTarget.Self, amount, powers);
    }

    public static PMove gain(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return new PMove_StackPower(PCLCardTarget.Self, 0, powers)
                .setSource(card, valueSource);
    }

    public static PMove gainBlock(int amount)
    {
        return new PMove_GainBlock(amount);
    }

    public static PMove gainBlock(PCLCardTarget target, int amount)
    {
        return new PMove_GainBlock(target, amount);
    }

    public static PMove gainBlock(PCLCard card)
    {
        return gainBlock(card, PSkill.PCLCardValueSource.Block);
    }

    public static PMove gainBlock(PCLCard card, PSkill.PCLCardValueSource valueSource)
    {
        return new PMove_GainBlock(0)
                .setSource(card, valueSource);
    }

    public static PMove gainEnergy(int amount)
    {
        return new PMove_GainEnergy(amount);
    }

    public static PMove gainGold(int amount)
    {
        return new PMove_GainGold(amount);
    }

    public static PMove gainOrbSlots(int amount)
    {
        return new PMove_GainOrbSlots(amount);
    }

    public static PMove gainOrbSlots(PCLCard card, PSkill.PCLCardValueSource valueSource)
    {
        return new PMove_GainOrbSlots(0)
                .setSource(card, valueSource);
    }

    public static PMove gainTempHP(int amount)
    {
        return new PMove_GainTempHP(amount);
    }

    public static PMove gainTempHP(PCLCardTarget target, int amount)
    {
        return new PMove_GainTempHP(target, amount);
    }

    public static PMove gainTempHP(PCLCard card)
    {
        return gainTempHP(card, PSkill.PCLCardValueSource.MagicNumber);
    }

    public static PMove gainTempHP(PCLCard card, PSkill.PCLCardValueSource valueSource)
    {
        return new PMove_GainTempHP(0)
                .setSource(card, valueSource);
    }

    public static PMove gainTemporary(int amount, PCLPowerHelper... powers)
    {
        return new PMove_StackTemporaryPower(PCLCardTarget.Self, amount, powers);
    }

    public static PMove gainTemporary(PCLCard card, PSkill.PCLCardValueSource valueSource, PCLPowerHelper... powers)
    {
        return new PMove_StackTemporaryPower(PCLCardTarget.Self, 0, powers)
                .setSource(card, valueSource);
    }

    public static PMove heal(int amount)
    {
        return new PMove_Heal(amount);
    }

    public static PMove heal(PCLCardTarget target, int amount)
    {
        return new PMove_Heal(target, amount);
    }

    public static PMove loseHp(int amount)
    {
        return new PMove_LoseHP(amount);
    }

    public static PMove loseHp(PCLCardTarget target, int amount)
    {
        return new PMove_LoseHP(target, amount);
    }

    public static PMove modifyAffinity(PCLAffinity... tag)
    {
        return new PMove_ModifyAffinity(1, tag);
    }

    public static PMove modifyAffinity(int amount, PCLAffinity... tag)
    {
        return new PMove_ModifyAffinity(amount, tag);
    }

    public static PMove modifyAffinity(int amount, int level, PCLAffinity... tag)
    {
        return new PMove_ModifyAffinity(amount, level, tag);
    }

    public static PMove modifyBlock(int block)
    {
        return new PMove_ModifyBlock(1, block);
    }

    public static PMove modifyBlock(int amount, int block, PCLCardGroupHelper... groups)
    {
        return new PMove_ModifyBlock(amount, block, groups);
    }

    public static PMove modifyCost(int block)
    {
        return new PMove_ModifyCost(1, block);
    }

    public static PMove modifyCost(int amount, int block, PCLCardGroupHelper... groups)
    {
        return new PMove_ModifyCost(amount, block, groups);
    }

    public static PMove modifyDamage(int damage)
    {
        return new PMove_ModifyDamage(1, damage);
    }

    public static PMove modifyDamage(int amount, int damage, PCLCardGroupHelper... groups)
    {
        return new PMove_ModifyDamage(amount, damage, groups);
    }

    public static PMove modifyTag(PCLCardTag... tag)
    {
        return new PMove_ModifyTag(1, tag);
    }

    public static PMove modifyTag(int amount, PCLCardTag... tag)
    {
        return new PMove_ModifyTag(amount, tag);
    }

    public static PMove obtain(String... cardData)
    {
        return new PMove_Obtain(1, cardData);
    }

    public static PMove obtain(int copies, String... cardData)
    {
        return new PMove_Obtain(copies, cardData);
    }

    public static PMove obtain(PCLCardData... cardData)
    {
        return obtain(1, cardData);
    }

    public static PMove obtain(int copies, PCLCardData... cardData)
    {
        return new PMove_Obtain(copies, EUIUtils.map(cardData, cd -> cd.ID));
    }

    public static PMove obtainDiscardPile(int copies, PCLCardData... cardData)
    {
        return new PMove_Obtain(copies, EUIUtils.map(cardData, cd -> cd.ID)).setCardGroup(PCLCardGroupHelper.DiscardPile);
    }

    public static PMove obtainDrawPile(int copies, PCLCardData... cardData)
    {
        return new PMove_Obtain(copies, EUIUtils.map(cardData, cd -> cd.ID)).setCardGroup(PCLCardGroupHelper.DrawPile);
    }

    public static PMove obtainRandom(int copies, int choices, PCLCardGroupHelper... cardgroup)
    {
        return new PMove_ObtainRandomCard(copies, choices, cardgroup);
    }

    public static PMove play(int copies, PCLCardTarget target, PCLCardGroupHelper... g)
    {
        return new PMove_Play(copies, target, g);
    }

    public static PMove playCopy(int copies, PCLCardTarget target, String... cardData)
    {
        return new PMove_PlayCopy(copies, target, cardData);
    }

    public static PMove playTop(int copies, PCLCardTarget target, PCLCardGroupHelper... g)
    {
        return new PMove_PlayTop(copies, target, g);
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
