package ram.ui;

import arc.Events;
import arc.math.Interp;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.actions.Actions;
import arc.scene.ui.Button;
import arc.scene.ui.Dialog;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType.*;

public class MenuHooker {
    public static void init() {
        Events.on(ClientLoadEvent.class, e -> {
            Dialog.setShowAction(() -> Actions.sequence(
                Actions.alpha(0f),
                Actions.scaleTo(0.92f, 0.92f),
                Actions.parallel(
                    Actions.fadeIn(0.28f, Interp.pow3Out),
                    Actions.scaleTo(1f, 1f, 0.28f, Interp.pow3Out)
                )
            ));
            Dialog.setHideAction(() -> Actions.sequence(
                Actions.parallel(
                    Actions.fadeOut(0.22f, Interp.pow2In),
                    Actions.scaleTo(0.94f, 0.94f, 0.22f, Interp.pow2In)
                )
            ));

            // Плавно оборачиваем окно выбора планет (PlanetDialog)
            if (Vars.ui.planet != null) {
                Vars.ui.planet.shown(() -> MenuTransitionOverlay.play(null));
                hookGroup(Vars.ui.planet);
            }

            Time.runTask(10f, () -> hookGroup(Vars.ui.menuGroup));
        });

        Events.on(ResizeEvent.class, e -> {
            Time.runTask(5f, () -> {
                hookGroup(Vars.ui.menuGroup);
                if (Vars.ui.planet != null) hookGroup(Vars.ui.planet);
            });
        });
    }

    private static void hookGroup(Group group) {
        if (group == null) return;
        for (Element child : group.getChildren()) {
            if (child instanceof Button) {
                Button btn = (Button) child;
                if (btn.name == null || !btn.name.equals("ram-hooked")) {
                    btn.name = "ram-hooked";
                    btn.changed(() -> MenuTransitionOverlay.play(null));
                }
            } else if (child instanceof Group) {
                hookGroup((Group) child);
            }
        }
    }
}
