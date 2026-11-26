package ram.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.event.Touchable;
import mindustry.Vars;
import mindustry.graphics.Pal;

public class PlanetScanOverlay extends Element {
    private static PlanetScanOverlay instance;
    private float timer = 0f;

    public PlanetScanOverlay() {
        touchable = Touchable.disabled;
        setFillParent(true);
    }

    public static void play() {
        if (instance == null) {
            instance = new PlanetScanOverlay();
            Core.scene.add(instance);
        }
        instance.timer = 0f;
        instance.visible = true;
        instance.toFront();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!visible) return;
        toFront();
        timer += 1f;

        float progress = Mathf.clamp(timer / 80f);
        float ease = Interp.pow3Out.apply(progress);

        if (Vars.ui != null && Vars.ui.planet != null && Vars.ui.planet.state != null) {
            Vars.ui.planet.state.zoom = Mathf.lerp(4.2f, 1.15f, ease);
        }

        if (progress >= 1f) {
            visible = false;
        }
    }
    @Override
    public void draw() {
        if (!visible) return;
        float progress = Mathf.clamp(timer / 80f);
        float alpha = 1f - Interp.pow2In.apply(progress);
        float expand = Interp.pow3Out.apply(progress);

        float w = width, h = height;
        float cx = x + w / 2f, cy = y + h / 2f;

        Draw.color(0.02f, 0.03f, 0.06f, alpha * 0.85f);
        Fill.rect(cx, cy, w, h);

        float baseR = Math.min(w, h) * 0.34f;
        Draw.color(Pal.accent, alpha * 0.75f);
        Lines.stroke(2f);
        Lines.circle(cx, cy, baseR * (0.4f + 0.6f * expand));

        for (int i = 0; i < 3; i++) {
            Lines.arc(cx, cy, baseR * 1.12f, 0.18f, timer * 3f + i * 120f);
        }

        Draw.color(Color.white, alpha * 0.4f);
        Lines.stroke(1f);
        Lines.line(cx - baseR * 1.3f, cy, cx + baseR * 1.3f, cy);
        Lines.line(cx, cy - baseR * 1.3f, cx, cy + baseR * 1.3f);
        Draw.color();
    }
}
