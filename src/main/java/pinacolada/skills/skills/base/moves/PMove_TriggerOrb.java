package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbData;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.ui.editor.orb.PCLCustomOrbEditScreen;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_TriggerOrb extends PMove<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PMove_TriggerOrb.class, PField_Orb.class)
            .setExtra(0, Integer.MAX_VALUE)
            .noTarget();

    public PMove_TriggerOrb() {
        this(1, 1);
    }

    public PMove_TriggerOrb(int amount, int extra, PCLOrbData... orb) {
        super(DATA, PCLCardTarget.None, amount, extra);
        fields.setOrb(orb);
    }

    public PMove_TriggerOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_TriggerOrb(int amount, PCLOrbData... orb) {
        this(amount, 1, orb);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_trigger(TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String orbStr = fields.not ? TEXT.subjects_this : fields.getOrbExtraString(requestor);
        return amount == 1 ? TEXT.act_trigger(orbStr) : TEXT.act_triggerXTimes(orbStr, getAmountRawString(requestor));
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRandom(editor);
        if (editor.editor.screen instanceof PCLCustomOrbEditScreen) {
            fields.registerNotBoolean(editor, StringUtils.capitalize(TEXT.subjects_this), null);
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (fields.not && source instanceof AbstractOrb) {
            order.triggerOrbPassive((AbstractOrb) source, refreshAmount(info));
        }
        else {
            order.triggerOrbPassive(refreshAmount(info), extra <= 0 ? GameUtilities.getOrbCount() : extra, fields.random)
                    .setFilter(fields.getOrbFilter());
        }

        super.use(info, order);
    }
}
