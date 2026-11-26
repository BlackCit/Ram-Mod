package ram.ui;

import arc.input.KeyCode;
import arc.math.Interp;
import arc.scene.Element;
import arc.scene.actions.Actions;
import arc.scene.event.ClickListener;
import arc.scene.event.InputEvent;
import arc.scene.ui.Button;
import arc.util.Align;
import ram.core.RamSettings;

public class ButtonAnimListener extends ClickListener {
    public static Button activeBtn = null;
    public static float glowAlpha = 0f;
    private final Button target;

    public ButtonAnimListener(Button target) {
        this.target = target;
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Element fromActor) {
        super.enter(event, x, y, pointer, fromActor);
        if (!RamSettings.enableButtons || target.isDisabled()) return;
        activeBtn = target;
        target.setTransform(true);
        target.setOrigin(Align.center);
        target.clearActions();
        target.addAction(Actions.scaleTo(1.04f, 1.04f, 0.14f, Interp.pow3Out));
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Element toActor) {
        super.exit(event, x, y, pointer, toActor);
        if (activeBtn == target) activeBtn = null;
        target.clearActions();
        target.addAction(Actions.scaleTo(1f, 1f, 0.16f, Interp.pow2Out));
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
        boolean res = super.touchDown(event, x, y, pointer, button);
        if (RamSettings.enableButtons && !target.isDisabled()) {
            activeBtn = target;
            glowAlpha = 1f;
            target.setTransform(true);
            target.setOrigin(Align.center);
            target.clearActions();
            target.addAction(Actions.scaleTo(0.93f, 0.93f, 0.08f, Interp.pow2Out));
        }
        return res;
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
        super.touchUp(event, x, y, pointer, button);
        target.clearActions();
        target.addAction(Actions.scaleTo(1f, 1f, 0.22f, Interp.swingOut));
    }
}
