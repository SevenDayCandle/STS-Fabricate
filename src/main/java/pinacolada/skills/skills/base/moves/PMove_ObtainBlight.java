package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import extendedui.utilities.BlightTier;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.patches.library.BlightHelperPatches;
import pinacolada.relics.PCLRelic;
import pinacolada.resources.PGR;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Blight;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@VisibleSkill
public class PMove_ObtainBlight extends PMove<PField_Blight> implements OutOfCombatMove {
    public static final PSkillData<PField_Blight> DATA = register(PMove_ObtainBlight.class, PField_Blight.class)
            .noTarget();

    public PMove_ObtainBlight() {
        super(DATA);
    }

    public PMove_ObtainBlight(Collection<String> blights) {
        super(DATA);
        fields.blightIDs.addAll(blights);
    }

    public PMove_ObtainBlight(String... blights) {
        super(DATA);
        fields.blightIDs.addAll(Arrays.asList(blights));
    }

    protected void createBlight(ActionT1<AbstractBlight> onCreate) {
        if (!fields.blightIDs.isEmpty()) {
            if (fields.not) {
                RandomizedList<String> choices = new RandomizedList<>(fields.blightIDs);
                for (int i = 0; i < amount; i++) {
                    // BlightHelper blights are always copies
                    String choice = choices.retrieve(PCLRelic.rng, true);
                    if (choice != null) {
                        AbstractBlight blight = BlightHelper.getBlight(choice);
                        if (blight != null) {
                            onCreate.invoke(blight);
                        }
                    }
                }
            }
            else {
                for (String r : fields.blightIDs) {
                    AbstractBlight blight = BlightHelper.getBlight(r);
                    if (blight != null) {
                        onCreate.invoke(blight);
                    }
                }
            }
        }
        else {
            RandomizedList<String> choices = BlightHelperPatches.getBlightIDs(
                    fields.rarities.isEmpty() ? EUIUtils.set(BlightTier.BASIC, BlightTier.BOSS) : fields.rarities,
                    fields.colors.isEmpty() ?
                            (AbstractDungeon.player != null ? EUIUtils.set(AbstractDungeon.player.getCardColor(), AbstractCard.CardColor.COLORLESS) : Collections.singleton(AbstractCard.CardColor.COLORLESS))
                            : fields.colors,
                    PGR.dungeon.allowCustomBlights);
            for (int i = 0; i < amount; i++) {
                String choice = choices.retrieve(PCLRelic.rng, true);
                if (choice != null) {
                    AbstractBlight blight = BlightHelper.getBlight(choice);
                    if (blight != null) {
                        onCreate.invoke(blight);
                    }
                }
            }
        }
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_obtain(TEXT.subjects_blight);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return fields.blightIDs.isEmpty() ? TEXT.act_obtainAmount(getAmountRawString(), fields.getFullBlightString()) : TEXT.act_obtain(fields.not ? fields.getBlightIDOrString() : fields.getBlightIDAndString());
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public PMove_ObtainBlight onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        if (card instanceof KeywordProvider) {
            List<EUIKeywordTooltip> tips = ((KeywordProvider) card).getTips();
            if (tips != null) {
                for (String r : fields.blightIDs) {
                    AbstractBlight blight = BlightHelper.getBlight(r);
                    if (blight != null) {
                        tips.add(new EUIKeywordTooltip(blight.name, blight.description));
                    }
                }
            }
        }
        return this;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        fields.registerNotBoolean(editor, TEXT.cedit_or, null);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order) {
        createBlight(order::obtainBlight);
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        PCLEffects.Queue.callback(() -> createBlight(b -> GameUtilities.obtainBlight(Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.5f, b)));
    }
}
