package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIUtils;
import extendedui.interfaces.delegates.ActionT1;
import extendedui.interfaces.markers.KeywordProvider;
import extendedui.ui.tooltips.EUIKeywordTooltip;
import pinacolada.actions.PCLActions;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.relics.PCLRelic;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Relic;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@VisibleSkill
public class PMove_ObtainRelic extends PMove<PField_Relic> implements OutOfCombatMove {
    public static final PSkillData<PField_Relic> DATA = register(PMove_ObtainRelic.class, PField_Relic.class)
            .noTarget();

    public PMove_ObtainRelic() {
        super(DATA);
    }

    public PMove_ObtainRelic(Collection<String> relics) {
        super(DATA);
        fields.relicIDs.addAll(relics);
    }

    public PMove_ObtainRelic(String... relics) {
        super(DATA);
        fields.relicIDs.addAll(Arrays.asList(relics));
    }

    protected void createRelic(ActionT1<AbstractRelic> onCreate) {
        if (!fields.relicIDs.isEmpty()) {
            if (fields.not) {
                RandomizedList<String> choices = new RandomizedList<>(fields.relicIDs);
                for (int i = 0; i < amount; i++) {
                    AbstractRelic relic = RelicLibrary.getRelic(choices.retrieve(PCLRelic.rng, true));
                    if (relic != null) {
                        onCreate.invoke(relic.makeCopy());
                    }
                }
            }
            else {
                for (String r : fields.relicIDs) {
                    AbstractRelic relic = RelicLibrary.getRelic(r);
                    if (relic != null) {
                        onCreate.invoke(relic.makeCopy());
                    }
                }
            }
        }
        else {
            RandomizedList<String> choices = new RandomizedList<>();
            if (!fields.colors.isEmpty()) {
                if (fields.rarities.isEmpty()) {
                    for (AbstractCard.CardColor color : fields.colors) {
                        choices.addAll(GameUtilities.getRelics(color).keySet());
                    }
                }
                else {
                    for (AbstractCard.CardColor color : fields.colors) {
                        for (Map.Entry<String, AbstractRelic> relic : GameUtilities.getRelics(color).entrySet()) {
                            if (EUIUtils.any(fields.rarities, r -> r == relic.getValue().tier)) {
                                choices.add(relic.getKey());
                            }
                        }
                    }
                }
            }
            else if (!fields.rarities.isEmpty()) {
                for (AbstractRelic.RelicTier rarity : fields.rarities) {
                    choices.addAll(GameUtilities.getRelicPool(rarity));
                }
            }

            for (int i = 0; i < amount; i++) {
                AbstractRelic relic = RelicLibrary.getRelic(choices.retrieve(PCLRelic.rng, true));
                if (relic != null) {
                    onCreate.invoke(relic.makeCopy());
                }
            }
        }
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_obtain(TEXT.subjects_relic);
    }

    @Override
    public String getSubText(PCLCardTarget perspective) {
        return fields.relicIDs.isEmpty() ? TEXT.act_obtainAmount(getAmountRawString(), fields.getFullRelicString()) : TEXT.act_obtain(fields.not ? fields.getRelicIDOrString() : fields.getRelicIDAndString());
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public PMove_ObtainRelic onAddToCard(AbstractCard card) {
        super.onAddToCard(card);
        if (card instanceof KeywordProvider) {
            List<EUIKeywordTooltip> tips = ((KeywordProvider) card).getTips();
            if (tips != null) {
                for (String r : fields.relicIDs) {
                    AbstractRelic relic = RelicLibrary.getRelic(r);
                    if (relic != null) {
                        tips.add(new EUIKeywordTooltip(relic.name, relic.description));
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
        createRelic(order::obtainRelic);
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle() {
        super.useOutsideOfBattle();
        createRelic(GameUtilities::obtainRelicFromEvent);
    }
}
