package pinacolada.skills.skills.special.moves;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import pinacolada.actions.PCLActions;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.monsters.PCLCardAlly;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

import java.util.List;

public class PMove_DisableRelic extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_DisableRelic.class, PField_Empty.class, 1, 1)
            .selfTarget()
            .pclOnly();

    public PMove_DisableRelic() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PMove_DisableRelic(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_disable(TEXT.subjects_relic);
    }

    @Override
    public String getSubText() {
        return TEXT.act_disable(TEXT.subjects_this);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (source instanceof AbstractRelic) {
            order.callback(() -> {
                ((AbstractRelic) source).usedUp();
            });
        }
    }
}
