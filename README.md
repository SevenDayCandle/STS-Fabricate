# Fabricate for Slay the Spire

NOTE: Please use the Steam link to get the latest updates! The version on Github may not always be up to date with the Steam release.
https://steamcommunity.com/sharedfiles/filedetails/?id=2947433143

-----

This mod adds the following features:
- A new main menu option called "Customize" allows you to access an editor for creating your own cards. For an overview of how this editor works, check out this page: https://github.com/SevenDayCandle/STS-CardEditor/wiki/Basic-Usage.
- A custom run menu overhaul that has support for cards created through the editor mentioned above (this menu can be disabled in the mod settings). This overhaul includes a card filter that lets you filter out individual cards in your custom run.
- Additional console commands to spawn custom cards, unlock cards, and export card information to CSV files.
- Adjustments to Basemod's ImGui menu to allow spawning of custom cards and changing card forms.

This mod also comes with a number of hooks for modders to use to leverage the Fabricate's custom card creation features in their own code.

## Installation Instructions
- Install StsLib, ModTheSpire, BaseMod, and EUI (these can be gotten from the Steam workshop. If you have the GOG version, follow these steps instead: https://reddit.com/r/slaythespire/comments/gj5kel/howto_add_mods_to_gog_version/)
- Download the mod from the link above and place it into your mods folder (usually {folder containing STS}\SlayTheSpire\mods; you will need to create this folder if it doesn't exist)
- Run ModTheSpire. Ensure that StsLib, BaseMod, and EUI are checked and appear above this mod in the mod load order

## Compatibility
- Certain custom characters may not interact properly with custom cards created for their card color.
- Custom mechanics introduced by other mods will not be included in the editor by default. Such mechanics require explicit integrations with said mods.
- Having other mods active that affect the cards available in the run (e.g. Card Filter) may interfere with this mod's card filtering function in custom runs.

## Links

If you would like to give feedback on the current state of this mod or wish to follow it for updates, you can do so at this Discord server: https://discord.gg/he76RmsuYZ

## Credits
- EatYourBeetS/The Animator for the initial framework. The original mod can be found here: https://steamcommunity.com/sharedfiles/filedetails/?id=1638308801, while their discord can be found here: https://discord.gg/SmHMmJR
- Several Effekseer effects are sourced from https://effekseer.github.io/en/contribute.html
