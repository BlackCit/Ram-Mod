package ram.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Interp;
import arc.math.Mathf;
import arc.scene.Element;
import arc.scene.event.Touchable;
import mindustry.graphics.Pal;

public class MenuTransitionOverlay extends Element {
    private static MenuTransitionOverlay instance;
    private float progress = 0f;
    private boolean closing = false;
    private Runnable midAction;

    public MenuTransitionOverlay() {
        touchable = Touchable.disabled;
        setFillParent(true);
    }

    public static void play(Runnable action) {
        if (!ram.core.RamSettings.enableMenuAnim) { if (action != null) action.run(); return; }
        if (instance == null) {
            instance = new MenuTransitionOverlay();
            Core.scene.add(instance);
        }
        instance.midAction = action;
        instance.progress = 0f;
        instance.closing = true;
        instance.visible = true;
        instance.toFront();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!visible) return;
        toFront();
        if (closing) {
            progress += 0.085f;
            if (progress >= 1f) {
                progress = 1f;
                closing = false;
                if (midAction != null) {
                    midAction.run();
                    midAction = null;
                }
            }
        } else {
            progress -= 0.065f;
            if (progress <= 0f) {
                progress = 0f;
                visible = false;
            }
        }
    }

    @Override
    public void draw() {
        if (!visible || progress <= 0.001f) return;
        float alpha = Interp.pow2Out.apply(progress);
        float cx = x + width / 2f, cy = y + height / 2f;

        Draw.color(0.04f, 0.05f, 0.07f, alpha * 0.92f);
        Fill.rect(cx, cy, width, height);

        float barW = Math.min(width * 0.45f, 340f) * alpha;
        Draw.color(Pal.accent, alpha);
        Fill.rect(cx, cy, barW, 3f);

        Lines.stroke(2f);
        Lines.circle(cx, cy, 18f * alpha);
        Draw.color();
    }
}
