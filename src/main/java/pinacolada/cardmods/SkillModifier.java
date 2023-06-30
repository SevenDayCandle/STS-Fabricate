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
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.skills.PSkill;

import java.util.ArrayList;

@AbstractCardModifier.SaveIgnore
public class SkillModifier extends AbstractCardModifier {
    protected String serialized;
    protected transient PSkill<?> skill;
    protected PCLUseInfo info;

    public SkillModifier(String serialized) {
        this.serialized = serialized;
        this.skill = PSkill.get(serialized);
    }

    public SkillModifier(PSkill<?> skill) {
        this.skill = skill;
        this.serialized = skill.serialize();
    }

    public static ArrayList<? extends SkillModifier> getAll(AbstractCard c) {
        return EUIUtils.mapAsNonnull(CardModifierManager.modifiers(c), mod -> EUIUtils.safeCast(mod, SkillModifier.class));
    }

    public PCLUseInfo getInfo(AbstractCard card, AbstractCreature target) {
        if (info == null) {
            info = CombatManager.playerSystem.generateInfo(card, AbstractDungeon.player, target);
        }
        return info;
    }

    public PSkill<?> getSkill() {
        return skill;
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return skill.modifyDamage(getInfo(card, target), damage);
    }

    // Generate infos manually because we cannot attach the skill to the card if it is not an EditorCard
    @Override
    public float modifyBlock(float block, AbstractCard card) {
        return skill.modifyBlock(getInfo(card, null), block);
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + EUIUtils.SPLIT_LINE + skill.getText(true);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        skill.use(getInfo(card, target), PCLActions.bottom);
    }

    public void onDrawn(AbstractCard card) {
        skill.triggerOnDraw(card);
    }

    public void onExhausted(AbstractCard card) {
        skill.triggerOnExhaust(card);
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (card instanceof PointerProvider) {
            skill.setSource((PointerProvider) card).onAddToCard(card);
        }
        else {
            skill.sourceCard = card;
        }
    }

    @Override
    public void onRemove(AbstractCard card) {
        skill.onRemoveFromCard(card);
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        info = refreshInfo(card, null);
    }

    @Override
    public void onCalculateCardDamage(AbstractCard card, AbstractMonster mo) {
        info = refreshInfo(card, mo);
    }

    public void onOtherCardPlayed(AbstractCard card, AbstractCard otherCard, CardGroup group) {
        skill.triggerOnOtherCardPlayed(otherCard);
    }

    public boolean canPlayCard(AbstractCard card) {
        return skill.canPlay(getInfo(card, null));
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SkillModifier(serialized);
    }

    @Override
    public String identifier(AbstractCard card) {
        return skill.effectID + skill.getUUID();
    }

    public void onDiscard(AbstractCard card) {
        skill.triggerOnDiscard(card);
    }

    public void onPurged(AbstractCard card) {
        skill.triggerOnPurge(card);
    }

    public void onReshuffled(AbstractCard card, CardGroup group) {
        skill.triggerOnReshuffle(card, group);
    }

    public void onUpgraded(AbstractCard card) {
        skill.triggerOnUpgrade(card);
    }

    public PCLUseInfo refreshInfo(AbstractCard card, AbstractCreature target) {
        info = null;
        return getInfo(card, target);
    }
}