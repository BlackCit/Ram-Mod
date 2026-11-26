package ram.effects;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.storage.CoreBlock;
import ram.core.ShaderSettings;

public class ShadowAndGlowRenderer {

    public static void drawWorld() {
        if (!Vars.state.isGame()) return;

        // 1. Мягкие 2.5D-тени под юнитами со смещением от источника света
        if (ShaderSettings.enableShadows) {
            Draw.z(Layer.groundUnit - 0.5f);
            float op = ShaderSettings.shadowOpacity;
            int layers = ShaderSettings.qualityLevel == 2 ? 2 : 1;

            for (Unit u : Groups.unit) {
                if (!u.within(Core.camera.position, Core.camera.width * 0.7f)) continue;
                float shift = u.isFlying() ? 7.5f : 2.8f;
                float r = u.hitSize * (u.isFlying() ? 0.65f : 0.72f);

                Draw.color(0f, 0f, 0f, op);
                Fill.circle(u.x + shift, u.y - shift, r);
                if (layers == 2) {
                    Draw.color(0f, 0f, 0f, op * 0.45f);
                    Fill.circle(u.x + shift * 1.15f, u.y - shift * 1.15f, r * 1.32f);
                }
            }
        }
        // 2. Аддитивное свечение снарядов, двигателей и ядер (Pseudo-Bloom)
        if (ShaderSettings.enableGlow) {
            Draw.z(Layer.effect + 2f);
            Draw.blend(Blending.additive);
            float glow = ShaderSettings.glowIntensity;

            for (Bullet b : Groups.bullet) {
                if (!b.within(Core.camera.position, Core.camera.width * 0.65f)) continue;
                Color c = b.type.lightColor != null ? b.type.lightColor : b.team.color;
                Draw.color(c, glow * 0.55f);
                Fill.circle(b.x, b.y, b.type.hitSize * 1.8f + 4f);
            }

            if (ShaderSettings.qualityLevel >= 1) {
                for (Unit u : Groups.unit) {
                    if (!u.within(Core.camera.position, Core.camera.width * 0.65f)) continue;
                    Draw.color(u.team.color, glow * 0.35f);
                    Fill.circle(u.x, u.y, u.hitSize * 0.9f + 3f);
                }
            }

            if (ShaderSettings.qualityLevel == 2) {
                for (Building build : Groups.build) {
                    if (build.block instanceof CoreBlock || build.block instanceof Turret) {
                        if (!build.within(Core.camera.position, Core.camera.width * 0.6f)) continue;
                        Draw.color(build.team.color, glow * 0.25f);
                        Fill.circle(build.x, build.y, build.block.size * 6.5f);
                    }
                }
            }
            Draw.blend();
        }
        Draw.reset();
    }

    public static void drawScreen() {
        if (!ShaderSettings.enableVignette || !Vars.state.isGame()) return;
        float w = Core.graphics.getWidth(), h = Core.graphics.getHeight();
        float edge = Math.min(w, h) * 0.14f;

        Draw.color(0f, 0f, 0f, 0.26f);
        Fill.rect(w / 2f, edge / 2f, w, edge);
        Fill.rect(w / 2f, h - edge / 2f, w, edge);
        Fill.rect(edge / 2f, h / 2f, edge, h);
        Fill.rect(w - edge / 2f, h / 2f, edge, h);
        Draw.color();
    }
}
