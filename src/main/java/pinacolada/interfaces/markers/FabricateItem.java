package pinacolada.interfaces.markers;

// Custom cards do not have inherent cardStrings so checks on those (e.g. FlavorText) should be skipped
public interface FabricateItem {

    public EditorMaker getDynamicData();
}
