package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.powers.AbstractPower;
import extendedui.ui.EUIHoverable;
import extendedui.ui.controls.EUILabel;
import extendedui.ui.hitboxes.EUIHitbox;
import extendedui.ui.hitboxes.RelativeHitbox;
import pinacolada.cards.base.PCLCard;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.interfaces.markers.MultiplicativePower;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.powers.PCLPowerHelper;
import pinacolada.resources.PGR;
import pinacolada.utilities.PCLRenderHelpers;

import java.util.ArrayList;

public class PowerFormulaRow extends EUIHoverable
{
    public enum Type {
        Attack,
        Defend,
        EnemyAttack
    }

    public static final float ICON_SIZE = 32f;
    public final Type type;
    protected final ArrayList<PowerFormulaItem> powers = new ArrayList<>();
    protected AbstractCard card;
    protected RelativeHitbox resultHb;
    protected EUILabel initial;
    protected EUILabel result;
    protected Texture icon;

    public PowerFormulaRow(EUIHitbox hb, Type type)
    {
        super(hb);
        this.type = type;
        this.resultHb = RelativeHitbox.fromPercentages(hb, 1, 1, getOffsetCx(0), -0.7f);
        this.initial = new EUILabel(FontHelper.powerAmountFont, RelativeHitbox.fromPercentages(hb, 1, 1, 0, -0.7f))
                .setSmartText(false);
        this.result = new EUILabel(FontHelper.powerAmountFont, resultHb)
                .setSmartText(false);
    }

    protected void addAffinity(PCLAffinity af, float input, float result)
    {
        powers.add(new PowerFormulaItem(RelativeHitbox.fromPercentages(hb, 1, 1, getOffsetCx(powers.size()), 1), true, af.getIcon(), result).setMultiplier(result / input));
        resultHb.setOffset(resultHb.width * getOffsetCx(powers.size() + 1),resultHb.height * -0.5f);
    }

    protected void addPower(AbstractPower po, float input, float result)
    {
        RelativeHitbox hitbox = RelativeHitbox.fromPercentages(hb, 1, 1, getOffsetCx(powers.size()), 1);
        PowerFormulaItem item;
        if (po.region48 != null)
        {
            item = new PowerFormulaItem(hitbox, po.owner instanceof AbstractPlayer, po.region48, result);
        }
        else
        {
            item = new PowerFormulaItem(hitbox, po.owner instanceof AbstractPlayer, po.img, result);
        }


        PCLPowerHelper helper = PCLPowerHelper.get(po.ID);
        if (helper != null) {
            if (helper.isPercentageBonus)
            {
                item.setMultiplier(result / input);
            }
            else
            {
                item.setAddition(result - input);
            }
        }
        else {
            // Assume that powers that have % relate to power multipliers
            // Only do string searching if absolutely necessary.
            if (po instanceof MultiplicativePower || po.description.contains("%"))
            {
                item.setMultiplier(result / input);
            }
            else
            {
                item.setAddition(result - input);
            }
        }
        powers.add(item);
        resultHb.setOffset(getOffsetCx(powers.size()),-0.5f);
    }

    protected void addSummon(PCLCardAlly ally, int input, int result)
    {
        powers.add(new PowerFormulaItem(RelativeHitbox.fromPercentages(hb, 1, 1, getOffsetCx(powers.size()), 1), true, ally.card.getTypeIcon(), result).setAddition(result - input));
        resultHb.setOffset(getOffsetCx(powers.size() + 1),-0.5f);
    }

    protected void setResult(float base, float amount) {
        result.setColor(amount > base ? Color.GREEN : amount < base ? Color.RED : Color.WHITE)
                .setLabel(PCLRenderHelpers.decimalFormat(amount));
    }

    protected float getOffsetCx(int size)
    {
        return (size + 1.2f) * 2.5f;
    }

    @Override
    public void renderImpl(SpriteBatch sb)
    {
        if (icon != null)
        {
            PCLRenderHelpers.drawCentered(sb, Color.WHITE, icon, hb.x, hb.cY, ICON_SIZE, ICON_SIZE, 1f, 0);
            initial.renderImpl(sb);
            FontHelper.renderFontCentered(sb, FontHelper.powerAmountFont, ">>", resultHb.cX - resultHb.width * 1.1f, hb.cY - hb.height * 0.25f, Color.WHITE);
            result.renderImpl(sb);
        }
        for (PowerFormulaItem power : powers)
        {
            power.renderImpl(sb);
        }
    }

    public void updateImpl(AbstractCard card, AbstractCreature target, boolean draggingCard, boolean shouldUpdateForCard, boolean shouldUpdateForTarget)
    {
        super.updateImpl();
        if (shouldUpdateForCard || shouldUpdateForTarget)
        {
            powers.clear();
            if (card != null)
            {
                switch (type) {
                    case Attack:
                        icon = (card instanceof PCLCard ? ((PCLCard) card).attackType.getTooltip().icon : PGR.core.tooltips.normalDamage.icon).getTexture();
                        int damage = card.baseDamage;
                        initial.setLabel(damage > 0 ?
                                damage : "");
                        break;
                    case Defend:
                        icon = PGR.core.tooltips.block.icon.getTexture();
                        int block = card.baseBlock;
                        initial.setLabel(block > 0 ?
                                block : "");
                        break;
                }
            }
        }
        else
        {
            for (PowerFormulaItem item : powers)
            {
                item.updateImpl();
            }
        }
        this.card = card;
        initial.updateImpl();
        result.updateImpl();
    }
}
