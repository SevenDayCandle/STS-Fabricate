package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.piles.RemoveFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.dungeon.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCallbackMove;

@VisibleSkill
public class PMove_RemoveCard extends PCallbackMove<PField_CardCategory>
{
    public static final PSkillData<PField_CardCategory> DATA = register(PMove_RemoveCard.class, PField_CardCategory.class)
            .setGroups(PCLCardGroupHelper.MasterDeck)
            .selfTarget();

    public PMove_RemoveCard()
    {
        this(1);
    }

    public PMove_RemoveCard(PSkillSaveData content)
    {
        super(DATA, content);
    }

    public PMove_RemoveCard(int copies, PCLCardGroupHelper... gt)
    {
        super(DATA, PCLCardTarget.None, copies);
        fields.setCardGroup(gt);
    }

    public PMove_RemoveCard(int copies, int extra, PCLCardGroupHelper... gt)
    {
        super(DATA, PCLCardTarget.None, copies, extra);
        fields.setCardGroup(gt);
    }

    @Override
    public PMove_RemoveCard onAddToCard(AbstractCard card)
    {
        super.onAddToCard(card);
        if (card.tags.contains(AbstractCard.CardTags.HEALING))
        {
            card.tags.add(AbstractCard.CardTags.HEALING);
        }
        return this;
    }

    @Override
    public String getSampleText(PSkill<?> callingSkill)
    {
        return TEXT.act_removeFrom(TEXT.subjects_card, TEXT.cpile_deck);
    }

    @Override
    public void use(PCLUseInfo info, ActionT1<PCLUseInfo> callback)
    {
        getActions().add(fields.createAction(RemoveFromPile::new, info, extra))
                .setFilter(c -> fields.getFullCardFilter().invoke(c))
                .setAnyNumber(!fields.forced)
                .addCallback(cards -> {
                    info.setData(cards);
                    callback.invoke(info);
                    if (this.childEffect != null)
                    {
                        this.childEffect.use(info);
                    }
                });
    }

    @Override
    public String getSubText()
    {
        String cString = useParent ? TEXT.subjects_them
                : fields.groupTypes.size() > 0 ? EUIRM.strings.numNoun(extra > amount ? TEXT.subjects_xOfY(getAmountRawString(), getExtraRawString()) : getAmountRawString(), fields.getCardOrString(getRawString(EXTRA_CHAR)))
                : TEXT.subjects_thisCard;
        return TEXT.act_removeFrom(cString, fields.getGroupString());
    }
}
