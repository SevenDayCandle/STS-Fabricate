package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import org.apache.commons.lang3.StringUtils;
import pinacolada.actions.PCLActions;
import pinacolada.actions.orbs.IncreaseOrbEvoke;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbData;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.ui.editor.orb.PCLCustomOrbEditScreen;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMove_IncreaseOrbEvoke extends PMove<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PMove_IncreaseOrbEvoke.class, PField_Orb.class)
            .setExtra(0, Integer.MAX_VALUE)
            .noTarget();

    public PMove_IncreaseOrbEvoke() {
        this(1, 1);
    }

    public PMove_IncreaseOrbEvoke(int amount, int extra, PCLOrbData... orb) {
        super(DATA, PCLCardTarget.None, amount, extra);
        fields.setOrb(orb);
    }

    public PMove_IncreaseOrbEvoke(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_IncreaseOrbEvoke(int amount, PCLOrbData... orb) {
        this(amount, 1, orb);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_increasePropertyBy(PGR.core.tooltips.evoke.title, PGR.core.tooltips.orb.title, TEXT.subjects_x);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String orbStr = fields.not ? TEXT.subjects_this : fields.getOrbExtraString(requestor);
        return TEXT.act_increasePropertyBy(PGR.core.tooltips.evoke.title, orbStr, getAmountRawString(requestor));
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
        int actualAmount = refreshAmount(info);
        if (fields.not && source instanceof AbstractOrb) {
            order.add(new IncreaseOrbEvoke(actualAmount, 1, fields.random, (AbstractOrb) source));
        }
        else {
            order.add(new IncreaseOrbEvoke(actualAmount, extra <= 0 ? GameUtilities.getOrbCount() : extra, fields.random, null))
                    .setFilter(fields.getOrbFilter());
        }

        super.use(info, order);
    }
}
