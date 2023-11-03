package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.cards.purple.Vault;
import com.megacrit.cardcrawl.powers.watcher.VaultPower;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_EndTurn extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_EndTurn.class, PField_Empty.class, 1, 1).noTarget();

    public PMove_EndTurn() {
        super(DATA);
    }

    public PMove_EndTurn(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_skipTurn();
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return TEXT.act_skipTurn();
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.add(new PressEndTurnButtonAction());
        super.use(info, order);
    }
}
