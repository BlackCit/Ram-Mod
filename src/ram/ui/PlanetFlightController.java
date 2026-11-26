package ram.ui;

import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import mindustry.Vars;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;

public class PlanetFlightController {
    private static boolean wasShown = false;
    private static Planet lastPlanet = null;
    private static final Vec3 startPos = new Vec3();
    private static final Vec3 targetPos = new Vec3();
    private static float flightTimer = 0f;
    private static float flightDuration = 75f;
    private static boolean flying = false;
    private static boolean isEntryZoom = false;

    public static void update() {
        if (!ram.core.RamSettings.enablePlanetFlight) return;
        if (Vars.ui == null || Vars.ui.planet == null) return;
        boolean shown = Vars.ui.planet.isShown();

        if (!shown) {
            wasShown = false;
            flying = false;
            return;
        }

        PlanetParams state = Vars.ui.planet.state;
        if (state == null || state.planet == null) return;

        if (!wasShown) {
            wasShown = true;
            lastPlanet = state.planet;
            flying = true;
            isEntryZoom = true;
            flightTimer = 0f;
            flightDuration = 65f;
            PlanetScanOverlay.play();
        }

        if (lastPlanet != null && state.planet != lastPlanet) {
            flying = true;
            isEntryZoom = false;
            flightTimer = 0f;
            flightDuration = 80f;

            startPos.set(lastPlanet.position).sub(state.planet.position);
            if (startPos.len() < 0.001f) startPos.set(1f, 0.3f, 0f);
            startPos.nor();

            targetPos.set(state.camPos).nor();
            if (targetPos.len() < 0.001f) targetPos.set(0f, 0.2f, 1f).nor();

            lastPlanet = state.planet;
            PlanetScanOverlay.play();
        }

        if (flying) {
            stepFlight(state);
        }
    }
    private static void stepFlight(PlanetParams state) {
        flightTimer += 1f;
        float t = Mathf.clamp(flightTimer / flightDuration);
        float ease = Interp.smoother.apply(t);

        if (isEntryZoom) {
            float desired = Mathf.lerp(3.2f, 1.0f, Interp.pow3Out.apply(t));
            state.zoom = Mathf.lerp(state.zoom, desired, 0.25f);
        } else {
            float arcLift = Mathf.sin(t * Mathf.pi) * 1.8f;
            float desiredZoom = 1.0f + arcLift;
            state.zoom = Mathf.lerp(state.zoom, desiredZoom, 0.22f);

            if (t < 0.85f) {
                float len = state.camPos.len();
                if (len < 0.01f) len = 1f;
                state.camPos.lerp(targetPos, 0.14f).nor().scl(len);
            }
        }

        if (t >= 1f) {
            state.zoom = 1.0f;
            flying = false;
        }
    }
}
