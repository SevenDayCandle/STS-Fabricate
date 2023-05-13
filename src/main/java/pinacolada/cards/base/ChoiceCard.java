package pinacolada.cards.base;

public class ChoiceCard<T> extends PCLDynamicCard {
    public final T value;

    public ChoiceCard(ChoiceCardData<T> builder) {
        super(builder);
        this.value = builder.object;
    }

    protected void initializeTextures() {
        if (((ChoiceCardData<?>) builder).fromCustom) {
            super.initializeTextures();
        }
    }
}