package ram.ui;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.event.Touchable;
import mindustry.Vars;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.graphics.Pal;

public class SectorDropOverlay extends Element {
    private static SectorDropOverlay instance;
    private float timer = 0f;
    private float startScale = 1.6f;

    public SectorDropOverlay() {
        touchable = Touchable.disabled;
        setFillParent(true);
    }

    public static void init() {
        Events.on(WorldLoadEvent.class, e -> {
            if (Vars.headless) return;
            Core.app.post(() -> {
                if (instance == null) {
                    instance = new SectorDropOverlay();
                    Core.scene.add(instance);
                }
                instance.timer = 0f;
                instance.visible = true;
                instance.toFront();
                if (Vars.renderer != null) {
                    instance.startScale = Vars.renderer.getScale() * 1.45f;
                }
            });
        });
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        if (!visible) return;
        toFront();
        timer += 1f;

        float progress = Mathf.clamp(timer / 95f);
        float ease = Interp.pow3Out.apply(progress);

        // Плавный орбитальный подлёт камеры при высадке на сектор
        if (Vars.renderer != null && progress < 1f) {
            float targetScale = Vars.renderer.getDisplayScale();
            if (targetScale > 0.1f) {
                float cur = Mathf.lerp(Vars.renderer.getScale(), targetScale, 0.14f);
                Vars.renderer.setScale(cur);
            }
        }

        if (progress >= 1f) {
            visible = false;
        }
    }

    @Override
    public void draw() {
        if (!visible) return;
        float progress = Mathf.clamp(timer / 95f);
        float alpha = 1f - Interp.pow2In.apply(progress);
        float expand = Interp.pow3Out.apply(progress);

        float w = width, h = height;
        float cx = x + w / 2f, cy = y + h / 2f;

        // Плавное рассеивание атмосферной дымки
        Draw.color(0.03f, 0.04f, 0.06f, alpha * 0.85f);
        Fill.rect(cx, cy, w, h);

        // Расходящиеся орбитальные кольца сканирования сектора
        float maxR = Math.max(w, h) * 0.55f;
        Draw.color(Pal.accent, alpha * 0.8f);
        Lines.stroke(2.5f * alpha);
        Lines.circle(cx, cy, 24f + maxR * expand);

        Draw.color(Color.white, alpha * 0.45f);
        Lines.stroke(1.2f * alpha);
        Lines.circle(cx, cy, 12f + maxR * 0.65f * expand);

        // Горизонтальный луч развёртки
        float lineW = w * 0.6f * (1f - progress * 0.5f);
        Draw.color(Pal.accent, alpha * 0.65f);
        Fill.rect(cx, cy, lineW, 2f);
        Draw.color();
    }
}
