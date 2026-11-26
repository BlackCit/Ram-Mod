package ram;

import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType.*;
import mindustry.mod.Mod;
import ram.core.RamSettings;
import ram.core.ShaderSettings;
import ram.effects.*;
import ram.ui.*;

public class RamMod extends Mod {
    @Override
    public void init() {
        if (Vars.headless) return;
        MenuHooker.init();
        MenuBgInjector.init();
        SectorDropOverlay.init();
        Events.on(ClientLoadEvent.class, e -> {
            RamSettings.load();
            ShaderSettings.load();
        });
        Events.on(BlockDestroyEvent.class, ExplosionAnimations::onBlockDestroy);
        Events.on(UnitDestroyEvent.class, ExplosionAnimations::onUnitDestroy);
        Events.run(Trigger.draw, ShadowAndGlowRenderer::drawWorld);
        Events.run(Trigger.uiDrawEnd, () -> {
            ShadowAndGlowRenderer.drawScreen();
            ButtonGlowRenderer.updateAndDraw();
        });
        Events.run(Trigger.update, () -> {
            PlanetFlightController.update();
            UnitAnimations.update();
            WeaponAnimations.update();
            BuildingAnimations.update();
        });
    }
}
