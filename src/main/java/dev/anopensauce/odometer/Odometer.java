package dev.anopensauce.odometer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class Odometer implements ModInitializer {

    public static final Identifier TINT_ID = Identifier.of("odometer","tinthandle");

    @Override
    public void onInitialize() {
        HudRenderCallbackListener.EVENT.register(new HudRenderCallbackListener());

        PayloadTypeRegistry.playS2C().register(statusUpdate.ID,statusUpdate.CODEC);

    }

    public record statusUpdate(Float value) implements CustomPayload {
        public static final CustomPayload.Id<statusUpdate> ID = new CustomPayload.Id<>(TINT_ID);
        public static final PacketCodec<RegistryByteBuf, statusUpdate> CODEC = PacketCodec.tuple(PacketCodecs.FLOAT,statusUpdate::value,statusUpdate::new);
        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

}
