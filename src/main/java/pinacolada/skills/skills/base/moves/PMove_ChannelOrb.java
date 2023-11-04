package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.orbs.PCLOrbHelper;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.utilities.GameUtilities;

import java.util.List;

@VisibleSkill
public class PMove_ChannelOrb extends PMove<PField_Orb> {
    public static final PSkillData<PField_Orb> DATA = register(PMove_ChannelOrb.class, PField_Orb.class)
            .setExtra(-1, DEFAULT_MAX)
            .noTarget();

    public PMove_ChannelOrb() {
        this(1);
    }

    public PMove_ChannelOrb(int amount, PCLOrbHelper... orb) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orb);
    }

    public PMove_ChannelOrb(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_ChannelOrb(int amount, int extra, PCLOrbHelper... orb) {
        super(DATA, PCLCardTarget.None, amount, extra);
        fields.setOrb(orb);
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_channelX(TEXT.subjects_x, PGR.core.tooltips.orb.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        String base = fields.getOrbAmountString();
        if (extra > 0) {
            base = TEXT.subjects_withX(base, EUIRM.strings.numNoun("+" + getExtraRawString(), PGR.core.tooltips.focus.title));
        }
        return fields.random ? TEXT.subjects_randomly(TEXT.act_channelX(getAmountRawString(), base))
                : TEXT.act_channelX(getAmountRawString(), base);
    }

    protected void modifyFocus(List<AbstractOrb> orbs) {
        if (extra > 0) {
            for (AbstractOrb o : orbs) {
                GameUtilities.modifyOrbBaseFocus(o, extra, true, false);
            }
        }
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        if (!fields.orbs.isEmpty()) {
            if (fields.random) {
                PCLOrbHelper orb = GameUtilities.getRandomElement(fields.orbs);
                if (orb != null) {
                    order.channelOrbs(orb, amount).addCallback(this::modifyFocus);
                }
            }
            else {
                for (PCLOrbHelper orb : fields.orbs) {
                    order.channelOrbs(orb, amount).addCallback(this::modifyFocus);
                }
            }
        }
        else {
            order.channelRandomOrbs(amount).addCallback(this::modifyFocus);
        }
        super.use(info, order);
    }
}
