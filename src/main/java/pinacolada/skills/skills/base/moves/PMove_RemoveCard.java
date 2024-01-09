package pinacolada.skills.skills.base.moves;

import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.actions.piles.RemoveFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.effects.PCLEffects;
import pinacolada.effects.card.ChooseCardsToPurgeEffect;
import pinacolada.interfaces.markers.OutOfCombatMove;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCallbackMove;
import pinacolada.ui.editor.PCLCustomEffectEditingPane;

@VisibleSkill
public class PMove_RemoveCard extends PCallbackMove<PField_CardCategory> implements OutOfCombatMove {
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_RemoveCard.class, PField_CardCategory.class)
            .setGroups(PCLCardGroupHelper.getAll())
            .noTarget();

    public PMove_RemoveCard() {
        this(1);
    }

    public PMove_RemoveCard(int copies, PCLCardGroupHelper... gt) {
        super(DATA, PCLCardTarget.None, copies);
        fields.setCardGroup(gt);
    }

    public PMove_RemoveCard(PSkillSaveData content) {
        super(DATA, content);
    }

    public PMove_RemoveCard(int copies, int extra, PCLCardGroupHelper... gt) {
        super(DATA, PCLCardTarget.None, copies, extra);
        fields.setCardGroup(gt);
    }

    @Override
    public String getAmountRawOrAllString() {
        return (shouldActAsAll()) ? fields.forced ? TEXT.subjects_all : TEXT.subjects_any
                : extra > 0 ? TEXT.subjects_xOfY(getExtraRawString(), getAmountRawString())
                : getAmountRawString();
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill, PSkill<?> parentSkill) {
        return TEXT.act_removeFrom(TEXT.subjects_card, TEXT.cpile_deck);
    }

    @Override
    public String getSubText(PCLCardTarget perspective, Object requestor) {
        if (useParent) {
            return TEXT.act_removeFrom(TEXT.subjects_themX, TEXT.cpile_deck);
        }
        else if (fields.groupTypes.size() == 0) {
            return TEXT.act_removeFrom(TEXT.subjects_thisCard(), TEXT.cpile_deck);
        }
        String cString = EUIRM.strings.numNoun(getAmountRawOrAllString(), fields.getCardOrString(getRawString(EXTRA_CHAR)));
        return fields.groupTypes.size() == 1 && fields.groupTypes.get(0) == PCLCardGroupHelper.MasterDeck ? TEXT.act_removeFrom(cString, TEXT.cpile_deck) :
                TEXT.act_removeInPlace(cString, fields.getGroupString(), TEXT.cpile_deck);
    }

    @Override
    public boolean isMetascaling() {
        return true;
    }

    @Override
    public void setupEditor(PCLCustomEffectEditingPane editor) {
        super.setupEditor(editor);
        registerUseParentBoolean(editor);
        fields.registerRequired(editor);
    }

    @Override
    public void use(PCLUseInfo info, PCLActions order, ActionT1<PCLUseInfo> callback) {
        order.add(fields.createAction(RemoveFromPile::new, info, extra))
                .setFilter(c -> fields.getFullCardFilter().invoke(c))
                .setAnyNumber(!fields.forced)
                .addCallback(cards -> {
                    info.setData(cards);
                    callback.invoke(info);
                    if (this.childEffect != null) {
                        this.childEffect.use(info, order);
                    }
                });
    }

    @Override
    public void useOutsideOfBattle(PCLUseInfo info) {
        PCLEffects.Queue.add(new ChooseCardsToPurgeEffect(amount, fields.getFullCardFilter()))
                .addCallback(effect -> {
                    info.setData(effect.cards);
                    super.useOutsideOfBattle(info);
                });
    }

    @Override
    public String wrapTextAmount(int input) {
        return extra > 0 || fields.forced || fields.origin != PCLCardSelection.Manual ? String.valueOf(input) : TEXT.subjects_upToX(input);
    }
}
