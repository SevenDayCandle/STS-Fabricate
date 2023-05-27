package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;
import org.apache.commons.lang3.StringUtils;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreImages;
import pinacolada.skills.PSkill;
import pinacolada.skills.skills.PTrigger;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PSkillPower extends PCLPower {
    public final ArrayList<PTrigger> ptriggers = new ArrayList<>();

    public PSkillPower(AbstractCreature owner, int turns, PTrigger... effects) {
        this(owner, turns, Arrays.asList(effects));
    }

    public PSkillPower(AbstractCreature owner, int turns, List<PTrigger> effects) {
        super(owner, owner);
        this.powerStrings = new PowerStrings();

        for (PTrigger effect : effects) {
            effect = effect.makeCopy();
            this.ptriggers.add(effect);
            effect.power = this;
            effect.resetUses();

            if (this.powerStrings.NAME == null) {
                this.ID = createPowerID(effect);
                if (effect.sourceCard instanceof EditorCard) {
                    // Vanilla rendering cannot render generated region48 properly
                    if (PGR.config.vanillaPowerRender.get()) {
                        this.img = PCLCoreImages.CardAffinity.unknown.texture();
                    }
                    else {
                        this.region48 = PCLRenderHelpers.generateIcon(((EditorCard) effect.sourceCard).getPortraitImageTexture());
                    }
                    this.powerStrings.NAME = effect.sourceCard.name;
                }
                else {
                    this.img = PCLCoreImages.CardAffinity.unknown.texture();
                    this.powerStrings.NAME = effect.source != null ? effect.source.getName() : effect.effectID != null ? effect.effectID : this.getClass().getSimpleName();
                }
            }

            PCLClickableUse use = effect.getClickable(this);
            if (use != null) {
                triggerCondition = use;
            }
        }

        setupDescription();
        if (turns > 0) {
            initialize(turns, getPowerType(), true);
        }
        else {
            initialize(-1, getPowerType(), false);
        }
    }

    public static String createPowerID(PSkill<?> effect) {
        return effect != null ? deriveID(effect.source != null ? effect.source.getID() + effect.source.getPowerEffects().indexOf(effect) : effect.effectID) : null;
    }

    public float atDamageGive(float block, DamageInfo.DamageType type, AbstractCard c) {
        return atDamageGive(CombatManager.playerSystem.generateInfo(c, owner, owner), block, type, c);
    }

    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card) {
        PCLUseInfo info = CombatManager.playerSystem.generateInfo(card, owner, owner);
        refreshTriggers(info);
        for (PTrigger effect : ptriggers) {
            damage = effect.modifyDamageIncoming(info, damage, type);
        }
        return super.atDamageReceive(damage, type, card);
    }

    // Update this power's effects whenever you play a card
    public void onAfterUseCard(AbstractCard card, UseCardAction act) {
        refreshTriggers(CombatManager.playerSystem.generateInfo(card, owner, owner));
        super.onAfterUseCard(card, act);
    }

    public float modifyBlock(float block, AbstractCard c) {
        return modifyBlock(CombatManager.playerSystem.generateInfo(c, owner, owner), block, c);
    }

    public float atDamageGive(PCLUseInfo info, float damage, DamageInfo.DamageType type, AbstractCard c) {
        refreshTriggers(info);
        for (PTrigger effect : ptriggers) {
            damage = effect.modifyDamage(info, damage);
        }
        return damage;
    }

    public PowerType getPowerType() {
        for (PTrigger trigger : ptriggers) {
            if (trigger.isDetrimental()) {
                return PowerType.DEBUFF;
            }
        }
        return PowerType.BUFF;
    }

    @Override
    protected ColoredString getSecondaryAmount(Color c) {
        for (PTrigger trigger : ptriggers) {
            int uses = trigger.getUses();
            if (!(trigger instanceof PTrigger_Interactable) && uses >= 0) {
                return new ColoredString(uses, uses > 0 ? Color.GREEN : Color.GRAY, c.a);
            }
        }
        return null;
    }

    @Override
    public String getUpdatedDescription() {
        this.powerStrings.DESCRIPTIONS = EUIUtils.mapAsNonnull(ptriggers, PSkill::getPowerText).toArray(new String[]{});
        return StringUtils.capitalize(EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, this.powerStrings.DESCRIPTIONS));
    }

    @Override
    public AbstractPower makeCopy() {
        return new PSkillPower(owner, amount, EUIUtils.map(ptriggers, PTrigger::makeCopy));
    }

    public float modifyBlock(PCLUseInfo info, float block, AbstractCard c) {
        refreshTriggers(info);
        for (PTrigger effect : ptriggers) {
            block = effect.modifyBlock(info, block);
        }
        return block;
    }

    public float modifyHeal(PCLUseInfo info, float damage, AbstractCard c) {
        refreshTriggers(info);
        for (PTrigger effect : ptriggers) {
            damage = effect.modifyHeal(info, damage);
        }
        return damage;
    }

    public float modifyHitCount(PCLUseInfo info, float damage, AbstractCard c) {
        refreshTriggers(info);
        for (PTrigger effect : ptriggers) {
            damage = effect.modifyHitCount(info, damage);
        }
        return damage;
    }

    public float modifyMagicNumber(PCLUseInfo info, float damage, AbstractCard c) {
        refreshTriggers(info);
        for (PTrigger effect : ptriggers) {
            damage = effect.modifyMagicNumber(info, damage);
        }
        return damage;
    }

    public float modifyRightCount(PCLUseInfo info, float damage, AbstractCard c) {
        refreshTriggers(info);
        for (PTrigger effect : ptriggers) {
            damage = effect.modifyRightCount(info, damage);
        }
        return damage;
    }

    @Override
    protected void onSamePowerApplied(AbstractPower power) {
        PSkillPower po = EUIUtils.safeCast(power, PSkillPower.class);
        if (po != null && this.ID.equals(po.ID)) {
            // The effects of identical cards will always be in the same order
            for (int i = 0; i < Math.min(ptriggers.size(), po.ptriggers.size()); i++) {
                ptriggers.get(i).stack(po.ptriggers.get(i));
            }
        }
    }

    public void atStartOfTurn() {
        super.atStartOfTurn();
        for (PTrigger effect : ptriggers) {
            effect.resetUses();
        }
        if (isTurnBased) {
            reducePower(1);
        }
    }

    public void onRemove() {
        super.onRemove();
        for (PTrigger effect : ptriggers) {
            effect.unsubscribeChildren();
        }
    }

    public void onInitialApplication() {
        super.onInitialApplication();
        for (PTrigger effect : ptriggers) {
            effect.subscribeChildren();
        }
    }

    public PSkillPower makeCopyOnTarget(AbstractCreature m, int amount) {
        return new PSkillPower(m, amount, EUIUtils.map(ptriggers, PTrigger::makeCopy));
    }

    public void refreshTriggers(PCLUseInfo info) {
        for (PTrigger effect : ptriggers) {
            effect.refresh(info, true);
        }
    }
}
