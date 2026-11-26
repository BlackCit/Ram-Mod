package ram.effects;

import arc.graphics.Color;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.game.EventType.BlockDestroyEvent;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import ram.core.RamSettings;

import static arc.graphics.g2d.Draw.*;

public class ExplosionAnimations {

    // Мощный многослойный взрыв (размер масштабируется через e.rotation!)
    public static final Effect realisticExplosion = new Effect(85f, 160f, e -> {
        float scale = Mathf.clamp(e.rotation / 16f, 0.6f, 3.5f);

        // 1. Вспышка ядра взрыва (первые 25% времени)
        if (e.fin() < 0.28f) {
            float f = 1f - (e.fin() / 0.28f);
            color(Color.white, Pal.lightOrange, 1f - f);
            alpha(f * 0.95f);
            Fill.circle(e.x, e.y, (10f + 14f * (1f - f)) * scale);
        }

        // 2. Двойная ударная волна
        float wave = e.finpow();
        color(Pal.lightOrange, Color.gray, e.fin());
        alpha(0.75f * e.fout());
        Lines.stroke(2.8f * e.fout() * Math.min(scale, 2f));
        Lines.circle(e.x, e.y, (6f + 38f * wave) * scale);

        alpha(0.45f * e.fout());
        Lines.stroke(1.4f * e.fout());
        Lines.circle(e.x, e.y, (4f + 54f * e.fin()) * scale);

        // 3. Клубы густого дыма, поднимающиеся от центра
        int smokeCount = Mathf.clamp((int)(6 * scale), 5, 16);
        for (int i = 0; i < smokeCount; i++) {
            float ang = Mathf.randomSeed(e.id + i, 360f);
            float spd = Mathf.randomSeed(e.id + i + 20, 6f, 26f) * scale;
            float sx = e.x + Mathf.cosDeg(ang) * spd * e.finpow();
            float sy = e.y + Mathf.sinDeg(ang) * spd * e.finpow() + e.fin() * 10f;

            color(Pal.turretHeat, Color.darkGray, Mathf.clamp(e.fin() * 2.2f));
            alpha(0.6f * e.fout());
            Fill.circle(sx, sy, (2.2f + e.fin() * 5.5f) * Math.min(scale, 2.2f));
        }
        // 4. Разлетающиеся вращающиеся обломки и искры
        int debrisCount = Mathf.clamp((int)(8 * scale), 6, 20);
        for (int i = 0; i < debrisCount; i++) {
            float ang = Mathf.randomSeed(e.id + i + 50, 360f);
            float dist = Mathf.randomSeed(e.id + i + 80, 12f, 46f) * scale * e.finpow();
            float dx = e.x + Mathf.cosDeg(ang) * dist;
            float dy = e.y + Mathf.sinDeg(ang) * dist;

            color(Pal.lightOrange, Color.darkGray, e.fin());
            alpha(e.fout());
            Fill.rect(dx, dy, 3f * e.fout(), 1.8f * e.fout(), ang + e.fin() * 360f);
        }
    }).layer(Layer.effect + 1f);

    public static void onUnitDestroy(UnitDestroyEvent event) {
        if (!RamSettings.enableExplosions || event.unit == null) return;
        float size = event.unit.hitSize;
        realisticExplosion.at(event.unit.x, event.unit.y, size);
        if (RamSettings.enableScreenShake && size >= 16f) {
            Effect.shake(size * 0.12f, size * 0.6f, event.unit.x, event.unit.y);
        }
    }

    public static void onBlockDestroy(BlockDestroyEvent event) {
        if (!RamSettings.enableExplosions || event.tile == null) return;
        float size = event.tile.block().size * 8f;
        realisticExplosion.at(event.tile.worldx(), event.tile.worldy(), size);
        if (RamSettings.enableScreenShake && size >= 16f) {
            Effect.shake(size * 0.1f, size * 0.5f, event.tile.worldx(), event.tile.worldy());
        }
    }
}
