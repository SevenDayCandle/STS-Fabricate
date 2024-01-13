package pinacolada.skills.skills.special.primary;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.markers.EditorCard;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLCreature;
import pinacolada.resources.PGR;
import pinacolada.skills.*;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PBlockTrait;
import pinacolada.skills.skills.PCardPrimary;
import pinacolada.ui.editor.PCLCustomEffectPage;
import pinacolada.utilities.PCLRenderHelpers;

@VisibleSkill
public class PCardPrimary_GainBlock extends PCardPrimary<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PCardPrimary_GainBlock.class, PField_Empty.class)
            .setExtra(0, DEFAULT_MAX);

    public PCardPrimary_GainBlock() {
        super(DATA, PCLCardTarget.Self, 0);
    }

    public PCardPrimary_GainBlock(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCardPrimary_GainBlock(EditorCard card) {
        super(DATA, card);
    }

    public PCLCardValueSource getAmountSource() {
        return PCLCardValueSource.Block;
    }

    @Override
    public Color getColoredAmount(int displayAmount) {
        return (source instanceof AbstractCard ?
                        ((AbstractCard) source).upgradedBlock ? Settings.GREEN_TEXT_COLOR :
                                ((AbstractCard) source).isBlockModified ? (amount > baseAmount ? Settings.GREEN_TEXT_COLOR : Settings.RED_TEXT_COLOR)
                                        : Settings.CREAM_COLOR : Settings.CREAM_COLOR);
    }

    public PCLCardValueSource getExtraSource() {
        return PCLCardValueSource.RightCount;
    }

    public AbstractMonster.Intent getIntent() {
        return AbstractMonster.Intent.DEFEND;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.block);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        int count = source != null ? getExtraFromCard() : 1;
        String amountString = count != 1 ? getAmountRawString() + "x" + getExtraRawString() : getAmountRawString();

        // Use expanded text like PMove_GainBlock if verbose mode is used
        if (isVerbose()) {
            if (isSelfOnlyTarget(perspective)) {
                return amount < 0 ? TEXT.act_loseAmount(amountString, PGR.core.tooltips.block) : TEXT.act_gainAmount(amountString, PGR.core.tooltips.block);
            }
            return TEXT.act_giveTargetAmount(getTargetString(target), amountString, PGR.core.tooltips.block); // Ignore perspective because this comes from the card
        }

        String targetShortString = target.getShortString();
        if (targetShortString != null) {
            if (scope > 1) {
                targetShortString = targetShortString + "x" + scope;
            }
            return EUIRM.strings.numAdjNoun(amountString, targetShortString, PGR.core.tooltips.block);
        }
        return EUIRM.strings.numNoun(amountString, PGR.core.tooltips.block);
    }

    @Override
    public boolean isSkillAllowed(PSkill<?> skill, PCLCustomEffectPage editor) {
        return super.isSkillAllowed(skill, editor) ||
                skill instanceof PBlockTrait;
    }

    @Override
    public PCardPrimary_GainBlock makeCopy() {
        return (PCardPrimary_GainBlock) super.makeCopy();
    }

    @Override
    public float renderIntentIcon(SpriteBatch sb, PCLCardAlly ally, float startY, boolean isPreview) {
        boolean dim = ally.shouldDim();
        TextureRegion icon = PGR.core.tooltips.block.icon;
        PCLRenderHelpers.drawGrayscaleIf(sb, s -> PCLRenderHelpers.drawCentered(sb, dim ? PCLCreature.TAKEN_TURN_COLOR : Color.WHITE, icon, ally.intentHb.cX - PCLCardAlly.INTENT_OFFSET, startY, icon.getRegionWidth(), icon.getRegionHeight(), 0.85f, 0f), dim);
        FontHelper.renderFontLeftTopAligned(sb,
                FontHelper.topPanelInfoFont, extra > 1 ? amount + "x" + extra : String.valueOf(amount), ally.intentHb.cX + PCLCardAlly.INTENT_OFFSET, startY,
                isPreview ? Settings.GREEN_TEXT_COLOR : dim ? PCLCreature.TAKEN_TURN_NUMBER_COLOR : Settings.CREAM_COLOR);
        return startY + icon.getRegionHeight() + Settings.scale * 10f;
    }

    public PCardPrimary_GainBlock setBonus(PCond<?> mod, int amount) {
        setChain(mod, PTrait.block(amount));
        return this;
    }

    public PCardPrimary_GainBlock setBonus(PCond<?> mod, int amount, int... upgrade) {
        setChain(mod, PTrait.block(amount).setUpgrade(upgrade));
        return this;
    }

    public PCardPrimary_GainBlock setBonus(PMod<?> mod, int amount) {
        setChain(mod, PTrait.block(amount));
        return this;
    }

    public PCardPrimary_GainBlock setBonus(PMod<?> mod, int amount, int... upgrade) {
        setChain(mod, PTrait.block(amount).setUpgrade(upgrade));
        return this;
    }

    public PCardPrimary_GainBlock setBonusPercent(PCond<?> mod, int amount) {
        setChain(mod, PTrait.blockMultiplier(amount));
        return this;
    }

    public PCardPrimary_GainBlock setBonusPercent(PCond<?> mod, int amount, int... upgrade) {
        setChain(mod, PTrait.blockMultiplier(amount).setUpgrade(upgrade));
        return this;
    }

    public PCardPrimary_GainBlock setBonusPercent(PMod<?> mod, int amount) {
        setChain(mod, PTrait.blockMultiplier(amount));
        return this;
    }

    public PCardPrimary_GainBlock setBonusPercent(PMod<?> mod, int amount, int... upgrade) {
        setChain(mod, PTrait.blockMultiplier(amount).setUpgrade(upgrade));
        return this;
    }

    public PCardPrimary_GainBlock setProvider(EditorCard card) {
        setSource(card);
        return this;
    }

    @Override
    public void useImpl(PCLUseInfo info, PCLActions order) {
        for (AbstractCreature c : getTargetListAsNew(info)) {
            // Extra has the value of right count
            for (int i = 0; i < extra; i++) {
                order.gainBlock(c, amount);
            }
        }
    }
}
