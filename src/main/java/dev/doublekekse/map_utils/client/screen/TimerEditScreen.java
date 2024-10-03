package dev.doublekekse.map_utils.client.screen;

import dev.doublekekse.map_utils.block.timer.TimerBlockEntity;
import dev.doublekekse.map_utils.packet.SetTimerBlockPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TimerEditScreen extends Screen {
    int duration;
    int interval;
    boolean tickUnloaded;

    EditBox durationInput;
    EditBox intervalInput;
    Checkbox tickUnloadedCheckbox;

    TimerBlockEntity blockEntity;

    public TimerEditScreen(TimerBlockEntity timerBlockEntity) {
        super(Component.translatable("gui.map_utils.screen.timer_edit"));

        duration = timerBlockEntity.duration;
        interval = timerBlockEntity.interval;
        tickUnloaded = timerBlockEntity.tickUnloaded;

        blockEntity = timerBlockEntity;
    }

    void sendToServer() {
        ClientPlayNetworking.send(new SetTimerBlockPacket(
            blockEntity.getBlockPos(),
            parseAsInt(durationInput.getValue()),
            parseAsInt(intervalInput.getValue()),
            tickUnloadedCheckbox.selected()
        ));
    }

    private int parseAsInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void onClose() {
        super.onClose();

        sendToServer();
    }

    @Override
    protected void init() {
        super.init();


        assert minecraft != null;

        durationInput = new EditBox(minecraft.font, this.width / 2 - 100, 20, 200, 20, Component.translatable("gui.map_utils.timer.duration"));
        durationInput.setValue(String.valueOf(duration));
        intervalInput = new EditBox(minecraft.font, this.width / 2 - 100, 60, 200, 20, Component.translatable("gui.map_utils.timer.interval"));
        intervalInput.setValue(String.valueOf(interval));
        tickUnloadedCheckbox = Checkbox.builder(Component.translatable("gui.map_utils.timer.tick_unloaded"), minecraft.font).pos(this.width / 2 - 100, 100).selected(tickUnloaded).build();
        tickUnloadedCheckbox.setTooltip(Tooltip.create(Component.translatable("gui.map_utils.timer.tick_unloaded.tooltip")));

        addRenderableWidget(durationInput);
        addRenderableWidget(intervalInput);
        addRenderableWidget(tickUnloadedCheckbox);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float pTicks) {
        super.render(graphics, x, y, pTicks);

        graphics.drawString(this.font, Component.translatable("gui.map_utils.timer.duration.label"), this.width / 2 - 100, 10, 10526880);
        graphics.drawString(this.font, Component.translatable("gui.map_utils.timer.interval.label"), this.width / 2 - 100, 50, 10526880);
        graphics.drawString(this.font, Component.translatable("gui.map_utils.timer.tick_unloaded.label"), this.width / 2 - 100, 90, 10526880);
    }
}
