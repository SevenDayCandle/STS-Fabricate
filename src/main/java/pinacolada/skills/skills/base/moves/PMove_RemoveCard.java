package pinacolada.skills.skills.base.moves;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import extendedui.EUIRM;
import extendedui.interfaces.delegates.ActionT1;
import pinacolada.actions.PCLActions;
import pinacolada.actions.piles.SelectFromPile;
import pinacolada.annotations.VisibleSkill;
import pinacolada.cards.base.PCLCardGroupHelper;
import pinacolada.cards.base.fields.PCLCardSelection;
import pinacolada.cards.base.fields.PCLCardTarget;
import pinacolada.effects.PCLEffects;
import pinacolada.misc.PCLUseInfo;
import pinacolada.skills.PSkill;
import pinacolada.skills.PSkillData;
import pinacolada.skills.PSkillSaveData;
import pinacolada.skills.fields.PField_CardCategory;
import pinacolada.skills.skills.PCallbackMove;
import pinacolada.utilities.GameUtilities;

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
        getActions().add(fields.createFilteredAction(SelectFromPile::new, info, extra))
                .setOptions((useParent || fields.groupTypes.isEmpty() ? PCLCardSelection.Random : PCLCardSelection.Manual).toSelection(), !fields.forced, false, false, true)
                .addCallback(cards -> {
                    // Remove all copies of the cards in play
                    for (AbstractCard c : cards)
                    {
                        AbstractCard masterCopy = GameUtilities.getMasterDeckInstance(c.uuid);
                        if (masterCopy != null)
                        {
                            PCLEffects.Queue.showCardBriefly(masterCopy);
                            AbstractDungeon.player.masterDeck.removeCard(masterCopy);
                        }
                        for (AbstractCard copy : GameUtilities.getAllInBattleInstances(c.uuid))
                        {
                            PCLActions.bottom.purge(copy).showEffect(true, false);
                        }
                    }
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
                : fields.groupTypes.size() > 0 ? EUIRM.strings.numNoun(extra > amount ? TEXT.subjects_xOfY(getAmountRawString(), getExtraRawString()) : getAmountRawString(), fields.getFullCardOrString(getRawString(EXTRA_CHAR)))
                : TEXT.subjects_thisCard;
        return TEXT.act_removeFrom(cString, TEXT.cpile_deck);
    }
}
