package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import pinacolada.annotations.VisibleSkill;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLSFX;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Empty;

@VisibleSkill
public class PMove_GainGold extends PMove_Gain implements OutOfCombatMove {
    public static final PSkillData<PField_Empty> DATA = register(PMove_GainGold.class, PField_Empty.class)
            .setAmounts(-DEFAULT_MAX, DEFAULT_MAX)
            .selfTarget();

    public PMove_GainGold() {
        this(1);
    }

    public PMove_GainGold(int amount) {
        super(DATA, amount);
    }

    public PMove_GainGold(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    public String gainText() {
        return PGR.core.tooltips.gold.getTitleOrIcon();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill) {
        return TEXT.act_gainAmount(TEXT.subjects_x, PGR.core.tooltips.gold.title);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void use(PCLUseInfo info) {
        PCLSFX.play(PCLSFX.GOLD_GAIN);
        getActions().gainGold(amount);
        super.use(info);
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        PCLSFX.play(PCLSFX.GOLD_GAIN);
        AbstractDungeon.player.gainGold(amount);
    }
}
