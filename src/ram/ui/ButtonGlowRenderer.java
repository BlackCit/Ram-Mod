package ram.ui;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.ui.Button;
import arc.util.Time;
import ram.core.RamSettings;

public class ButtonGlowRenderer {
    private static final Color MATRIX_GREEN = Color.valueOf("00ff41");
    private static final Color DARK_GREEN = Color.valueOf("003b00");
    private static final Vec2 pos = new Vec2();
    private static int scanTick = 0;

    public static void updateAndDraw() {
        if (!RamSettings.enableButtons || Core.scene == null) return;
        if (++scanTick % 20 == 0) attachRecursive(Core.scene.root);

        ButtonAnimListener.glowAlpha = Mathf.lerp(ButtonAnimListener.glowAlpha, 0f, 0.08f);
        drawAllButtons(Core.scene.root);
    }

    private static void drawAllButtons(Group group) {
        if (group == null || !group.visible) return;
        for (Element child : group.getChildren()) {
            if (child instanceof Button) {
                Button b = (Button) child;
                if (b.visible && b.getWidth() >= 18f && b.getHeight() >= 18f) {
                    drawHackerButton(b, b == ButtonAnimListener.activeBtn);
                }
            } else if (child instanceof Group) {
                drawAllButtons((Group) child);
            }
        }
    }
    private static void drawHackerButton(Button btn, boolean isActive) {
        btn.localToStageCoordinates(pos.set(0f, 0f));
        float bx = pos.x, by = pos.y, w = btn.getWidth(), h = btn.getHeight();
        float boost = isActive ? 1f : ButtonAnimListener.glowAlpha * (btn == ButtonAnimListener.activeBtn ? 1f : 0f);
        float a = 0.42f + 0.58f * boost;

        Draw.blend(Blending.additive);
        Draw.color(DARK_GREEN, 0.25f + 0.35f * boost);
        Fill.rect(bx + w / 2f, by + h / 2f, w, h);

        Draw.color(MATRIX_GREEN, a);
        Lines.stroke(isActive ? 2f : 1.2f);
        Lines.rect(bx, by, w, h);

        float c = Math.min(8f, Math.min(w, h) * 0.28f);
        Draw.color(isActive ? Color.white : MATRIX_GREEN, 0.7f + 0.3f * boost);
        Lines.stroke(1.8f);
        Lines.line(bx, by, bx, by + h);
        Lines.line(bx, by + h, bx + c, by + h);
        Lines.line(bx, by, bx + c, by);
        Lines.line(bx + w, by, bx + w, by + h);
        Lines.line(bx + w, by + h, bx + w - c, by + h);
        Lines.line(bx + w, by, bx + w - c, by);

        if (isActive) {
            float sweep = ((Time.globalTime * 0.04f) % 1f);
            Draw.color(MATRIX_GREEN, 0.45f * Mathf.sin(sweep * Mathf.pi));
            Fill.rect(bx + w * sweep, by + h / 2f, Math.min(16f, w * 0.2f), h);
        }
        Draw.blend();
        Draw.color();
    }

    private static void attachRecursive(Group group) {
        if (group == null || !group.visible) return;
        for (Element child : group.getChildren()) {
            if (child instanceof Button) {
                Button b = (Button) child;
                if (b.userObject != ButtonGlowRenderer.class) {
                    b.userObject = ButtonGlowRenderer.class;
                    b.addListener(new ButtonAnimListener(b));
                }
            } else if (child instanceof Group) {
                attachRecursive((Group) child);
            }
        }
    }
}
