package pinacolada.cards.base.modifiers;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIUtils;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.misc.CombatManager;
import pinacolada.skills.PSkill;

import java.util.ArrayList;

@AbstractCardModifier.SaveIgnore
public class SkillModifier extends AbstractCardModifier
{
    protected String serialized;
    protected transient PSkill<?> skill;

    public static ArrayList<? extends SkillModifier> getAll(AbstractCard c)
    {
        return EUIUtils.mapAsNonnull(CardModifierManager.modifiers(c), mod -> EUIUtils.safeCast(mod, SkillModifier.class));
    }

    public SkillModifier(String serialized)
    {
        this.serialized = serialized;
        this.skill = PSkill.get(serialized);
    }

    public SkillModifier(PSkill<?> skill)
    {
        this.skill = skill;
        this.serialized = skill.serialize();
    }

    public void manualUse(AbstractCard card, AbstractPlayer player, AbstractCreature monster)
    {
        skill.use(CombatManager.playerSystem.generateInfo(card, player, monster));
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card)
    {
        if (card instanceof EditorCard)
        {
            return rawDescription;
        }
        return rawDescription + EUIUtils.SPLIT_LINE + skill.getText(true);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action)
    {
        if (!(card instanceof EditorCard))
        {
            this.manualUse(card, AbstractDungeon.player, target);
        }
    }

    public void onDrawn(AbstractCard card) {
        if (!(card instanceof EditorCard))
        {
            skill.triggerOnDraw(card);
        }
    }

    public void onDiscard(AbstractCard card) {
        if (!(card instanceof EditorCard))
        {
            skill.triggerOnDiscard(card);
        }
    }

    public void onExhausted(AbstractCard card) {
        if (!(card instanceof EditorCard))
        {
            skill.triggerOnExhaust(card);
        }
    }

    public void onPurged(AbstractCard card) {
        if (!(card instanceof EditorCard))
        {
            skill.triggerOnPurge(card);
        }
    }

    public void onReshuffled(AbstractCard card, CardGroup group) {
        if (!(card instanceof EditorCard))
        {
            skill.triggerOnReshuffle(card, group);
        }
    }

    public void onOtherCardPlayed(AbstractCard card, AbstractCard otherCard, CardGroup group) {
        if (!(card instanceof EditorCard))
        {
            skill.triggerOnOtherCardPlayed(otherCard);
        }
    }

    public boolean canPlayCard(AbstractCard card) {
        if (!(card instanceof EditorCard))
        {
            return skill.canPlay(CombatManager.playerSystem.generateInfo(card, AbstractDungeon.player, null));
        }
        return true;
    }

    // Generate infos manually because we cannot be able to attach the skill to the card if it is not an EditorCard
    public float modifyBlock(float block, AbstractCard card) {
        return skill.modifyBlock(CombatManager.playerSystem.generateInfo(card, AbstractDungeon.player, null), block);
    }

    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return skill.modifyDamage(CombatManager.playerSystem.generateInfo(card, AbstractDungeon.player, target), damage);
    }

    @Override
    public AbstractCardModifier makeCopy()
    {
        return new SkillModifier(serialized);
    }

    public void onInitialApplication(AbstractCard card) {
        if (card instanceof EditorCard)
        {
            ((EditorCard) card).addUseMove(this.skill);
        }
    }

    public void onRemove(AbstractCard card) {
        if (card instanceof EditorCard)
        {
            ((EditorCard) card).tryRemove(((EditorCard) card).getEffects().indexOf(this.skill));
        }
    }

    @Override
    public String identifier(AbstractCard card)
    {
        return skill.effectID + skill.uuid;
    }

    public PSkill<?> getSkill()
    {
        return skill;
    }
}