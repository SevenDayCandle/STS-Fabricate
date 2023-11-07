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
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;

import java.util.ArrayList;
import java.util.StringJoiner;

public class AugmentModifier extends AbstractCardModifier {
    protected PCLAugment.SaveData save;
    protected transient PCLAugment augment;
    protected transient PCLUseInfo info;
    private String cachedDesc;

    public AugmentModifier(PCLAugment.SaveData save) {
        this.save = save;
        this.augment = save.create();
    }

    public AugmentModifier(PCLAugment augment) {
        this.augment = augment;
        this.save = augment.save;
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
        PCLUseInfo info = getInfo(card, null);
        for (PSkill<?> be : augment.getFullEffects()) {
            if (!be.canPlay(info, null)) {
                return false;
            }
        }
        return true;
    }

    private String getDesc() {
        if (cachedDesc == null) {
            String breakStr = PGR.config.removeLineBreaks.get() ? " " : EUIUtils.LEGACY_DOUBLE_SPLIT_LINE;
            StringJoiner sb = new StringJoiner(breakStr);
            // Do not display skills that consist solely of traits
            for (PSkill<?> skill : augment.getFullEffects()) {
                if (!skill.isPassiveOnly()) {
                    sb.add(skill.getText(PCLCardTarget.Self, null, true));
                }
            }
            String res = sb.toString();
            cachedDesc = res.isEmpty() ? res : breakStr + res;
        }
        return cachedDesc;
    }

    public PCLUseInfo getInfo(AbstractCard card, AbstractCreature target) {
        if (info == null) {
            info = CombatManager.playerSystem.getInfo(card, AbstractDungeon.player, target);
        }
        return info;
    }

    @Override
    public String identifier(AbstractCard card) {
        return save.ID;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AugmentModifier(augment.makeCopy());
    }

    // Generate infos manually because we cannot attach the augment.skill to the card if it is not an EditorCard
    @Override
    public float modifyBlock(float block, AbstractCard card) {
        PCLUseInfo info = getInfo(card, null);
        for (PSkill<?> be : augment.getFullEffects()) {
            block = be.modifyBlockFirst(info, block);
        }
        return block;
    }

    @Override
    public float modifyBlockFinal(float block, AbstractCard card) {
        PCLUseInfo info = getInfo(card, null);
        for (PSkill<?> be : augment.getFullEffects()) {
            block = be.modifyBlockLast(info, block);
        }
        return block;
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        PCLUseInfo info = getInfo(card, target);
        for (PSkill<?> be : augment.getFullEffects()) {
            damage = be.modifyDamageGiveFirst(info, damage);
        }
        return damage;
    }

    @Override
    public float modifyDamageFinal(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        PCLUseInfo info = getInfo(card, target);
        for (PSkill<?> be : augment.getFullEffects()) {
            damage = be.modifyDamageGiveLast(info, damage);
        }
        return damage;
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + getDesc();
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
        for (PSkill<?> be : augment.getFullEffects()) {
            be.triggerOnDiscard(card);
        }
    }

    public void onDrawn(AbstractCard card) {
        for (PSkill<?> be : augment.getFullEffects()) {
            be.triggerOnDraw(card);
        }
    }

    public void onExhausted(AbstractCard card) {
        for (PSkill<?> be : augment.getFullEffects()) {
            be.triggerOnExhaust(card);
        }
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        for (PSkill<?> be : augment.getFullEffects()) {
            be.onAddToCard(card);
        }
    }

    public void onOtherCardPlayed(AbstractCard card, AbstractCard otherCard, CardGroup group) {
        for (PSkill<?> be : augment.getFullEffects()) {
            be.triggerOnOtherCardPlayed(otherCard);
        }
    }

    public void onPurged(AbstractCard card) {
        for (PSkill<?> be : augment.getFullEffects()) {
            be.triggerOnPurge(card);
        }
    }

    @Override
    public void onRemove(AbstractCard card) {
        for (PSkill<?> be : augment.getFullEffects()) {
            be.onRemoveFromCard(card);
        }
    }

    public void onReshuffled(AbstractCard card, CardGroup group) {
        for (PSkill<?> be : augment.getFullEffects()) {
            be.triggerOnReshuffle(card, group);
        }
    }

    public void onUpgraded(AbstractCard card) {
        for (PSkill<?> be : augment.getFullEffects()) {
            be.triggerOnUpgrade(card);
        }
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        PCLUseInfo info = getInfo(card, target);
        for (PSkill<?> be : augment.getFullEffects()) {
            be.use(info, PCLActions.bottom);
        }
    }

    public PCLUseInfo refreshInfo(AbstractCard card, AbstractCreature target) {
        info = null;
        return getInfo(card, target);
    }
}