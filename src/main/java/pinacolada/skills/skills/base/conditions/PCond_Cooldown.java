package pinacolada.skills.skills.base.conditions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import extendedui.EUI;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.utilities.EUIColors;
import pinacolada.actions.PCLAction;
import pinacolada.actions.PCLActions;
import pinacolada.actions.special.CooldownProgressAction;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.providers.CooldownProvider;
import pinacolada.interfaces.subscribers.OnCooldownTriggeredSubscriber;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLCreature;
import pinacolada.resources.PGR;
import pinacolada.skills.PCond;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PActiveCond;
import pinacolada.skills.skills.PBranchCond;
import pinacolada.utilities.PCLRenderHelpers;

// TODO move to primary
@VisibleSkill
public class PCond_Cooldown extends PActiveCond<PField_Empty> implements CooldownProvider, OnCooldownTriggeredSubscriber {
    public static final PSkillData<PField_Empty> DATA = register(PCond_Cooldown.class, PField_Empty.class)
            .noTarget();
    private float flashTimer;
    private int cooldown;

    public PCond_Cooldown(PSkillSaveData content) {
        super(DATA, content);
        cooldown = amount;
    }

    public PCond_Cooldown() {
        super(DATA, PCLCardTarget.None, 1);
        cooldown = amount;
    }

    public PCond_Cooldown(int amount) {
        super(DATA, PCLCardTarget.None, amount);
        cooldown = amount;
    }

    // Must return true when using or cooldown will not progress in a multicond
    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        return isUsing || getCooldown() <= 0;
    }

    @Override
    public int getBaseCooldown() {
        return amount;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.act_trigger(PGR.core.tooltips.cooldown.title)) : EUIRM.strings.generic2(PGR.core.tooltips.cooldown.title, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_trigger(PGR.core.tooltips.cooldown.title), perspective);
        }
        return EUIRM.strings.generic2(PGR.core.tooltips.cooldown.title, getXRawString(requestor));
    }

    @Override
    public String getText(PCLCardTarget perspective, Object requestor, boolean addPeriod) {
        String condString = isWhenClause() ? getCapitalSubText(perspective, requestor, addPeriod) : getConditionRawString(perspective, requestor, addPeriod);
        return condString + (childEffect != null ? ((childEffect instanceof PCond && !(childEffect instanceof PBranchCond) ? EFFECT_SEPARATOR : ": ") + childEffect.getText(perspective, requestor, addPeriod)) : "");
    }

    @Override
    public Color getXColor() {
        return CombatManager.inBattle() ? getCooldownColor() : Settings.CREAM_COLOR;
    }

    @Override
    public int getXValue() {
        return cooldown;
    }

    @Override
    public boolean isDisplayingUpgrade() {
        return displayUpgrades && getUpgrade() != 0;
    }

    @Override
    public PCond_Cooldown makeCopy() {
        PCond_Cooldown copy = (PCond_Cooldown) super.makeCopy();
        copy.cooldown = cooldown = amount;
        return copy;
    }

    @Override
    public PCond_Cooldown onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        cooldown = amount;
        return this;
    }

    @Override
    public boolean onCooldownTriggered(CooldownProvider cooldown, AbstractCreature s, AbstractCreature m) {
        if (cooldown.canActivate()) {
            useFromTrigger(generateInfo(m).setData(cooldown));
            flashTimer = 1;
        }
        return true;
    }

    @Override
    public PCond_Cooldown onRemoveFromCard(AbstractCard card) {
        super.onRemoveFromCard(card);
        return this;
    }

    @Override
    public float renderIntentIcon(SpriteBatch sb, PCLCardAlly ally, float startY, boolean isPreview) {
        boolean canActivate = canActivate();
        boolean dim = ally.shouldDim();
        Color iconColor = dim ? PCLCreature.TAKEN_TURN_COLOR : Color.WHITE;
        Color textColor = canActivate ? (dim ? PCLCardAlly.FADE_COOLDOWN_COLOR : Settings.GREEN_TEXT_COLOR) :
                (Settings.CREAM_COLOR);
        PCLRenderHelpers.drawGrayscaleIf(sb,
                s -> PCLRenderHelpers.drawCentered(s, iconColor, PGR.core.tooltips.cooldown.icon, ally.intentHb.cX - PCLCardAlly.INTENT_OFFSET, startY, PGR.core.tooltips.cooldown.icon.getRegionWidth(), PGR.core.tooltips.cooldown.icon.getRegionHeight(), 0.65f, 0f),
                dim);
        FontHelper.renderFontLeftTopAligned(sb, FontHelper.topPanelInfoFont, Integer.toString(getCooldown()), ally.intentHb.cX + PCLCardAlly.INTENT_OFFSET, startY, textColor);
        if (flashTimer > 0) {
            Color flashColor = EUIColors.white(flashTimer);
            PCLRenderHelpers.drawGlowing(sb, s -> PCLRenderHelpers.drawCentered(s, iconColor, PGR.core.tooltips.cooldown.icon, ally.intentHb.cX - PCLCardAlly.INTENT_OFFSET, startY, PGR.core.tooltips.cooldown.icon.getRegionWidth(), PGR.core.tooltips.cooldown.icon.getRegionHeight(), 2f - flashTimer, 0f));
            flashTimer -= EUI.delta();
        }

        return startY + PGR.core.tooltips.cooldown.icon.getRegionHeight() + Settings.scale * 10f;
    }

    @Override
    public void setCooldown(int value) {
        this.cooldown = value;
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (childEffect != null) {
            useImpl(info, order, (i) -> childEffect.use(info, PCLActions.bottom), (i) -> {});
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, boolean shouldPay) {
        if (shouldPay) {
            use(info, order);
        }
        else if (childEffect != null) {
            childEffect.use(info, order);
        }
    }

    @Override
    protected PCLAction<?> useImpl(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> onComplete, ActionT1<PCLUseInfo> onFail) {
        return order.add(new CooldownProgressAction(this, info.source, info.target, 1))
                .addCallback(result -> {
                    if (result) {
                        onComplete.invoke(info);
                    }
                    else {
                        onFail.invoke(info);
                    }
                });
    }
}
