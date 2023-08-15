package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
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
public class PMod_PerOrbTurn extends PMod_Per<PField_Orb> {

    public static final PSkillData<PField_Orb> DATA = register(PMod_PerOrbTurn.class, PField_Orb.class).selfTarget();

    public PMod_PerOrbTurn(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerOrbTurn() {
        super(DATA);
    }

    public PMod_PerOrbTurn(int amount, PCLOrbHelper... orbs) {
        super(DATA, amount);
        fields.setOrb(orbs);
    }

    @Override
    public String getConditionText(PCLCardTarget perspective, String childText) {
        if (fields.not) {
            return TEXT.cond_xConditional(childText,
                    fields.random ? TEXT.cond_perThisCombat(getAmountRawString(), fields.getOrbAndString(1), PCLCoreStrings.past(PGR.core.tooltips.channel)) : TEXT.cond_perThisTurn(getAmountRawString(), fields.getOrbAndString(1), PCLCoreStrings.past(PGR.core.tooltips.evoke)));
        }
        String subjString = this.amount <= 1 ? fields.getOrbAndString(1) : EUIRM.strings.numNoun(getAmountRawString(), fields.getOrbAndString());
        return fields.random ? TEXT.cond_perThisCombat(childText, subjString, PCLCoreStrings.past(PGR.core.tooltips.channel)) : TEXT.cond_perThisTurn(childText, subjString, PCLCoreStrings.past(PGR.core.tooltips.channel));
    }

    @Override
    public int getMultiplier(PCLUseInfo info, boolean isUsing) {
        List<AbstractOrb> orbs = fields.random ? AbstractDungeon.actionManager.orbsChanneledThisCombat : AbstractDungeon.actionManager.orbsChanneledThisTurn;
        return (fields.orbs.isEmpty() ? orbs.size() :
                EUIUtils.count(orbs, o -> EUIUtils.any(fields.orbs, orb -> orb.ID.equals(o.ID))));
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.cond_xPerYZ(TEXT.subjects_x, PGR.core.tooltips.orb.title, PGR.core.tooltips.channel.past());
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return PGR.core.tooltips.orb.title;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRBoolean(editor, TEXT.cedit_combat, null);
    }
}
