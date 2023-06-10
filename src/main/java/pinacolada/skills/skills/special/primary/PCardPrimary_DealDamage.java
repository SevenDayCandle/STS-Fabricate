package pinacolada.skills.skills.special.primary;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.tooltips.EUITooltip;
import extendedui.utilities.ColoredString;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.creature.DealDamage;
import pinacolada.actions.creature.DealDamageToAll;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.EffekseerEFK;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.VFX;
import pinacolada.interfaces.providers.PointerProvider;
import pinacolada.resources.PGR;
import pinacolada.skills.PMod;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Attack;
import pinacolada.skills.skills.*;
import pinacolada.skills.skills.base.traits.PTrait_HitCount;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.ui.editor.card.PCLCustomCardEditCardScreen;

import java.util.Arrays;

@VisibleSkill
public class PCardPrimary_DealDamage extends PCardPrimary<PField_Attack> {
    public static final PSkillData<PField_Attack> DATA = register(PCardPrimary_DealDamage.class, PField_Attack.class)

            .setExtra(1, DEFAULT_MAX);

    // Damage effects are only customizable in code and cannot be saved in fields
    protected FuncT2<Float, AbstractCreature, AbstractCreature> damageEffect;

    // Needed for effect editor initialization. PLEASE do not call this anywhere else without setting a card first
    public PCardPrimary_DealDamage() {
        super(DATA, PCLCardTarget.Single, 0);
    }

    public PCardPrimary_DealDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCardPrimary_DealDamage(PointerProvider card) {
        super(DATA, card);
    }

    public PCardPrimary_DealDamage(PointerProvider card, AbstractGameAction.AttackEffect attackEffect) {
        super(DATA, card);
        fields.attackEffect = attackEffect;
    }

    public PCLCardValueSource getAmountSource() {
        return PCLCardValueSource.Damage;
    }

    public PCLCardValueSource getExtraSource() {
        return PCLCardValueSource.HitCount;
    }

    @Override
    public ColoredString getColoredValueString(Object displayBase, Object displayAmount) {
        return new ColoredString(displayAmount,
                (sourceCard != null ?
                        sourceCard.upgradedDamage ? Settings.GREEN_TEXT_COLOR :
                                sourceCard.isDamageModified ? (amount > baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR)
                                        : Settings.CREAM_COLOR : Settings.CREAM_COLOR));
    }

    public AbstractMonster.Intent getIntent() {
        return AbstractMonster.Intent.ATTACK;
    }

    @Override
    public String getSubText() {
        int count = source != null ? getExtraFromCard() : 1;
        // We can omit the hit count if there is only one hit and the hit count is never modified
        // TODO dynamically check if a child effect overrides modifyHitCount
        String amountString = (count > 1 || hasChildType(PTrait_HitCount.class)) ? getAmountRawString() + "x" + getExtraRawString() : getAmountRawString();

        // When displayed as text, we can just write normal damage down as "damage"
        EUITooltip attackTooltip = getAttackTooltip();
        String attackString = attackTooltip == PGR.core.tooltips.normalDamage && (EUIConfiguration.disableDescrptionIcons.get() || PGR.config.expandAbbreviatedEffects.get()) ? PGR.core.strings.subjects_damage : attackTooltip.toString();

        // Use expanded text like PMove_DealDamage if verbose mode is used
        if (PGR.config.expandAbbreviatedEffects.get()) {
            if (target == PCLCardTarget.Self) {
                return TEXT.act_takeDamage(amountString);
            }
            if (target == PCLCardTarget.Single) {
                return TEXT.act_deal(amountString, attackString);
            }
            return TEXT.act_dealTo(amountString, attackString, getTargetString());
        }

        String targetShortString = target.getShortString();
        if (targetShortString != null) {
            return EUIRM.strings.numAdjNoun(amountString, targetShortString, attackString);
        }
        return EUIRM.strings.numNoun(amountString, attackString);
    }

    @Override
    public PCardPrimary_DealDamage makeCopy() {
        return (PCardPrimary_DealDamage) super.makeCopy();
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill) {
        return skill instanceof PPassiveCond ||
                skill instanceof PPassiveMod ||
                skill instanceof PDamageTrait;
    }

    public PCardPrimary_DealDamage setBonus(PMod<?> mod, int amount) {
        setChain(mod, PTrait.damage(amount));
        return this;
    }

    public PCardPrimary_DealDamage setBonus(PMod<?> mod, int amount, int... upgrade) {
        setChain(mod, PTrait.damage(amount).setUpgrade(upgrade));
        return this;
    }

    public PCardPrimary_DealDamage setBonusPercent(PMod<?> mod, int amount) {
        setChain(mod, PTrait.damageMultiplier(amount));
        return this;
    }

    public PCardPrimary_DealDamage setBonusPercent(PMod<?> mod, int amount, int... upgrade) {
        setChain(mod, PTrait.damageMultiplier(amount).setUpgrade(upgrade));
        return this;
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey) {
        this.damageEffect = (s, m) -> PCLEffects.Queue.add(VFX.eFX(effekseerKey, m.hb)).duration * 0.8f;
        return this;
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey, Color color) {
        return setDamageEffect(effekseerKey, color, 0.8f);
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey, Color color, float durationMult) {
        this.damageEffect = (s, m) -> PCLEffects.Queue.add(VFX.eFX(effekseerKey, m.hb).setColor(color)).duration * durationMult;
        return this;
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey, float durationMult) {
        this.damageEffect = (s, m) -> PCLEffects.Queue.add(VFX.eFX(effekseerKey, m.hb)).duration * durationMult;
        return this;
    }

    public PCardPrimary_DealDamage setDamageEffect(FuncT2<Float, AbstractCreature, AbstractCreature> damageEffect) {
        this.damageEffect = damageEffect;
        return this;
    }

    protected void setDamageOptions(DealDamageToAll damageAction, PCLUseInfo info) {
        if (damageEffect != null) {
            damageAction.setDamageEffect((enemy, __) -> damageEffect.invoke(info.source, enemy));
        }
        if (fields.vfxColor != null) {
            if (fields.vfxTargetColor != null) {
                damageAction.setVFXColor(fields.vfxColor, fields.vfxTargetColor);
            }
            else {
                damageAction.setVFXColor(fields.vfxColor);
            }
        }
    }

    protected void setDamageOptions(DealDamage damageAction, PCLUseInfo info) {
        if (damageEffect != null) {
            damageAction.setDamageEffect(damageEffect);
        }
        if (fields.vfxColor != null) {
            if (fields.vfxTargetColor != null) {
                damageAction.setVFXColor(fields.vfxColor, fields.vfxTargetColor);
            }
            else {
                damageAction.setVFXColor(fields.vfxColor);
            }
        }
    }

    public PCardPrimary_DealDamage setProvider(PointerProvider card) {
        setTarget(card instanceof PCLCard ? ((PCLCard) card).pclTarget : PCLCardTarget.Single);
        setSource(card);
        return this;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        PCLCustomCardEditCardScreen sc = EUIUtils.safeCast(editor.editor.screen, PCLCustomCardEditCardScreen.class);
        if (sc != null) {
            editor.registerDropdown(Arrays.asList(PCLAttackType.values())
                    , EUIUtils.arrayList(sc.getBuilder().attackType)
                    , item -> {
                        if (item.size() > 0) {
                            sc.modifyBuilder(e -> e.setAttackType(item.get(0)));
                        }
                    }
                    , item -> StringUtils.capitalize(item.toString().toLowerCase()),
                    PGR.core.strings.cedit_attackType,
                    true,
                    false, true).setTooltip(PGR.core.strings.cedit_attackType, PGR.core.strings.cetut_attackType);
        }

        super.setupEditor(editor);
    }

    @Override
    public void useImpl(PCLUseInfo info) {
        PCLCard pCard = EUIUtils.safeCast(sourceCard, PCLCard.class);
        if (pCard != null) {
            switch (target) {
                case All:
                    getActions().dealCardDamageToAll(pCard, info.source, fields.attackEffect).forEach(e -> setDamageOptions(e, info));
                case Team:
                    getActions().dealCardDamage(pCard, info.source, AbstractDungeon.player, fields.attackEffect).forEach(e -> setDamageOptions(e, info));
                case AllAlly:
                    getActions().dealCardDamageToAll(pCard, info.source, fields.attackEffect).forEach(e -> setDamageOptions(e.targetAllies(true), info));
                    break;
                case AllEnemy:
                    getActions().dealCardDamageToAll(pCard, info.source, fields.attackEffect).forEach(e -> setDamageOptions(e, info));
                    break;
                default:
                    getActions().dealCardDamage(pCard, info.source, info.target, fields.attackEffect).forEach(e -> setDamageOptions(e, info));
            }
        }
    }

    public PCardPrimary_DealDamage setVFXColor(Color vfxColor, Color vfxTargetColor) {
        fields.setVFXColor(vfxColor, vfxTargetColor);
        return this;
    }
}
