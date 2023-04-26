package pinacolada.cards.base.modifiers;

import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.cards.base.tags.PCLCardTag;
import pinacolada.dungeon.CombatManager;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.skills.PSkill;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

@AbstractCardModifier.SaveIgnore
public class OverrideSkillModifier extends SkillModifier {
    public OverrideSkillModifier(String effectString) {
        super(effectString);
    }

    public OverrideSkillModifier(PSkill<?> effect) {
        super(effect);
    }

    public static ArrayList<OverrideSkillModifier> getAll(AbstractCard c) {
        return EUIUtils.mapAsNonnull(CardModifierManager.modifiers(c), mod -> EUIUtils.safeCast(mod, OverrideSkillModifier.class));
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (card instanceof EditorCard) {
            return rawDescription;
        }
        return skill.getExportText();
    }

    // Logic for handling override is handled in AbstractCardPatches
    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!(card instanceof EditorCard)) {
            this.manualUse(card, AbstractDungeon.player, target);
        }
    }

    public void onDrawn(AbstractCard card) {
        skill.triggerOnDraw(card);
    }

    public void onExhausted(AbstractCard card) {
        skill.triggerOnExhaust(card);
    }

    // For EditorCards, we can just clear their skills instead of having to rely on the patch to do things
    // For unplayable cards like statuses, remove unplayable so the card can be played, and show a cost to make the energy requirement more clear
    public void onInitialApplication(AbstractCard card) {
        if (card instanceof EditorCard) {
            ((EditorCard) card).clearSkills();
            ((EditorCard) card).addUseMove(this.skill);
        }
        PCLCardTag.Unplayable.set(card, 0);
        if (card.cost <= -2) {
            GameUtilities.modifyCostForCombat(card, 0, false);
        }
    }

    public void onRemove(AbstractCard card) {
        if (card instanceof EditorCard) {
            ((EditorCard) card).setup(null);
        }
    }

    public void onOtherCardPlayed(AbstractCard card, AbstractCard otherCard, CardGroup group) {
        skill.triggerOnOtherCardPlayed(otherCard);
    }

    public boolean canPlayCard(AbstractCard card) {
        return skill.canPlay(CombatManager.playerSystem.generateInfo(card, AbstractDungeon.player, null));
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new OverrideSkillModifier(serialized);
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
}