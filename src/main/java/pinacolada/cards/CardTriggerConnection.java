package pinacolada.cards;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.TriggerConnection;
import pinacolada.interfaces.subscribers.*;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

public class CardTriggerConnection implements TriggerConnection, OnPhaseChangedSubscriber, OnModifyBlockFirstSubscriber,
                                              OnModifyBlockLastSubscriber, OnModifyDamageGiveFirstSubscriber, OnModifyDamageGiveLastSubscriber,
                                              OnModifyDamageReceiveFirstSubscriber, OnModifyDamageReceiveLastSubscriber, OnTryUsingCardSubscriber {
    public final PTrigger trigger;
    public final AbstractCard card;

    public CardTriggerConnection(PTrigger trigger, AbstractCard card) {
        this.trigger = trigger;
        this.card = card;
    }

    @Override
    public boolean canActivate(PTrigger trigger) {
        return EUIUtils.any(trigger.fields.groupTypes, g -> {
            CardGroup gr = g.getCardGroup();
            return gr != null && gr.contains(card);
        });
    }

    @Override
    public boolean canUse(AbstractCard card, AbstractPlayer p, AbstractMonster m, boolean canUse) {
        if (canActivate(trigger)) {
            canUse = trigger.canPlay(trigger.getInfo(null), trigger);
        }
        return canUse;
    }

    @Override
    public AbstractCreature getOwner() {
        return GameUtilities.getCardOwner(card);
    }

    public void initialize() {
        subscribeToAll();
        trigger.subscribeChildren();
    }

    @Override
    public float onModifyBlockFirst(float amount, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyBlockFirst(trigger.getInfo(null), amount);
        }
        return amount;
    }

    @Override
    public float onModifyBlockLast(float amount, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyBlockLast(trigger.getInfo(null), amount);
        }
        return amount;
    }

    @Override
    public float onModifyDamageGiveFirst(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyDamageGiveFirst(trigger.getInfo(target), amount);
        }
        return amount;
    }

    @Override
    public float onModifyDamageGiveLast(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyDamageGiveLast(trigger.getInfo(target), amount);
        }
        return amount;
    }

    @Override
    public float onModifyDamageReceiveFirst(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyDamageReceiveFirst(trigger.getInfo(target), amount, type);
        }
        return amount;
    }

    @Override
    public float onModifyDamageReceiveLast(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyDamageReceiveLast(trigger.getInfo(target), amount, type);
        }
        return amount;
    }

    @Override
    public void onPhaseChanged(GameActionManager.Phase phase) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(card, getOwner(), null);
        trigger.refresh(info, true);
    }
}
