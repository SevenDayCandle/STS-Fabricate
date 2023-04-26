package pinacolada.ui.combat;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import extendedui.EUIRM;
import extendedui.ui.hitboxes.EUIHitbox;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.monsters.PCLIntentInfo;
import pinacolada.utilities.PCLRenderHelpers;

public class PowerFormulaEnemyRow extends PowerFormulaRow {
    public boolean shouldRender;

    public PowerFormulaEnemyRow(EUIHitbox hb) {
        super(hb, Type.EnemyAttack);
    }

    @Override
    public void renderImpl(SpriteBatch sb) {
        super.renderImpl(sb);
        if (card != null && (card.baseDamage > 0 || card.baseBlock > 0)) {
            PCLRenderHelpers.drawCentered(sb, Color.WHITE, EUIRM.images.border.texture(), hb.cX + hb.width * getOffsetCx(powers.size() / 2), hb.cY, ICON_SIZE, 16f, 1f, 0);
        }
    }

    public void updateImpl(AbstractCard card, AbstractCreature target, boolean draggingCard, boolean shouldUpdateForCard, boolean shouldUpdateForTarget) {
        super.updateImpl();
        if (shouldUpdateForCard || shouldUpdateForTarget) {
            powers.clear();
            if (target instanceof AbstractMonster && !(target instanceof PCLCardAlly)) {
                PCLIntentInfo intent = PCLIntentInfo.get((AbstractMonster) target);
                int input = intent.getBaseDamage(false);
                int fd = intent.getFinalDamage();
                icon = intent.getIntentImage();
                initial.setLabel(input);
                result.setColor(fd > input ? Settings.GREEN_TEXT_COLOR : fd < input ? Settings.RED_TEXT_COLOR : Color.WHITE).setLabel(fd);
                shouldRender = input > 0;
            }
        }
        else {
            for (PowerFormulaItem item : powers) {
                item.updateImpl();
            }
        }
        initial.updateImpl();
        result.updateImpl();
    }
}
