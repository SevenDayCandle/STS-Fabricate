package pinacolada.skills.skills.special.primary;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.configuration.EUIConfiguration;
import extendedui.interfaces.delegates.FuncT2;
import extendedui.ui.tooltips.EUITooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.actions.creature.DealDamage;
import pinacolada.actions.creature.DealDamageToAll;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAttackType;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.EffekseerEFK;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLCreature;
import pinacolada.resources.PGR;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField_Attack;
import pinacolada.skills.skills.PCardPrimary;
import pinacolada.skills.skills.PDamageTrait;
import pinacolada.skills.skills.base.traits.PTrait_HitCount;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.ui.editor.PCLCustomEffectPage;
import pinacolada.ui.editor.card.PCLCustomCardEditScreen;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.Arrays;

@VisibleSkill
public class PCardPrimary_DealDamage extends PCardPrimary<PField_Attack> {
    public static final PSkillData<PField_Attack> DATA = register(PCardPrimary_DealDamage.class, PField_Attack.class)
            .setExtra(0, DEFAULT_MAX);

    // Damage effects are only customizable in code and cannot be saved in fields
    private FuncT2<Float, AbstractCreature, AbstractCreature> damageEffect;

    // Needed for effect editor initialization. PLEASE do not call this anywhere else without setting a card first
    public PCardPrimary_DealDamage() {
        super(DATA, PCLCardTarget.Single, 0);
    }

    public PCardPrimary_DealDamage(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCardPrimary_DealDamage(AbstractCard card) {
        super(DATA, card);
    }

    public PCardPrimary_DealDamage(AbstractCard card, AbstractGameAction.AttackEffect attackEffect) {
        super(DATA, card);
        fields.attackEffect = attackEffect;
    }

    public PCLCardValueSource getAmountSource() {
        return PCLCardValueSource.Damage;
    }

    @Override
    public Color getColoredAmount(int displayAmount) {
        return (source instanceof AbstractCard ?
                        ((AbstractCard) source).upgradedDamage ? Settings.GREEN_TEXT_COLOR :
                                ((AbstractCard) source).isDamageModified ? (amount > baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR)
                                        : Settings.CREAM_COLOR : Settings.CREAM_COLOR);
    }

    public PCLCardValueSource getExtraSource() {
        return PCLCardValueSource.HitCount;
    }

    public AbstractMonster.Intent getIntent() {
        return AbstractMonster.Intent.ATTACK;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_deal(TEXT.subjects_x, PGR.core.strings.subjects_damage);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        int count = source != null ? getExtraFromCard() : 1;
        // We can omit the hit count if there is only one hit and the hit count is never modified
        String amountString = (count != 1 || hasChildType(PTrait_HitCount.class)) ? getAmountRawString() + "x" + getExtraRawString() : getAmountRawString();

        // When displayed as text, we can just write normal damage down as "damage"
        EUITooltip attackTooltip = getAttackTooltip();
        String attackString = attackTooltip == PGR.core.tooltips.normalDamage && (!EUIConfiguration.enableDescriptionIcons.get() || isVerbose()) ? PGR.core.strings.subjects_damage : attackTooltip.toString();

        // Use expanded text like PMove_DealDamage if verbose mode is used
        if (isVerbose()) {
            if (target == PCLCardTarget.Self) {
                return TEXT.act_takeDamage(amountString);
            }
            if (target == PCLCardTarget.Single) {
                return TEXT.act_deal(amountString, attackString);
            }
            return TEXT.act_dealTo(amountString, attackString, getTargetStringPerspective(perspective));
        }

        String targetShortString = target.getShortString();
        if (targetShortString != null) {
            if (scope > 1) {
                targetShortString = targetShortString + "x" + scope;
            }
            return EUIRM.strings.numAdjNoun(amountString, targetShortString, attackString);
        }
        return EUIRM.strings.numNoun(amountString, attackString);
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill, PCLCustomEffectPage editor) {
        return super.isSkillAllowed(skill, editor) ||
                skill instanceof PDamageTrait;
    }

    @Override
    public PCardPrimary_DealDamage makeCopy() {
        return (PCardPrimary_DealDamage) super.makeCopy();
    }

    @Override
    public float renderIntentIcon(SpriteBatch sb, PCLCardAlly ally, float startY, boolean isPreview) {
        boolean dim = ally.shouldDim();
        TextureRegion icon = getAttackTooltip().icon;
        PCLRenderHelpers.drawGrayscaleIf(sb, s -> PCLRenderHelpers.drawCentered(sb, dim ? PCLCreature.TAKEN_TURN_COLOR : Color.WHITE, icon, ally.intentHb.cX - PCLCardAlly.INTENT_OFFSET, startY, icon.getRegionWidth(), icon.getRegionHeight(), 0.85f, 0f), dim);
        FontHelper.renderFontLeftTopAligned(sb,
                FontHelper.topPanelInfoFont, extra > 1 ? amount + "x" + extra : String.valueOf(amount), ally.intentHb.cX + PCLCardAlly.INTENT_OFFSET, startY,
                isPreview ? Settings.GREEN_TEXT_COLOR : dim ? PCLCreature.TAKEN_TURN_NUMBER_COLOR : Settings.CREAM_COLOR);
        return startY + icon.getRegionHeight() + Settings.scale * 10f;
    }

    public PCardPrimary_DealDamage setBonus(PCond<?> cond, int amount) {
        setChain(cond, PTrait.damage(amount));
        return this;
    }

    public PCardPrimary_DealDamage setBonus(PCond<?> cond, int amount, int... upgrade) {
        setChain(cond, PTrait.damage(amount).setUpgrade(upgrade));
        return this;
    }

    public PCardPrimary_DealDamage setBonus(PMod<?> mod, int amount) {
        setChain(mod, PTrait.damage(amount));
        return this;
    }

    public PCardPrimary_DealDamage setBonus(PMod<?> mod, int amount, int... upgrade) {
        setChain(mod, PTrait.damage(amount).setUpgrade(upgrade));
        return this;
    }

    public PCardPrimary_DealDamage setBonusPercent(PCond<?> cond, int amount) {
        setChain(cond, PTrait.damageMultiplier(amount));
        return this;
    }

    public PCardPrimary_DealDamage setBonusPercent(PCond<?> cond, int amount, int... upgrade) {
        setChain(cond, PTrait.damageMultiplier(amount).setUpgrade(upgrade));
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
        return setDamageEffect(effekseerKey, Settings.FAST_MODE ? 0.4f : 0.8f);
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey, Color color) {
        return setDamageEffect(effekseerKey, color, Settings.FAST_MODE ? 0.4f : 0.8f);
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey, Color color, float durationMult) {
        this.damageEffect = (s, m) -> PCLEffects.Queue.add(EffekseerEFK.efk(effekseerKey, m.hb).setColor(color)).duration * durationMult;
        return this;
    }

    public PCardPrimary_DealDamage setDamageEffect(EffekseerEFK effekseerKey, float durationMult) {
        this.damageEffect = (s, m) -> PCLEffects.Queue.add(EffekseerEFK.efk(effekseerKey, m.hb)).duration * durationMult;
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

    public PCardPrimary_DealDamage setProvider(AbstractCard card) {
        setTarget(card instanceof EditorCard ? ((EditorCard) card).pclTarget() : PCLCardTarget.forVanilla(card.target));
        setSource(card);
        return this;
    }

    public PCardPrimary_DealDamage setVFXColor(Color vfxColor, Color vfxTargetColor) {
        fields.setVFXColor(vfxColor, vfxTargetColor);
        return this;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        PCLCustomCardEditScreen sc = EUIUtils.safeCast(editor.editor.screen, PCLCustomCardEditScreen.class);
        if (sc != null) {
            editor.registerDropdown(Arrays.asList(PCLAttackType.values())
                    , EUIUtils.arrayList(sc.getBuilder().attackType)
                    , item -> {
                        if (!item.isEmpty()) {
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
    public boolean shouldOverrideTarget() {
        return true;
    }

    @Override
    public void useImpl(PCLUseInfo info, PCLActions order) {
        PCLCard pCard = EUIUtils.safeCast(source, PCLCard.class);
        if (fields.effekseer != null && damageEffect == null) {
            if (fields.vfxColor != null) {
                setDamageEffect(fields.effekseer, fields.vfxColor);
            }
            else {
                setDamageEffect(fields.effekseer);
            }
        }
        if (pCard != null) {
            if (target.targetsMulti()) {
                setDamageOptions(order.dealCardDamageToAll(pCard, info.source, fields.attackEffect), info);
            }
            else if (target.targetsRandom() && scope > 1) {
                for (AbstractCreature cr : getTargetListAsNew(info)) {
                    setDamageOptions(order.dealCardDamage(pCard, info.source, cr, fields.attackEffect), info);
                }
            }
            else {
                setDamageOptions(order.dealCardDamage(pCard, info.source, info.target, fields.attackEffect), info);
            }
        }
    }
}
