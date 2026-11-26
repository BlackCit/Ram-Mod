package ram.ui;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Align;
import arc.util.Time;
import mindustry.graphics.MenuRenderer;
import mindustry.ui.Fonts;
import ram.core.RamSettings;

public class HackerMenuRenderer extends MenuRenderer {
    private static final Color GREEN = Color.valueOf("00ff41");
    private static final Color DARK_GREEN = Color.valueOf("003b00");
    private static final Color RED = Color.valueOf("ff2a2a");
    private static final String HEX = "0123456789ABCDEF#$@%&";
    private static final String[] BAR_LABELS = {"KERNEL_HOOK", "NET_BYPASS", "MEM_OVERRIDE", "SHADER_PIPE", "SYS_BREACH"};

    @Override
    public void render() {
        if (!RamSettings.enableMenuBg) {
            super.render();
            return;
        }

        float w = Core.graphics.getWidth(), h = Core.graphics.getHeight();
        float cx = w / 2f, cy = h / 2f, t = Time.globalTime;

        Draw.proj().setOrtho(0, 0, w, h);
        Draw.color(0.01f, 0.025f, 0.012f, 1f);
        Fill.rect(cx, cy, w, h);

        Draw.color(DARK_GREEN, 0.5f);
        Lines.stroke(1f);
        float grid = 48f;
        for (float x = (t * 0.35f) % grid; x < w; x += grid) Lines.line(x, 0, x, h);
        for (float y = (t * 0.25f) % grid; y < h; y += grid) Lines.line(0, y, w, y);

        Font font = Fonts.def != null ? Fonts.def : Fonts.outline;
        if (font != null) {
            font.getData().setScale(0.7f);
            for (int i = 0; i < (int)(w / 30f); i++) {
                float spd = 1.2f + Mathf.randomSeed(i, 2.2f);
                float dy = h - ((t * spd * 4.5f + Mathf.randomSeed(i + 99, h)) % (h + 140f));
                for (int k = 0; k < 6; k++) {
                    float py = dy + k * 18f;
                    if (py < 0 || py > h) continue;
                    font.setColor(k == 0 ? Color.white : GREEN);
                    font.getColor().a = (1f - k / 6f) * (k == 0 ? 0.5f : 0.22f);
                    int idx = Math.abs((int)(t * 0.12f + i * 11 + k)) % HEX.length();
                    font.draw(String.valueOf(HEX.charAt(idx)), i * 30f + 14f, py);
                }
            }
            font.getData().setScale(1f);
        }
        float rad = Math.min(w, h) * 0.27f;
        Draw.color(DARK_GREEN, 0.65f);
        Lines.stroke(2f);
        Lines.circle(cx, cy, rad);
        Draw.color(GREEN, 0.4f);
        for (int i = 0; i < 4; i++) {
            Lines.arc(cx, cy, rad * 1.08f, 0.14f, t * 0.8f + i * 90f);
        }

        // Боковые тактические панели (слева Эрекир + полосы-графики, справа Серпуло + матричный эквалайзер)
        float panelW = Math.min(w * 0.23f, h * 0.36f);
        if (panelW >= 68f) {
            float sideX = Math.max(rad * 1.32f + panelW * 0.48f, w * 0.35f);
            if (sideX + panelW * 0.52f > w * 0.485f) {
                sideX = w * 0.485f - panelW * 0.52f;
            }
            float topY = cy + panelW * 0.24f;
            float botY = cy - panelW * 0.58f;
            float graphH = panelW * 0.54f;

            drawPolarisPlanet(cx - sideX, topY, panelW, t, true, font);
            drawPolarisPlanet(cx + sideX, topY, panelW, t, false, font);

            drawTelemetryBars(cx - sideX, botY, panelW, graphH, t, font);
            drawEqualizerMatrix(cx + sideX, botY, panelW, graphH, t);
        }

        Draw.color(0f, 0f, 0f, 0.2f);
        for (float y = 0; y < h; y += 4f) Fill.rect(cx, y, w, 1.2f);
        Draw.flush();
        Draw.color();
    }
    private void drawPolarisPlanet(float bx, float by, float size, float t, boolean isErekir, Font font) {
        float half = size / 2f;
        Draw.color(0.01f, 0.03f, 0.015f, 0.9f);
        Fill.rect(bx, by, size, size);

        float strip = size * 0.16f;
        for (int c = 0; c < 4; c++) {
            float sx = bx + ((c % 2 == 0) ? -half + strip / 2f : half - strip / 2f);
            float sy = by + ((c < 2) ? half - strip / 2f : -half + strip / 2f);
            Draw.color(GREEN, 0.35f);
            Fill.rect(sx, sy, strip, strip);
            Draw.color(0.01f, 0.03f, 0.015f, 0.95f);
            Fill.rect(sx, sy, strip * 0.85f, 3f, 45f);
        }

        Draw.color(GREEN, 0.7f);
        Lines.stroke(1.5f);
        Lines.rect(bx - half, by - half, size, size);

        float pr = size * 0.34f;
        Draw.color(0.01f, 0.05f, 0.02f, 0.95f);
        Fill.circle(bx, by, pr);
        float spin = t * (isErekir ? 0.5f : -0.42f);
        int pts = 26;
        float[] px = new float[pts], py = new float[pts];
        boolean[] vis = new boolean[pts];

        for (int i = 0; i < pts; i++) {
            float lon = spin + Mathf.randomSeed(i + (isErekir ? 10 : 400), 360f);
            float lat = Mathf.randomSeed(i + (isErekir ? 60 : 460), -68f, 68f);
            vis[i] = Mathf.cosDeg(lon) > -0.05f;
            px[i] = bx + pr * 0.92f * Mathf.cosDeg(lat) * Mathf.sinDeg(lon);
            py[i] = by + pr * 0.92f * Mathf.sinDeg(lat);
        }

        Lines.stroke(1f);
        float maxDist = pr * 0.62f;
        for (int i = 0; i < pts; i++) {
            if (!vis[i]) continue;
            for (int j = i + 1; j < pts; j++) {
                if (!vis[j]) continue;
                float d = Mathf.dst(px[i], py[i], px[j], py[j]);
                if (d < maxDist) {
                    Draw.color(((i + j) % 7 == 0) ? RED : GREEN, (1f - d / maxDist) * 0.75f);
                    Lines.line(px[i], py[i], px[j], py[j]);
                }
            }
            Draw.color(i % 5 == 0 ? RED : GREEN, 0.85f);
            Fill.circle(px[i], py[i], 1.6f);
        }

        Draw.color(RED, 0.45f);
        Lines.stroke(1.2f);
        Lines.circle(bx, by, pr * 0.94f);
        Draw.color(GREEN, 0.9f);
        Lines.stroke(1.8f);
        Lines.circle(bx, by, pr);

        if (font != null) {
            Draw.color(0.01f, 0.03f, 0.015f, 0.82f);
            Fill.rect(bx, by, pr * 1.45f, 16f);
            Draw.color(GREEN, 0.65f);
            Lines.stroke(1f);
            Lines.rect(bx - pr * 0.725f, by - 8f, pr * 1.45f, 16f);
            font.getData().setScale(0.58f);
            font.setColor(Color.white);
            font.draw(isErekir ? "<< EREKIR >>" : "<< SERPULO >>", bx, by + 4f, Align.center);
            font.getData().setScale(1f);
        }
    }
    private void drawTelemetryBars(float bx, float by, float w, float h, float t, Font font) {
        float halfW = w / 2f, halfH = h / 2f;
        Draw.color(0.01f, 0.03f, 0.015f, 0.88f);
        Fill.rect(bx, by, w, h);
        Draw.color(GREEN, 0.6f);
        Lines.stroke(1.3f);
        Lines.rect(bx - halfW, by - halfH, w, h);

        int rows = BAR_LABELS.length;
        float rowH = (h - 12f) / rows;
        for (int i = 0; i < rows; i++) {
            float ry = by + halfH - 8f - i * rowH - rowH / 2f;
            float val = Mathf.clamp(0.45f + 0.45f * Mathf.sin(t * 0.04f + i * 1.7f) * Mathf.cos(t * 0.02f + i), 0.25f, 0.96f);
            Draw.color(DARK_GREEN, 0.6f);
            Fill.rect(bx, ry, w - 12f, rowH * 0.55f);
            float fillW = (w - 12f) * val;
            Draw.color((i == 1 || i == 3) ? RED : GREEN, 0.85f);
            Fill.rect(bx - halfW + 6f + fillW / 2f, ry, fillW, rowH * 0.55f);
            if (font != null) {
                font.getData().setScale(0.46f);
                font.setColor(Color.white);
                font.draw(BAR_LABELS[i], bx - halfW + 9f, ry + 3.5f, Align.left);
                font.getData().setScale(1f);
            }
        }
    }

    private void drawEqualizerMatrix(float bx, float by, float w, float h, float t) {
        float halfW = w / 2f, halfH = h / 2f;
        Draw.color(0.01f, 0.03f, 0.015f, 0.88f);
        Fill.rect(bx, by, w, h);
        Draw.color(GREEN, 0.6f);
        Lines.stroke(1.3f);
        Lines.rect(bx - halfW, by - halfH, w, h);

        int cols = 10, rows = 6;
        float cellW = (w - 12f) / cols, cellH = (h - 12f) / rows;
        for (int c = 0; c < cols; c++) {
            float wave = (Mathf.sin(t * 0.07f + c * 0.8f) + Mathf.cos(t * 0.04f - c * 1.3f) + 2f) / 4f;
            int active = Mathf.clamp(Mathf.round(wave * rows), 1, rows);
            for (int r = 0; r < rows; r++) {
                float cx2 = bx - halfW + 6f + c * cellW + cellW / 2f;
                float cy2 = by - halfH + 6f + r * cellH + cellH / 2f;
                Draw.color(r < active ? (r >= rows - 1 ? RED : GREEN) : DARK_GREEN, r < active ? 0.85f : 0.35f);
                Fill.rect(cx2, cy2, cellW - 3f, cellH - 3f);
            }
        }
    }
}
