package pinacolada.cards.base;

public class ChoiceCard<T> extends PCLDynamicCard {
    public final T value;

    public ChoiceCard(ChoiceData<T> builder) {
        super(builder);
        this.value = builder.object;
    }

    protected void initializeTextures() {
        if (((ChoiceData<?>) builder).fromCustom) {
            super.initializeTextures();
        }
    }
}