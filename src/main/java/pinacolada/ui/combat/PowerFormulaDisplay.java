package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.PercentageRelativeHitbox;
import pinacolada.cards.base.PCLAffinity;
import pinacolada.cards.base.PCLCard;
import pinacolada.resources.PGR;
import pinacolada.ui.EUICardDraggable;

public class PowerFormulaDisplay extends EUICardDraggable<AbstractCard>
{

    // TODO Lock functionality to continue showing stats for a certain card
    // TODO support for non-PCL cards

    public static final float ICON_SIZE = 32f;
    public static final float OFFSET1 = -4.5f;
    public static final float OFFSET2 = -8.5f;
    public static final float OFFSET3 = -12.5f;
    public final PowerFormulaRow attack;
    public final PowerFormulaRow defend;
    public final PowerFormulaEnemyRow enemyAttack;
    protected final EUILabel title;
    private final PercentageRelativeHitbox attackHb;
    private final PercentageRelativeHitbox defendHb;
    private final PercentageRelativeHitbox enemyAttackHb;

    public PowerFormulaDisplay()
    {
        super(PGR.core.config.damageFormulaPosition, new DraggableHitbox(screenW(0.0366f), screenH(0.425f), ICON_SIZE, ICON_SIZE, true), ICON_SIZE);
        attackHb = new PercentageRelativeHitbox(hb, 1, 1, 0.3f, OFFSET1);
        defendHb = new PercentageRelativeHitbox(hb, 1, 1, 0.3f, OFFSET2);
        enemyAttackHb = new PercentageRelativeHitbox(hb, 1, 1, 0.3f, OFFSET3);

        attack = new PowerFormulaRow(attackHb, PowerFormulaRow.Type.Attack);
        defend = new PowerFormulaRow(defendHb, PowerFormulaRow.Type.Defend);
        enemyAttack = new PowerFormulaEnemyRow(enemyAttackHb);
        title = new EUILabel(FontHelper.powerAmountFont, new PercentageRelativeHitbox(hb, 1, 1, 1f, -2f))
                .setAlignment(0.5f, 0.2f)
                .setLabel("--");
    }

    public void addAttackAffinity(PCLAffinity po, float input, float result)
    {
        if (input != result)
        {
            attack.addAffinity(po, input, result);
        }
    }

    public void addAttackPower(AbstractPower po, float input, float result)
    {
        if (input != result)
        {
            attack.addPower(po, input, result);
        }
    }

    public void addDefendAffinity(PCLAffinity po, float input, float result)
    {
        if (input != result)
        {
            defend.addAffinity(po, input, result);
        }
    }

    public void addDefendPower(AbstractPower po, float input, float result)
    {
        if (input != result)
        {
            defend.addPower(po, input, result);
        }
    }

    public void addEnemyAttackPower(AbstractPower po, float input, float result)
    {
        if (input != result)
        {
            enemyAttack.addPower(po, input, result);
        }
    }

    public void setAttackResult(float input, float result)
    {
        attack.setResult(input, result);
    }

    public void setDefendResult(float input, float result)
    {
        defend.setResult(input, result);
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        super.renderImpl(sb);
        title.renderImpl(sb);
        if (getLastCard() != null) {
            if (getLastCard().baseDamage > 0) {
                attack.renderImpl(sb);
            }
            if (getLastCard().baseBlock > 0) {
                defend.renderImpl(sb);
            }
        }
        if (getLastTarget() != null && enemyAttack.shouldRender) {
            enemyAttack.renderImpl(sb);
        }
    }

    @Override
    public void updateImpl(AbstractCard card, AbstractCreature target, boolean draggingCard, boolean shouldUpdateForCard, boolean shouldUpdateForTarget)
    {
        if (shouldUpdateForCard || shouldUpdateForTarget)
        {
            if (card instanceof PCLCard)
            {
                ((PCLCard) card).formulaDisplay = this;
                title.setLabel(target != null ? card.name + " >> " + target.name : card.name);
                defendHb.setOffset(0.3f, card.baseDamage > 0 && card.baseBlock > 0 ? OFFSET2 : OFFSET1);
                enemyAttackHb.setOffset(0.3f, (card.baseDamage > 0 && card.baseBlock > 0) ? OFFSET3 : (card.baseDamage > 0 || card.baseBlock > 0) ? OFFSET2 : OFFSET1);
            }
            else
            {
                title.setLabel("--");
            }
        }
        title.updateImpl();
        if (card != null)
        {
            if (card.baseDamage > 0)
            {
                attack.updateImpl(card, target, draggingCard, shouldUpdateForCard, shouldUpdateForTarget);
            }
            if (card.baseBlock > 0)
            {
                defend.updateImpl(card, target, draggingCard, shouldUpdateForCard, shouldUpdateForTarget);
            }
        }
        if (target != null) {
            enemyAttack.updateImpl(card, target, draggingCard, shouldUpdateForCard, shouldUpdateForTarget);
        }

    }

}
