package ram.ui;

import arc.Core;
import arc.Events;
import arc.scene.Element;
import arc.scene.event.Touchable;
import mindustry.game.EventType.ClientLoadEvent;

public class HackerUiOverlay extends Element {
    public HackerUiOverlay() {
        touchable = Touchable.disabled;
        setFillParent(true);
    }

    public static void init() {
        Events.on(ClientLoadEvent.class, e -> {
            Core.app.post(() -> Core.scene.add(new HackerUiOverlay()));
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        toFront();
    }

    @Override
    public void draw() {
        ButtonGlowRenderer.updateAndDraw();
    }
}
