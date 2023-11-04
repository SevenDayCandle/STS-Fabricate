package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.relics.AbstractRelic;
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
public class PMove_DisableRelic extends PMove<PField_Empty> {
    public static final PSkillData<PField_Empty> DATA = register(PMove_DisableRelic.class, PField_Empty.class, 1, 1)
            .setSourceTypes(PSkillData.SourceType.Collectible)
            .noTarget();

    public PMove_DisableRelic() {
        super(DATA, PCLCardTarget.None, 1);
    }

    public PMove_DisableRelic(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_disable(TEXT.subjects_relic);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
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
