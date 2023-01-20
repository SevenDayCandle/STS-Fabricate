package pinacolada.powers;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.mod.stslib.patches.NeutralPowertypePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.utilities.ColoredString;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PTrigger;
import pinacolada.skills.skills.base.primary.PTrigger_Interactable;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PSkillPower extends PCLPower
{
    public final ArrayList<PTrigger> ptriggers = new ArrayList<>();

    public PSkillPower(AbstractCreature owner, int turns, PTrigger... effects)
    {
        this(owner, turns, Arrays.asList(effects));
    }

    public PSkillPower(AbstractCreature owner, int turns, List<PTrigger> effects)
    {
        super(owner, owner);
        this.powerStrings = new PowerStrings();

        for (PTrigger effect : effects)
        {
            this.ptriggers.add(effect.makeCopy());
            effect.power = this;
            effect.resetUses();

            if (this.powerStrings.NAME == null)
            {
                this.ID = createPowerID(effect);
                if (effect.sourceCard instanceof EditorCard)
                {
                    this.region48 = PCLRenderHelpers.generateIcon(((EditorCard) effect.sourceCard).getPortraitImageTexture());
                    this.powerStrings.NAME = effect.sourceCard.name;
                }
                else
                {
                    this.img = EUIRM.getTexture(PGR.core.createID("UnknownPower"));
                    this.powerStrings.NAME = effect.source != null ? effect.source.getName() : effect.effectID != null ? effect.effectID : "UnknownPower";
                }
            }

            if (effect instanceof PTrigger_Interactable)
            {
                triggerCondition = new PCLClickableUse(this, effect.getChild(), effect.amount <= 0 ? -1 : effect.amount, !effect.fields.not, true);
            }
        }

        setupDescription();
        if (turns > 0)
        {
            initialize(turns, NeutralPowertypePatch.NEUTRAL, true);
        }
        else
        {
            initialize(-1, NeutralPowertypePatch.NEUTRAL, false);
        }
    }

    public static String createPowerID(PSkill<?> effect)
    {
        return effect != null ? deriveID(effect.source != null ? effect.source.getID() + effect.source.getPowerEffects().indexOf(effect) : effect.effectID) : null;
    }

    @Override
    protected ColoredString getSecondaryAmount(Color c)
    {
        for (PTrigger trigger : ptriggers)
        {
            int uses = trigger.getUses();
            if (!(trigger instanceof PTrigger_Interactable) && uses >= 0)
            {
                return new ColoredString(uses, uses > 0 ? Color.GREEN : Color.GRAY, c.a);
            }
        }
        return null;
    }

    @Override
    public String getUpdatedDescription()
    {
        this.powerStrings.DESCRIPTIONS = EUIUtils.mapAsNonnull(ptriggers, PSkill::getPowerText).toArray(new String[]{});
        return EUIUtils.joinStrings(EUIUtils.SPLIT_LINE, this.powerStrings.DESCRIPTIONS);
    }

    @Override
    protected void onSamePowerApplied(AbstractPower power)
    {
        PSkillPower po = EUIUtils.safeCast(power, PSkillPower.class);
        if (po != null && this.ID.equals(po.ID))
        {
            // The effects of identical cards will always be in the same order
            for (int i = 0; i < Math.min(ptriggers.size(), po.ptriggers.size()); i++)
            {
                ptriggers.get(i).stack(po.ptriggers.get(i));
            }
        }
    }

    @Override
    public AbstractPower makeCopy()
    {
        return new PSkillPower(owner, amount, EUIUtils.map(ptriggers, PTrigger::makeCopy));
    }

    public void atStartOfTurn()
    {
        super.atStartOfTurn();
        for (PTrigger effect : ptriggers)
        {
            effect.resetUses();
        }
        if (isTurnBased)
        {
            reducePower(1);
        }
    }

    public void onInitialApplication()
    {
        super.onInitialApplication();
        for (PTrigger effect : ptriggers)
        {
            effect.subscribeChildren();
        }
    }

    public void onRemove()
    {
        super.onRemove();
        for (PTrigger effect : ptriggers)
        {
            effect.unsubscribeChildren();
        }
    }

    public PSkillPower makeCopyOnTarget(AbstractCreature m, int amount)
    {
        return new PSkillPower(m, amount, EUIUtils.map(ptriggers, PTrigger::makeCopy));
    }

    public float atDamageGive(float damage, DamageInfo.DamageType type, AbstractCard card)
    {
        for (PTrigger effect : ptriggers)
        {
            damage = effect.atDamageGive(owner, damage, type, card);
        }
        return super.atDamageGive(damage, type, card);
    }

    public float atDamageReceive(float damage, DamageInfo.DamageType type, AbstractCard card)
    {
        for (PTrigger effect : ptriggers)
        {
            damage = effect.atDamageReceive(owner, damage, type, card);
        }
        return super.atDamageReceive(damage, type, card);
    }

    public float modifyBlock(float damage, AbstractCard card)
    {
        for (PTrigger effect : ptriggers)
        {
            damage = effect.atBlockGain(owner, damage, card);
        }
        return super.modifyBlock(damage, card);
    }

    public boolean canPlayCard(AbstractCard card)
    {
        for (PTrigger effect : ptriggers)
        {
            effect.refresh(EUIUtils.safeCast(owner, AbstractMonster.class), card, true);
        }
        return super.canPlayCard(card);
    }
}
