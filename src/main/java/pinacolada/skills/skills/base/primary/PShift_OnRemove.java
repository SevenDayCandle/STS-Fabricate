package pinacolada.skills.skills.base.primary;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import org.apache.commons.lang3.StringUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.CombatManager;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;
import pinacolada.skills.skills.PShift;

import java.util.Collections;

@VisibleSkill
public class PShift_OnRemove extends PShift {
    public static final PSkillData<PField_Empty> DATA = register(PShift_OnRemove.class, PField_Empty.class, 1, 1)
            .setSourceTypes(PSkillData.SourceType.Card, PSkillData.SourceType.Collectible)
            .noTarget();

    public PShift_OnRemove() {
        super(DATA);
    }

    public PShift_OnRemove(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String getDelegateText() {
        return PGR.core.tooltips.remove.past();
    }

    @Override
    public PShift_OnRemove scanForTips(String source) {
        if (tips == null) {
            tips = Collections.singletonList(new EUIKeywordTooltip(StringUtils.capitalize(TEXT.cond_when(PGR.core.tooltips.remove.past())), TEXT.cetut_onRemove));
        }
        return this;
    }

    @Override
    public void triggerOnRemoveFromInventory() {
        PCLUseInfo info = CombatManager.playerSystem.generateInfo(EUIUtils.safeCast(source, AbstractCard.class), getSourceCreature(), null);
        useOutsideOfBattle(info);
    }
}
