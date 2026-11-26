package ram.effects;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.environment.Floor;
import ram.core.RamSettings;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.*;

public class UnitAnimations {

    public static final Effect groundDust = new Effect(45f, e -> {
        color(e.color);
        alpha(0.45f * e.fout());
        float spread = e.fin() * 8f;
        Fill.circle(e.x + Mathf.cos(e.rotation) * spread, e.y + Mathf.sin(e.rotation) * spread, 1.2f + e.fin() * 2.8f);
    }).layer(Layer.groundUnit - 1f);

    public static final Effect waterWake = new Effect(60f, e -> {
        color(Color.white);
        alpha(0.35f * e.fout());
        stroke(1.2f * e.fout());
        circle(e.x, e.y, 2f + e.fin() * 9f);
    }).layer(Layer.groundUnit - 1f);

    public static final Effect jetExhaust = new Effect(24f, e -> {
        color(e.color, Color.lightGray, e.fin());
        alpha(0.45f * e.fout());
        Fill.circle(e.x + Mathf.range(1f), e.y + Mathf.range(1f), 1.0f + e.fin() * 2.2f);
    }).layer(Layer.flyingUnit - 1f);

    public static final Effect heavyDamageSmoke = new Effect(90f, e -> {
        color(Color.darkGray, Color.gray, e.fin());
        alpha(0.65f * e.fout());
        float x = e.x + Mathf.cos(e.id * 12f) * e.fin() * 14f;
        float y = e.y + Mathf.sin(e.id * 12f) * e.fin() * 14f + e.fin() * 8f;
        Fill.circle(x, y, 1.5f + e.fin() * 4.5f);

        if (e.id % 3 == 0 && e.fin() < 0.4f) {
            color(Pal.lightOrange, Pal.turretHeat, e.fin() * 2.5f);
            alpha(e.fout());
            stroke(1f);
            lineAngle(e.x, e.y, e.rotation, 3f * e.fout());
        }
    });
    public static void update() {
        if (!RamSettings.enableUnits || Vars.state.isPaused()) return;
        float mult = RamSettings.particleDensity;

        for (Unit unit : Groups.unit) {
            if (unit.healthf() < 0.5f) {
                float chance = (0.5f - unit.healthf()) * 0.45f * mult * Time.delta;
                if (Mathf.chance(chance)) {
                    heavyDamageSmoke.at(unit.x + Mathf.range(unit.hitSize * 0.4f), unit.y + Mathf.range(unit.hitSize * 0.4f), Mathf.random(360f));
                }
            }

            if (unit.moving()) {
                float speed = unit.vel.len();
                float backAngle = unit.rotation + 180f + Mathf.range(25f);
                float offset = unit.hitSize * 0.45f;
                float px = unit.x + Mathf.cosDeg(backAngle) * offset;
                float py = unit.y + Mathf.sinDeg(backAngle) * offset;

                if (unit.isFlying()) {
                    if (Mathf.chance(0.35f * speed * mult * Time.delta)) {
                        jetExhaust.at(px, py, backAngle, unit.team.color);
                    }
                } else {
                    Floor floor = unit.floorOn();
                    if (floor != null && Mathf.chance(0.25f * speed * mult * Time.delta)) {
                        if (floor.isLiquid) waterWake.at(px, py);
                        else groundDust.at(px, py, backAngle, floor.mapColor);
                    }
                }
            }
        }
    }
}
