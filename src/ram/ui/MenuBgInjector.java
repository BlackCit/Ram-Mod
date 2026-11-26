package ram.ui;

import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.graphics.MenuRenderer;
import mindustry.ui.fragments.MenuFragment;
import java.lang.reflect.Field;

public class MenuBgInjector {
    public static void init() {
        Events.on(ClientLoadEvent.class, e -> {
            try {
                for (Field f : MenuFragment.class.getDeclaredFields()) {
                    if (MenuRenderer.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        MenuRenderer old = (MenuRenderer) f.get(Vars.ui.menufrag);
                        if (old != null) old.dispose();
                        f.set(Vars.ui.menufrag, new HackerMenuRenderer());
                        break;
                    }
                }
            } catch (Throwable ignored) {}
        });
    }
}
