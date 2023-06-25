package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

import java.util.List;

public abstract class PMod_BonusOnHas extends PMod_BonusOn<PField_CardCategory> {
    public PMod_BonusOnHas(PSkillData<PField_CardCategory> data, PSkillSaveData content) {
        super(data, content);
    }

    public PMod_BonusOnHas(PSkillData<PField_CardCategory> data) {
        this(data, 0, 1);
    }

    public PMod_BonusOnHas(PSkillData<PField_CardCategory> data, int amount, int count) {
        super(data, amount, count);
    }

    @Override
    public String getConditionText() {
        return fields.forced ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getExtraRawString(), fields.getFullCardOrString(getExtraRawString()))) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getExtraRawString(), fields.getFullCardOrString(getExtraRawString())));
    }

    @Override
    public boolean meetsCondition(PCLUseInfo info) {
        int count = EUIUtils.count(getCardPile(),
                c -> fields.getFullCardFilter().invoke(c));
        return extra == 0 ? count == 0 : fields.not ^ count >= extra;
    }

    @Override
    public String getSubText() {
        return TEXT.cond_ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerFBoolean(editor, TEXT.cedit_combat, null);
    }

    abstract public EUIKeywordTooltip getActionTooltip();

    abstract public List<AbstractCard> getCardPile();
}
