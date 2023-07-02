package pinacolada.skills.skills.base.moves;

import com.evacipated.cardcrawl.mod.stslib.powers.StunMonsterPower;
import com.megacrit.cardcrawl.actions.watcher.PressEndTurnButtonAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
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
public class PMove_Stun extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_Stun.class, PField_Empty.class);

    public PMove_Stun() {
        this(1);
    }

    public PMove_Stun(int amount) {
        super(DATA, PCLCardTarget.Single, amount);
    }

    public PMove_Stun(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_stun(TEXT.subjects_x);
    }

    @Override
    public String getSubText() {
        return target == PCLCardTarget.None ? TEXT.act_skipTurn() : TEXT.act_stun(getTargetString());
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (target == PCLCardTarget.None) {
            order.add(new PressEndTurnButtonAction());
        }
        else {
            // This power cannot be applied to players
            for (AbstractCreature c : getTargetList(info)) {
                if (c instanceof AbstractMonster) {
                    order.applyPower(info.source, new StunMonsterPower((AbstractMonster) c, amount));
                }
            }
        }
        super.use(info, order);
    }
}