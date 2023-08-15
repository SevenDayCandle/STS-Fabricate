package pinacolada.cardmods;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.augments.PCLAugment;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrait;

import java.util.ArrayList;

@AbstractCardModifier.SaveIgnore
public class AugmentModifier extends AbstractCardModifier {
    protected String augmentID;
    protected transient PCLAugment augment;
    protected PCLUseInfo info;

    public AugmentModifier(String augmentID) {
        this.augmentID = augmentID;
        this.augment = PCLAugmentData.get(augmentID).create();
    }

    public AugmentModifier(PCLAugment augment) {
        this.augment = augment;
        this.augmentID = augment.ID;
    }

    public static AugmentModifier apply(PCLAugment augment, AbstractCard c) {
        AugmentModifier mod = new AugmentModifier(augment);
        CardModifierManager.addModifier(c, mod);
        return mod;
    }

    public static ArrayList<? extends AugmentModifier> getAll(AbstractCard c) {
        return EUIUtils.mapAsNonnull(CardModifierManager.modifiers(c), mod -> EUIUtils.safeCast(mod, AugmentModifier.class));
    }

    public boolean canPlayCard(AbstractCard card) {
        return augment.skill.canPlay(getInfo(card, null), null);
    }

    public PCLUseInfo getInfo(AbstractCard card, AbstractCreature target) {
        if (info == null) {
            info = CombatManager.playerSystem.getInfo(card, AbstractDungeon.player, target);
        }
        return info;
    }

    public PSkill<?> getSkill() {
        return augment.skill;
    }

    @Override
    public String identifier(AbstractCard card) {
        return augment.skill.effectID + augment.skill.getUUID();
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AugmentModifier(augment.ID);
    }

    // Generate infos manually because we cannot attach the augment.skill to the card if it is not an EditorCard
    @Override
    public float modifyBlock(float block, AbstractCard card) {
        return augment.skill.modifyBlockFirst(getInfo(card, null), block);
    }

    @Override
    public float modifyBlockFinal(float block, AbstractCard card) {
        return augment.skill.modifyBlockLast(getInfo(card, null), block);
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return augment.skill.modifyDamageGiveFirst(getInfo(card, target), damage);
    }

    @Override
    public float modifyDamageFinal(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return augment.skill.modifyDamageGiveLast(getInfo(card, target), damage);
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return augment.skill instanceof PTrait ? rawDescription : rawDescription + EUIUtils.SPLIT_LINE + augment.skill.getText(PCLCardTarget.Self, true);
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return EUIRM.strings.adjNoun(augment.getName(), cardName);
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        info = refreshInfo(card, null);
    }

    @Override
    public void onCalculateCardDamage(AbstractCard card, AbstractMonster mo) {
        info = refreshInfo(card, mo);
    }

    public void onDiscard(AbstractCard card) {
        augment.skill.triggerOnDiscard(card);
    }

    public void onDrawn(AbstractCard card) {
        augment.skill.triggerOnDraw(card);
    }

    public void onExhausted(AbstractCard card) {
        augment.skill.triggerOnExhaust(card);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card instanceof PointerProvider) {
            augment.skill.setSource((PointerProvider) card).onAddToCard(card);
        }
        else {
            augment.skill.sourceCard = card;
        }
    }

    public void onOtherCardPlayed(AbstractCard card, AbstractCard otherCard, CardGroup group) {
        augment.skill.triggerOnOtherCardPlayed(otherCard);
    }

    public void onPurged(AbstractCard card) {
        augment.skill.triggerOnPurge(card);
    }

    @Override
    public void onRemove(AbstractCard card) {
        augment.skill.onRemoveFromCard(card);
    }

    public void onReshuffled(AbstractCard card, CardGroup group) {
        augment.skill.triggerOnReshuffle(card, group);
    }

    public void onUpgraded(AbstractCard card) {
        augment.skill.triggerOnUpgrade(card);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        augment.skill.use(getInfo(card, target), PCLActions.bottom);
    }

    public PCLUseInfo refreshInfo(AbstractCard card, AbstractCreature target) {
        info = null;
        return getInfo(card, target);
    }
}