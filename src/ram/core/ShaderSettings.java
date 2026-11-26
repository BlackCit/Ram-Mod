package ram.core;

import arc.Core;
import mindustry.Vars;

public class ShaderSettings {
    public static boolean enableShadows = true;
    public static boolean enableGlow = true;
    public static boolean enableVignette = true;
    public static float shadowOpacity = 0.35f;
    public static float glowIntensity = 0.45f;
    public static int qualityLevel = 1; // 0 = Low, 1 = Med, 2 = High

    public static void load() {
        enableShadows = Core.settings.getBool("ram-gfx-shadows", true);
        enableGlow = Core.settings.getBool("ram-gfx-glow", true);
        enableVignette = Core.settings.getBool("ram-gfx-vignette", true);
        shadowOpacity = Core.settings.getInt("ram-gfx-shadow-op", 35) / 100f;
        glowIntensity = Core.settings.getInt("ram-gfx-glow-int", 45) / 100f;
        qualityLevel = Core.settings.getInt("ram-gfx-quality", 1);

        Vars.ui.settings.addCategory(Core.bundle.get("setting.category.ram-gfx.name"), t -> {
            t.sliderPref("ram-gfx-quality", 1, 0, 2, 1, val -> {
                qualityLevel = val;
                if (val == 0) return "Low (FPS Boost)";
                if (val == 1) return "Medium (Balanced)";
                return "Ultra (Max Visuals)";
            });
            t.checkPref("ram-gfx-shadows", true, val -> enableShadows = val);
            t.sliderPref("ram-gfx-shadow-op", 35, 10, 75, 5, val -> {
                shadowOpacity = val / 100f;
                return val + "%";
            });
            t.checkPref("ram-gfx-glow", true, val -> enableGlow = val);
            t.sliderPref("ram-gfx-glow-int", 45, 10, 100, 5, val -> {
                glowIntensity = val / 100f;
                return val + "%";
            });
            t.checkPref("ram-gfx-vignette", true, val -> enableVignette = val);
        });
    }
}
