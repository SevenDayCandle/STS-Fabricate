package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Potion;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PMod_PerPotion extends PMod_Per<PField_Potion> {
    public static final PSkillData<PField_Potion> DATA = register(PMod_PerPotion.class, PField_Potion.class).selfTarget();

    public PMod_PerPotion(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerPotion() {
        super(DATA);
    }

    public PMod_PerPotion(int amount, String... potion) {
        super(DATA, amount);
        fields.setPotionID(potion);
    }

    @Override
    public int getMultiplier(PCLUseInfo info) {
        return (EUIUtils.count(AbstractDungeon.player.potions, r -> fields.getFullPotionFilter().invoke(r)));
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
    }

    @Override
    public String getSubSampleText() {
        return TEXT.subjects_potion;
    }

    @Override
    public String getSubText() {
        return this.amount <= 1 ? fields.getFullPotionStringSingular() : fields.getFullPotionString();
    }
}
