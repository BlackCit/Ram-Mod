package ram.effects;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import ram.core.RamSettings;

import static arc.graphics.g2d.Draw.*;

public class WeaponAnimations {

    public static final Effect brassCasing = new Effect(48f, e -> {
        float side = (e.id % 2 == 0) ? 1f : -1f;
        float ejectAngle = e.rotation + 90f * side + Mathf.range(18f);
        float dist = e.finpow() * 14f;
        float drop = e.fin() * e.fin() * 6f;

        float cx = e.x + Mathf.cosDeg(ejectAngle) * dist;
        float cy = e.y + Mathf.sinDeg(ejectAngle) * dist - drop;

        color(Pal.lightOrange, Color.goldenrod, e.fin());
        alpha(e.fout());
        Fill.rect(cx, cy, 2.2f, 1.1f, e.rotation + e.fin() * 540f * side);
    }).layer(Layer.debris);

    public static final Effect muzzleSpark = new Effect(14f, e -> {
        color(Color.white, Pal.lightOrange, e.fin());
        alpha(0.85f * e.fout());
        Fill.circle(e.x, e.y, 2.5f * e.fout());
    }).layer(Layer.bullet + 1f);

    public static final Effect rocketTrail = new Effect(42f, e -> {
        color(Pal.lightOrange, Color.gray, e.fin());
        alpha(0.55f * e.fout());
        Fill.circle(e.x + Mathf.range(1.5f), e.y + Mathf.range(1.5f), 1.4f + e.fin() * 3.2f);
    });
    public static void update() {
        if (!RamSettings.enableWeapons || Vars.state.isPaused()) return;
        float mult = RamSettings.particleDensity;

        for (Bullet b : Groups.bullet) {
            if (b.time <= Time.delta * 1.2f) {
                if (b.type instanceof BasicBulletType || b.type instanceof FlakBulletType) {
                    if (Mathf.chance(0.75f * mult)) {
                        brassCasing.at(b.x, b.y, b.rotation());
                        muzzleSpark.at(b.x, b.y);
                    }
                }
            }

            if (b.type instanceof MissileBulletType || b.type instanceof ArtilleryBulletType) {
                if (Mathf.chance(0.65f * mult * Time.delta)) {
                    rocketTrail.at(b.x, b.y);
                }
            }
        }
    }
}
