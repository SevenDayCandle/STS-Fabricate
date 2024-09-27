package pinacolada.skills.skills.base.modifiers;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLAffinity;
import pinacolada.resources.PGR;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.fields.PField_CardModifyAffinity;
import pinacolada.utilities.GameUtilities;

@VisibleSkill
public class PMod_PerCardAffinity extends PMod_PerCardProperty<PField_CardModifyAffinity> {

    public static final PSkillData<PField_CardModifyAffinity> DATA = register(PMod_PerCardAffinity.class, PField_CardModifyAffinity.class).noTarget();

    public PMod_PerCardAffinity() {
        this(1);
    }

    public PMod_PerCardAffinity(int amount) {
        super(DATA, amount);
    }

    public PMod_PerCardAffinity(PSkillSaveData content) {
        super(DATA, content);
    }

    @Override
    protected int getCardProperty(AbstractCard c) {
        return Math.max(0, fields.addAffinities.isEmpty() ? GameUtilities.getPCLCardAffinityLevel(c, PCLAffinity.General, true) : EUIUtils.sumInt(fields.addAffinities, a -> GameUtilities.getPCLCardAffinityLevel(c, a, true)));
    }

    @Override
    protected String getCardPropertyString() {
        return PField_CardCategory.getAffinityOrString(fields.addAffinities);
    }

    @Override
    public String getSubSampleText() {
        return EUIRM.strings.adjNoun(TEXT.subjects_card, PGR.core.tooltips.affinityGeneral.title);
    }
}
