package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.augments.PCLAugmentData;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.special.ObtainAugmentEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Augment;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@VisibleSkill
public class PMove_ObtainAugment extends PMove<PField_Augment> implements OutOfCombatMove {
    public static final PSkillData<PField_Augment> DATA = register(PMove_ObtainAugment.class, PField_Augment.class)
            .noTarget();

    public PMove_ObtainAugment() {
        super(DATA);
    }

    public PMove_ObtainAugment(Collection<String> blights) {
        super(DATA);
        fields.augmentIDs.addAll(blights);
    }

    public PMove_ObtainAugment(String... blights) {
        super(DATA);
        fields.augmentIDs.addAll(Arrays.asList(blights));
    }

    protected void createAugment(PCLUseInfo info) {
        if (!fields.augmentIDs.isEmpty()) {
            if (fields.not) {
                RandomizedList<String> choices = new RandomizedList<>(fields.augmentIDs);
                for (int i = 0; i < refreshAmount(info); i++) {
                    String choice = choices.retrieve(AbstractDungeon.relicRng, true);
                    if (choice != null) {
                        PCLAugmentData augment = PCLAugmentData.getStaticDataOrCustom(choice);
                        if (augment != null) {
                            PCLEffects.Queue.add(new ObtainAugmentEffect(augment.create(), Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f));
                        }
                    }
                }
            }
            else {
                for (String r : fields.augmentIDs) {
                    PCLAugmentData augment = PCLAugmentData.getStaticDataOrCustom(r);
                    if (augment != null) {
                        PCLEffects.Queue.add(new ObtainAugmentEffect(augment.create(), Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f));
                    }
                }
            }
        }
        else {
            RandomizedList<PCLAugmentData> choices = new RandomizedList<>(PCLAugmentData.getAllData(true, false, aug -> fields.colors.isEmpty() || fields.colors.contains(aug.cardColor)));
            for (int i = 0; i < refreshAmount(info); i++) {
                PCLAugmentData choice = choices.retrieve(AbstractDungeon.relicRng, true);
                if (choice != null) {
                    PCLEffects.Queue.add(new ObtainAugmentEffect(choice.create(), Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f));
                }
            }
        }
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_obtain(PGR.core.tooltips.augment.title);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return fields.augmentIDs.isEmpty() ? TEXT.act_obtainAmount(getAmountRawString(requestor), fields.getFullAugmentString(requestor)) : TEXT.act_obtain(fields.not ? fields.getAugmentIDOrString() : fields.getAugmentIDAndString());
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void onSetupTips(AbstractCard card) {
        if (card instanceof KeywordProvider) {
            List<EUIKeywordTooltip> tips = ((KeywordProvider) card).getTips();
            if (tips != null) {
                for (String r : fields.augmentIDs) {
                    PCLAugmentData a = PCLAugmentData.getStaticDataOrCustom(r);
                    if (a != null) {
                        tips.add(new EUIKeywordTooltip(a.getName(), a.getText()));
                    }
                }
            }
        }
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerNotBoolean(editor, TEXT.cedit_or, null);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        order.callback(() -> createAugment(info));
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        PCLEffects.Queue.callback(() -> createAugment(info))
                .addCallback(() -> super.useOutsideOfBattle(info));
    }
}
