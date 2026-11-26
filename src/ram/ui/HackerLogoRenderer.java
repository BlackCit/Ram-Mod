package ram.ui;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.TextureRegion;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;

public class HackerLogoRenderer {
    public static void init() {
        Events.on(ClientLoadEvent.class, e -> {
            if (Vars.headless) return;
            TextureRegion custom = Core.atlas.find("ram-logo");
            TextureRegion vanilla = Core.atlas.find("logo");
            if (custom != null && custom.found() && vanilla != null) {
                vanilla.set(custom);
            }
        });
    }
}
