package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.stances.NeutralStance;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Stance;
import pinacolada.stances.PCLStanceHelper;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMove_EnterStance extends PMove<PField_Stance> {
    public static final PSkillData<PField_Stance> DATA = register(PMove_EnterStance.class, PField_Stance.class, 1, 1)
            .noTarget();

    public PMove_EnterStance() {
        super(DATA);
    }

    public PMove_EnterStance(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_EnterStance(PCLStanceHelper... stance) {
        super(DATA);
        fields.setStance(stance);
    }

    public void chooseStance(PCLUseInfo info, PCLActions order, List<PCLStanceHelper> choices) {
        if (fields.random) {
            order.changeStance(GameUtilities.getRandomElement(choices));
            return;
        }
        chooseEffect(info, order, EUIUtils.map(choices, PMove::enterStance));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_enterStance(TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (fields.stances.isEmpty()) {
            return TEXT.act_exitStance();
        }
        if (extra > 0) {
            return fields.random ? TEXT.subjects_randomly(TEXT.act_enterStance(TEXT.subjects_anyX(PGR.core.tooltips.stance))) : TEXT.act_enterStance(TEXT.subjects_anyX(PGR.core.tooltips.stance));
        }
        return fields.random ? TEXT.subjects_randomly(TEXT.act_enterStance(fields.getStanceString())) : TEXT.act_enterStance(fields.getStanceString());
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (fields.stances.isEmpty()) {
            order.changeStance(NeutralStance.STANCE_ID);
        }
        else if (extra > 0) {
            chooseStance(info, order, PCLStanceHelper.inGameValues(AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS));
        }
        else if (fields.stances.size() == 1) {
            order.changeStance(fields.stances.get(0));
        }
        else {
            chooseStance(info, order, fields.stances);
        }
        super.use(info, order);
    }
}
