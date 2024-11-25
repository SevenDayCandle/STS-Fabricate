package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import extendedui.EUIGameUtils;
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
import pinacolada.relics.PCLCustomRelicSlot;
import pinacolada.relics.PCLDynamicRelicData;
import pinacolada.skills.PMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.fields.PField_Relic;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;
import pinacolada.utilities.GameUtilities;
import pinacolada.utilities.RandomizedList;

import java.util.*;

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

    protected void createRelic(ActionT1<AbstractRelic> onCreate, PCLUseInfo info) {
        if (!fields.relicIDs.isEmpty()) {
            if (fields.not) {
                RandomizedList<String> choices = new RandomizedList<>(fields.relicIDs);
                for (int i = 0; i < refreshAmount(info); i++) {
                    AbstractRelic relic = RelicLibrary.getRelic(choices.retrieve(AbstractDungeon.relicRng, true));
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
                        for (PCLCustomRelicSlot slot : PCLCustomRelicSlot.getRelics(color)) {
                            choices.add(slot.ID);
                        }
                    }
                }
                else {
                    for (AbstractCard.CardColor color : fields.colors) {
                        for (Map.Entry<String, AbstractRelic> relic : GameUtilities.getRelics(color).entrySet()) {
                            if (EUIUtils.any(fields.rarities, r -> r == relic.getValue().tier)) {
                                choices.add(relic.getKey());
                            }
                        }
                        for (PCLCustomRelicSlot slot : PCLCustomRelicSlot.getRelics(color)) {
                            PCLDynamicRelicData relic = slot.getFirstBuilder();
                            if (relic != null && EUIUtils.any(fields.rarities, r -> r == relic.tier)) {
                                choices.add(slot.ID);
                            }
                        }
                    }
                }
            }
            else if (!fields.rarities.isEmpty()) {
                for (AbstractRelic.RelicTier rarity : fields.rarities) {
                    ArrayList<String> pool = GameUtilities.getRelicPool(rarity);
                    if (pool != null) {
                        choices.addAll(GameUtilities.getRelicPool(rarity));
                    }
                    else {
                        AbstractCard.CardColor targetColor = AbstractDungeon.player != null ? AbstractDungeon.player.getCardColor() : AbstractCard.CardColor.COLORLESS;
                        for (Map.Entry<String, AbstractRelic> relic : GameUtilities.getRelics(targetColor).entrySet()) {
                            if (rarity == relic.getValue().tier) {
                                choices.add(relic.getKey());
                            }
                        }
                        for (PCLCustomRelicSlot slot : PCLCustomRelicSlot.getRelics(targetColor)) {
                            PCLDynamicRelicData relic = slot.getFirstBuilder();
                            if (relic != null && EUIUtils.any(fields.rarities, r -> r == relic.tier)) {
                                choices.add(slot.ID);
                            }
                        }
                    }
                }
            }
            else {
                choices.addAll(EUIGameUtils.getInGameRelicIDs());
            }

            for (int i = 0; i < refreshAmount(info); i++) {
                AbstractRelic relic = RelicLibrary.getRelic(choices.retrieve(AbstractDungeon.relicRng, true));
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
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        return fields.relicIDs.isEmpty() ? TEXT.act_obtainAmount(getAmountRawString(requestor), fields.getFullRelicString(requestor)) : TEXT.act_obtain(fields.not ? fields.getRelicIDOrString() : fields.getRelicIDAndString());
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
                for (String r : fields.relicIDs) {
                    AbstractRelic relic = RelicLibrary.getRelic(r);
                    if (relic != null) {
                        tips.add(new EUIKeywordTooltip(relic.name, relic.description));
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
        createRelic(order::obtainRelic, info);
        super.use(info, order);
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        PCLEffects.Queue.callback(() -> {
            createRelic(GameUtilities::obtainRelicFromEvent, info);
        }).addCallback(() -> super.useOutsideOfBattle(info)); // Use callback to avoid concurrent modification if called from relic
    }
}
