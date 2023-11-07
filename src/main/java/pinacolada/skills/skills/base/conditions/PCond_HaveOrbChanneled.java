package pinacolada.skills.skills.base.conditions;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import extendedui.EUIRM;
import extendedui.EUIUtils;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.interfaces.subscribers.OnOrbChannelSubscriber;
import pinacolada.orbs.PCLOrbData;
import pinacolada.resources.PGR;
import pinacolada.resources.pcl.PCLCoreStrings;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_Orb;
import pinacolada.skills.skills.PPassiveCond;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PCond_HaveOrbChanneled extends PPassiveCond<PField_Orb> implements OnOrbChannelSubscriber {
    public static final PSkillData<PField_Orb> DATA = register(PCond_HaveOrbChanneled.class, PField_Orb.class)
            .noTarget();

    public PCond_HaveOrbChanneled() {
        this(1);
    }

    public PCond_HaveOrbChanneled(int amount) {
        super(DATA, PCLCardTarget.None, amount);
    }

    public PCond_HaveOrbChanneled(PSkillSaveData content) {
        super(DATA, content);
    }

    public PCond_HaveOrbChanneled(int amount, PCLOrbData... orbs) {
        super(DATA, PCLCardTarget.None, amount);
        fields.setOrb(orbs);
    }

    @Override
    public boolean checkCondition(PCLUseInfo info, boolean isUsing, PSkill<?> triggerSource) {
        int count = EUIUtils.count(fields.random ? AbstractDungeon.actionManager.orbsChanneledThisCombat : AbstractDungeon.actionManager.orbsChanneledThisTurn,
                c -> fields.getOrbFilter().invoke(c));
        return amount == 0 ? count == 0 : fields.not ^ count >= amount;
    }

    public EUIKeywordTooltip getActionTooltip() {
        return PGR.core.tooltips.channel;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return isUnderWhen(callingSkill, parentSkill) ? TEXT.cond_when(TEXT.act_channel(PGR.core.tooltips.orb.title)) : TEXT.cond_ifX(PCLCoreStrings.past(getActionTooltip()));
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (isBranch()) {
            return getWheneverString(PGR.core.tooltips.channel.title, perspective);
        }
        String tt = fields.getOrbAndOrString();
        if (isWhenClause()) {
            return getWheneverString(TEXT.act_channel(tt), perspective);
        }
        return fields.random ? TEXT.cond_ifYouDidThisCombat(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), tt)) :
                TEXT.cond_ifYouDidThisTurn(PCLCoreStrings.past(getActionTooltip()), EUIRM.strings.numNoun(getAmountRawString(), tt));
    }

    @Override
    public void onChannelOrb(AbstractOrb orb) {
        if (fields.getOrbFilter().invoke(orb)) {
            useFromTrigger(generateInfo(null).setData(orb));
        }
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerRBoolean(editor, TEXT.cedit_combat, null);
    }
}
