package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.List;

@VisibleSkill
public class PMod_PerOrbEvokedTurn extends PMod_Per<PField_Orb> {

    public static final PSkillData<PField_Orb> DATA = register(PMod_PerOrbEvokedTurn.class, PField_Orb.class).selfTarget();

    public PMod_PerOrbEvokedTurn(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerOrbEvokedTurn() {
        super(DATA);
    }

    public PMod_PerOrbEvokedTurn(int amount, PCLOrbHelper... orbs) {
        super(DATA, amount);
        fields.setOrb(orbs);
    }

    @Override
    public String getConditionText(String childText) {
        if (fields.not) {
            return TEXT.cond_xConditional(childText,
                    fields.random ? TEXT.cond_perThisCombat(getAmountRawString(), fields.getOrbAndString(1), PCLCoreStrings.past(PGR.core.tooltips.evoke)) : TEXT.cond_perThisTurn(getAmountRawString(), fields.getOrbAndString(1), PCLCoreStrings.past(PGR.core.tooltips.evoke)));
        }
        String subjString = this.amount <= 1 ? fields.getOrbAndString(1) : EUIRM.strings.numNoun(getAmountRawString(), fields.getOrbAndString());
        return fields.random ? TEXT.cond_perThisCombat(childText, subjString, PCLCoreStrings.past(PGR.core.tooltips.evoke)) : TEXT.cond_perThisTurn(childText, subjString, PCLCoreStrings.past(PGR.core.tooltips.evoke));
    }

    @Override
    public int getMultiplier(PCLUseInfo info) {
        List<AbstractOrb> orbs = fields.random ? CombatManager.orbsEvokedThisCombat() : CombatManager.orbsEvokedThisTurn();
        return (fields.orbs.isEmpty() ? orbs.size() :
                EUIUtils.count(orbs, o -> EUIUtils.any(fields.orbs, orb -> orb.ID.equals(o.ID))));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.cond_perXY(TEXT.subjects_x, PGR.core.tooltips.orb.title, PGR.core.tooltips.evoke.past());
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRBoolean(editor, TEXT.cedit_combat, null);
    }

    @Override
    public String getSubText() {
        return PGR.core.tooltips.orb.title;
    }
}
