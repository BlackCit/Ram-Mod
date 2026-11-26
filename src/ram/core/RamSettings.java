package ram.core;

import arc.Core;
import mindustry.Vars;

public class RamSettings {
    public static boolean enableUnits = true;
    public static boolean enableWeapons = true;
    public static boolean enableBuildings = true;
    public static boolean enableExplosions = true;
    public static boolean enableScreenShake = true;
    public static boolean enableMenuAnim = true;
    public static boolean enablePlanetFlight = true;
    public static boolean enableButtons = true;
    public static boolean enableMenuBg = true;
    public static float particleDensity = 1.0f;

    public static void load() {
        enableUnits = Core.settings.getBool("ram-units", true);
        enableWeapons = Core.settings.getBool("ram-weapons", true);
        enableBuildings = Core.settings.getBool("ram-buildings", true);
        enableExplosions = Core.settings.getBool("ram-explosions", true);
        enableScreenShake = Core.settings.getBool("ram-shake", true);
        enableMenuAnim = Core.settings.getBool("ram-menu", true);
        enablePlanetFlight = Core.settings.getBool("ram-planets", true);
        enableButtons = Core.settings.getBool("ram-buttons", true);
        enableMenuBg = Core.settings.getBool("ram-menu-bg", true);
        particleDensity = Core.settings.getInt("ram-density", 100) / 100f;

        Vars.ui.settings.addCategory(Core.bundle.get("setting.category.ram-anim.name"), t -> {
            t.checkPref("ram-units", true, val -> enableUnits = val);
            t.checkPref("ram-weapons", true, val -> enableWeapons = val);
            t.checkPref("ram-buildings", true, val -> enableBuildings = val);
            t.checkPref("ram-explosions", true, val -> enableExplosions = val);
            t.checkPref("ram-shake", true, val -> enableScreenShake = val);
            t.checkPref("ram-menu", true, val -> enableMenuAnim = val);
            t.checkPref("ram-planets", true, val -> enablePlanetFlight = val);
            t.checkPref("ram-buttons", true, val -> enableButtons = val);
            t.checkPref("ram-menu-bg", true, val -> enableMenuBg = val);
            t.sliderPref("ram-density", 100, 25, 200, 25, val -> {
                particleDensity = val / 100f;
                return val + "%";
            });
        });
    }
}
