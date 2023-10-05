package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.EUIUtils;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.DraggableHitbox;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.ui.EUICardDraggable;

public class PowerFormulaDisplay extends EUICardDraggable<AbstractCard> {
    // TODO Lock functionality to continue showing stats for a certain card
    // TODO support for non-PCL cards

    public static final float ICON_SIZE = 32f;
    public static final float OFFSET_MULT_X = 3.2f;
    public static final float OFFSET_MULT_Y = -4f;
    private final RelativeHitbox attackHb;
    private final RelativeHitbox defendHb;
    private final RelativeHitbox enemyAttackHb;
    protected final EUILabel title;
    public final PowerFormulaRow attack;
    public final PowerFormulaRow defend;
    public final PowerFormulaEnemyRow enemyAttack;
    private boolean updatedAttack;
    private boolean updatedDefend;

    public PowerFormulaDisplay() {
        super(PGR.config.damageFormulaPosition, new DraggableHitbox(screenW(0.0366f), screenH(0.425f), ICON_SIZE, ICON_SIZE, true), ICON_SIZE);
        attackHb = RelativeHitbox.fromPercentages(hb, 1, 1, OFFSET_MULT_X, -0.5f + OFFSET_MULT_Y);
        defendHb = RelativeHitbox.fromPercentages(hb, 1, 1, OFFSET_MULT_X, -0.5f + OFFSET_MULT_Y * 2);
        enemyAttackHb = RelativeHitbox.fromPercentages(hb, 1, 1, OFFSET_MULT_X, -0.5f + OFFSET_MULT_Y * 3);

        attack = new PowerFormulaRow(attackHb, PowerFormulaRow.Type.Attack);
        defend = new PowerFormulaRow(defendHb, PowerFormulaRow.Type.Defend);
        enemyAttack = new PowerFormulaEnemyRow(enemyAttackHb);
        title = new EUILabel(FontHelper.powerAmountFont, RelativeHitbox.fromPercentages(hb, 1, 1, OFFSET_MULT_X, -OFFSET_MULT_Y * 0.59f))
                .setAlignment(0.5f, 0.2f)
                .setLabel("--");
    }

    public void addAttackAffinity(PCLAffinity po, float input, float result) {
        if (input != result) {
            attack.addAffinity(po, input, result);
        }
    }

    public void addAttackGeneric(float input, float result) {
        if (input != result) {
            attack.addGeneric(input, result);
        }
    }

    public void addAttackGeneric(Texture tex, float input, float result) {
        if (input != result) {
            attack.addGeneric(tex, input, result);
        }
    }

    public void addAttackPower(AbstractPower po, float input, float result) {
        if (input != result) {
            attack.addPower(po, input, result);
        }
    }

    public void addDefendAffinity(PCLAffinity po, float input, float result) {
        if (input != result) {
            defend.addAffinity(po, input, result);
        }
    }

    public void addDefendGeneric(float input, float result) {
        if (input != result) {
            defend.addGeneric(input, result);
        }
    }

    public void addDefendGeneric(Texture tex, float input, float result) {
        if (input != result) {
            defend.addGeneric(tex, input, result);
        }
    }

    public void addDefendPower(AbstractPower po, float input, float result) {
        if (input != result) {
            defend.addPower(po, input, result);
        }
    }

    public void addEnemyAttackPower(AbstractPower po, float input, float result) {
        if (input != result) {
            enemyAttack.addPower(po, input, result);
        }
    }

    protected float moveHitbox(EUIHitbox hitbox, float offset) {
        hitbox.setOffsetY(offset);
        return offset + hb.height * OFFSET_MULT_Y;
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        title.renderImpl(sb);
        if (updatedAttack) {
            attack.renderImpl(sb);
        }
        if (updatedDefend) {
            defend.renderImpl(sb);
        }
        if (getLastTarget() != null && enemyAttack.shouldRender) {
            enemyAttack.renderImpl(sb);
        }
    }

    public void setAttackResult(float input, float result) {
        attack.setResult(input, result);
    }

    public void setDefendResult(float input, float result) {
        defend.setResult(input, result);
    }

    @Override
    public void updateImpl(AbstractCard card, AbstractCard originalCard, AbstractCreature target, AbstractCreature originalTarget, boolean draggingCard, boolean shouldUpdateForCard, boolean shouldUpdateForTarget) {
        PCLCard pCard = EUIUtils.safeCast(card, PCLCard.class);
        if (shouldUpdateForCard || shouldUpdateForTarget) {
            if (pCard != null) {
                pCard.formulaDisplay = this;
                title.setLabel(target != null ? card.name + " >> " + target.name : card.name);

                float curOff = hb.height * -0.5f;
                if (pCard.onAttackEffect != null) {
                    curOff = moveHitbox(attackHb, curOff);
                    updatedAttack = true;
                }
                else {
                    updatedAttack = false;
                }
                if (pCard.onBlockEffect != null) {
                    curOff = moveHitbox(defendHb, curOff);
                    updatedDefend = true;
                }
                else {
                    updatedDefend = false;
                }
                curOff = moveHitbox(enemyAttackHb, curOff);
            }
            else {
                title.setLabel("--");
                updatedAttack = false;
                updatedDefend = false;
            }
        }
        title.updateImpl();
        if (updatedAttack) {
            attack.updateImpl(card, target, draggingCard, shouldUpdateForCard, shouldUpdateForTarget);
        }
        if (updatedDefend) {
            defend.updateImpl(card, target, draggingCard, shouldUpdateForCard, shouldUpdateForTarget);
        }
        if (target != null) {
            enemyAttack.updateImpl(card, target, draggingCard, shouldUpdateForCard, shouldUpdateForTarget);
        }
    }

}
