package dev.doublekekse.map_utils.client.screen;

import dev.doublekekse.map_utils.block.annotation.AnnotationBlockEntity;
import dev.doublekekse.map_utils.packet.ServerboundUpdateAnnotationPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AnnotationEditScreen extends Screen {
    String text;
    AnnotationBlockEntity annotation;
    MultiLineEditBox editBox;

    public AnnotationEditScreen(AnnotationBlockEntity annotation) {
        super(Component.translatable("gui.map_utils.screen.annotation_edit"));

        this.annotation = annotation;
        text = annotation.getText();
    }

    @Override
    protected void init() {
        this.minecraft.textInputManager().startTextInput(this);

        if (editBox != null) {
            text = editBox.getValue();
        }

        editBox = new MultiLineEditBox.Builder()
            .setY(70)
            .setX(20)
            .build(font, width - 40, height / 4 + 74 - 40, Component.empty());
        editBox.setValue(text);

        this.addRenderableWidget(editBox);
        this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_DONE, _ -> this.onDone()).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build()
        );
    }

    @Override
    public void tick() {
        if (!this.isValid()) {
            this.onDone();
        }
    }


    private boolean isValid() {
        return minecraft.player != null && !annotation.isRemoved() && minecraft.player.canUseGameMasterBlocks();
    }

    @Override
    public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(this.font, this.title, width / 2, 40, -1);
    }

    @Override
    public void onClose() {
        this.onDone();
    }

    @Override
    public void removed() {
        text = editBox.getValue();
        ClientPlayNetworking.send(new ServerboundUpdateAnnotationPacket(annotation.getBlockPos(), text));

        this.minecraft.textInputManager().stopTextInput(this);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    private void onDone() {
        this.minecraft.gui.setScreen(null);
    }
}
