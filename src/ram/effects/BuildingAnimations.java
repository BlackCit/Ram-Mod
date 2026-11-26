package ram.effects;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.production.GenericCrafter;
import ram.core.RamSettings;

import static arc.graphics.g2d.Draw.*;

public class BuildingAnimations {

    public static final Effect factorySpark = new Effect(26f, e -> {
        color(Color.white, Pal.lightOrange, e.fin());
        alpha(0.85f * e.fout());
        float dist = e.finpow() * 10f;
        Fill.circle(e.x + Mathf.cosDeg(e.rotation) * dist, e.y + Mathf.sinDeg(e.rotation) * dist, 1.3f * e.fout());
    }).layer(Layer.blockOver);

    public static void update() {
        if (!RamSettings.enableBuildings || Vars.state.isPaused()) return;
        float mult = RamSettings.particleDensity;

        for (Building b : Groups.build) {
            if (!b.isValid()) continue;

            // 1. Густой дым и искры от повреждённых построек (< 55% ХП)
            if (b.healthf() < 0.55f) {
                float chance = (0.55f - b.healthf()) * 0.25f * mult * Time.delta;
                if (Mathf.chance(chance)) {
                    float spread = b.block.size * 3.5f;
                    UnitAnimations.heavyDamageSmoke.at(b.x + Mathf.range(spread), b.y + Mathf.range(spread), Mathf.random(360f));
                }
            }

            // 2. Искры от активно работающих заводов и переплавщиков
            if (b.block instanceof GenericCrafter && b.efficiency > 0.1f) {
                if (Mathf.chance(0.06f * b.efficiency * mult * Time.delta)) {
                    float spread = b.block.size * 3f;
                    factorySpark.at(b.x + Mathf.range(spread), b.y + Mathf.range(spread), Mathf.random(360f));
                }
            }
        }
    }
}
