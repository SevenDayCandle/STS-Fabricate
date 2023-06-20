package pinacolada.actions.cards;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import extendedui.EUIUtils;
import extendedui.utilities.EUIColors;
import pinacolada.actions.utility.GenericCardSelection;
import pinacolada.cardmods.SkillModifier;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.interfaces.providers.CooldownProvider;
import pinacolada.skills.PSkill;
import pinacolada.utilities.GameUtilities;

import java.util.ArrayList;

public class ProgressCooldown extends GenericCardSelection {
    protected int change;
    protected Color flashColor = EUIColors.gold(1).cpy();

    public ProgressCooldown(AbstractCreature source, AbstractCard card, int change) {
        this(source, card, 1, change);
    }

    protected ProgressCooldown(AbstractCreature source, AbstractCard card, int amount, int change) {
        super(card, amount);

        this.change = change;
        this.source = source;
    }

    @Override
    protected boolean canSelect(AbstractCard card) {
        return super.canSelect(card) && getCooldowns(card).size() > 0;
    }

    public ProgressCooldown flash(Color flashColor) {
        this.flashColor = flashColor;

        return this;
    }

    protected ArrayList<CooldownProvider> getCooldowns(AbstractCard c) {
        ArrayList<CooldownProvider> cooldowns = new ArrayList<>();
        EditorCard eC = EUIUtils.safeCast(c, EditorCard.class);
        if (eC != null) {
            for (PSkill<?> s : eC.getEffects()) {
                if (s instanceof CooldownProvider) {
                    cooldowns.add((CooldownProvider) s);
                }
            }
        }
        for (SkillModifier sk : SkillModifier.getAll(c)) {
            PSkill<?> s = sk.getSkill();
            if (s instanceof CooldownProvider) {
                cooldowns.add((CooldownProvider) s);
            }
        }
        return cooldowns;
    }

    @Override
    protected void selectCard(AbstractCard card) {
        super.selectCard(card);

        if (flashColor != null) {
            GameUtilities.flash(card, flashColor, true);
        }

        for (CooldownProvider cooldown : getCooldowns(card)) {
            cooldown.progressCooldownAndTrigger(source, null, change);
        }
    }
}
