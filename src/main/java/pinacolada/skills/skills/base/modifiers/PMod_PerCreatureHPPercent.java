package pinacolada.skills.skills.base.modifiers;

import com.badlogic.gdx.math.MathUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Not;

@VisibleSkill
public class PMod_PerCreatureHPPercent extends PMod_Per<PField_Not> {
    public static final PSkillData<PField_Not> DATA = register(PMod_PerCreatureHPPercent.class, PField_Not.class).selfTarget();

    public PMod_PerCreatureHPPercent(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMod_PerCreatureHPPercent() {
        super(DATA);
    }

    public PMod_PerCreatureHPPercent(int amount) {
        super(DATA, PCLCardTarget.Self, amount);
    }

    public PMod_PerCreatureHPPercent(PCLCardTarget target, int amount) {
        super(DATA, target, amount);
    }

    @Override
    public String getSubSampleText() {
        return PGR.core.tooltips.hp.title + "%";
    }

    @Override
    public int getMultiplier(PCLUseInfo info) {
        return sumTargets(info, t -> MathUtils.ceil(t.currentHealth * 100f / t.maxHealth));
    }

    @Override
    public String getSubText() {
        String baseString = getSubSampleText();
        switch (target) {
            case All:
            case Any:
                return TEXT.subjects_onAnyCharacter(baseString);
            case AllEnemy:
                return TEXT.subjects_onAnyEnemy(baseString);
            case Single:
                return TEXT.subjects_onTheEnemy(baseString);
            case Self:
                return TEXT.subjects_onYou(baseString);
            default:
                return baseString;
        }
    }
}
