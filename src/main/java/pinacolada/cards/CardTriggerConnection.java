package pinacolada.cards;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.cards.base.PCLCard;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.TriggerConnection;
import pinacolada.interfaces.providers.ClickableProvider;
import pinacolada.interfaces.subscribers.*;
import pinacolada.powers.PCLClickableUse;
import pinacolada.skills.skills.PTrigger;
import pinacolada.utilities.GameUtilities;

public class CardTriggerConnection implements ClickableProvider, TriggerConnection, OnPhaseChangedSubscriber, OnModifyBlockFirstSubscriber,
                                              OnModifyBlockLastSubscriber, OnModifyCostSubscriber, OnModifyDamageGiveFirstSubscriber, OnModifyDamageGiveLastSubscriber,
                                              OnModifyDamageReceiveFirstSubscriber, OnModifyDamageReceiveLastSubscriber, OnModifyHitCountSubscriber, OnModifyRightCountSubscriber, OnTryUsingCardSubscriber {
    public final PTrigger trigger;
    public final AbstractCard card;
    private PCLClickableUse triggerCondition;
    private EUIKeywordTooltip triggerTip;
    private final String unplayableMessage;

    public CardTriggerConnection(PTrigger trigger, AbstractCard card) {
        this.trigger = trigger;
        this.card = card;

        this.unplayableMessage = PCLCard.UNPLAYABLE_MESSAGE + " NL #r(" + EUIUtils.modifyString(card.name, " ", " ", 1, w -> "#r" + w) + ")";
    }

    @Override
    public boolean canActivate(PTrigger trigger) {
        return trigger.fields.groupTypes.isEmpty() || EUIUtils.any(trigger.fields.groupTypes, g -> {
            CardGroup gr = g.getCardGroup();
            return gr != null && gr.contains(card);
        });
    }

    @Override
    public boolean canUse(AbstractCard card, AbstractPlayer p, AbstractMonster m, boolean canUse) {
        if (canActivate(trigger)) {
            canUse = trigger.canPlay(CombatManager.playerSystem.getInfo(card, getOwner(), getOwner()), trigger);
        }
        return canUse;
    }

    @Override
    public PCLClickableUse getClickable() {
        return triggerCondition;
    }

    @Override
    public String getName() {
        return this.card.name;
    }

    @Override
    public String getID() {
        return this.card.cardID;
    }

    @Override
    public AbstractCreature getOwner() {
        return GameUtilities.getCardOwner(card);
    }

    @Override
    public EUIKeywordTooltip getTooltip() {
        return triggerTip;
    }

    @Override
    public String getUnplayableMessage() {
        return unplayableMessage;
    }

    public void initialize() {
        subscribeToAll();
        trigger.subscribeChildren();
        if (card instanceof PCLCard) {
            ((PCLCard) card).controller = this;
        }
        PCLClickableUse use = trigger.getClickable(this);
        if (use != null) {
            triggerCondition = use;
            triggerTip = new EUIKeywordTooltip(card.name, trigger.getPowerText(null));
        }
    }

    @Override
    public float onModifyBlockFirst(float amount, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyBlockFirst(CombatManager.playerSystem.getInfo(card, getOwner(), getOwner()), amount);
        }
        return amount;
    }

    @Override
    public float onModifyBlockLast(float amount, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyBlockLast(CombatManager.playerSystem.getInfo(card, getOwner(), getOwner()), amount);
        }
        return amount;
    }

    @Override
    public int onModifyCost(int amount, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyCost(CombatManager.playerSystem.getInfo(card, getOwner(), getOwner()), amount);
        }
        return amount;
    }

    @Override
    public float onModifyDamageGiveFirst(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyDamageGiveFirst(CombatManager.playerSystem.getInfo(card, source, target), amount);
        }
        return amount;
    }

    @Override
    public float onModifyDamageGiveLast(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyDamageGiveLast(CombatManager.playerSystem.getInfo(card, source, target), amount);
        }
        return amount;
    }

    @Override
    public float onModifyDamageReceiveFirst(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyDamageReceiveFirst(CombatManager.playerSystem.getInfo(card, source, target), amount, type);
        }
        return amount;
    }

    @Override
    public float onModifyDamageReceiveLast(float amount, DamageInfo.DamageType type, AbstractCreature source, AbstractCreature target, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = trigger.modifyDamageReceiveLast(CombatManager.playerSystem.getInfo(card, source, target), amount, type);
        }
        return amount;
    }

    @Override
    public int onModifyHitCount(int amount, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = (int) trigger.modifyHitCount(CombatManager.playerSystem.getInfo(card, getOwner(), getOwner()), amount);
        }
        return amount;
    }

    @Override
    public int onModifyRightCount(int amount, AbstractCard card) {
        if (canActivate(trigger)) {
            amount = (int) trigger.modifyRightCount(CombatManager.playerSystem.getInfo(card, getOwner(), getOwner()), amount);
        }
        return amount;
    }

    @Override
    public void onPhaseChanged(GameActionManager.Phase phase) {
        PCLUseInfo info = CombatManager.playerSystem.getInfo(card, getOwner(), null);
        trigger.refresh(info, true, false);
    }

    public void targetToUse(int amount) {
        if (triggerCondition != null) {
            triggerCondition.targetToUse(amount);
        }
    }
}
