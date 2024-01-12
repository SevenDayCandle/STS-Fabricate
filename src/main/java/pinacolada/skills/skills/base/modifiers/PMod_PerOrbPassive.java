package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLDynamicOrbData;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.ui.editor.orb.PCLCustomOrbEditScreen;

@VisibleSkill
public class PMod_PerOrbPassive extends PMod_Per<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PMod_PerOrbPassive.class, PField_Orb.class).noTarget();

    public PMod_PerOrbPassive(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerOrbPassive() {
        this(1);
    }

    public PMod_PerOrbPassive(int amount) {
        super(DATA, amount);
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        return !fields.random && source instanceof AbstractOrb ? ((AbstractOrb) source).passiveAmount : AbstractDungeon.player != null ?
                EUIUtils.sumInt(AbstractDungeon.player.orbs, o -> fields.getOrbFilter().invoke(o) ? o.passiveAmount : 0) : 0;
    }

    @Override
    public String getSubSampleText() {
        return EUIRM.strings.adjNoun(PGR.core.tooltips.orb.title, PGR.core.tooltips.focus.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String subjectString = amount > 1 ? EUIRM.strings.numNoun(getAmountRawString(), PGR.core.tooltips.focus.title) : PGR.core.tooltips.focus.title;
        if (useParent) {
            return TEXT.subjects_onTarget(subjectString, getInheritedThemString());
        }
        return TEXT.subjects_onTarget(subjectString, !fields.random && (source instanceof AbstractOrb || requestor instanceof PCLDynamicOrbData) ? TEXT.subjects_this : fields.getOrbAndString());
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
        if (editor.editor.screen instanceof PCLCustomOrbEditScreen) {
            fields.registerRBoolean(editor, StringUtils.capitalize(PGR.core.strings.subjects_allX(PGR.core.tooltips.orb.title)), null);
        }
    }
}
