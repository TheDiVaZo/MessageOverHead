package me.thedivazo.messageoverhead.armorstand;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import me.thedivazo.messageoverhead.MessageOverHeadPlugin;
import me.thedivazo.messageoverhead.util.MinecraftVersion;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Client-side armor stand used to display a custom name without creating a
 * Bukkit entity on the server.
 */
public final class FakeArmorStand {

    private static final int ENTITY_FLAGS_INDEX = 0;
    private static final int CUSTOM_NAME_INDEX = 2;
    private static final int CUSTOM_NAME_VISIBLE_INDEX = 3;

    private static final byte ENTITY_FLAG_INVISIBLE = 0x20;
    private static final byte ARMOR_STAND_FLAG_SMALL = 0x01;
    private static final byte ARMOR_STAND_FLAG_NO_BASE_PLATE = 0x08;
    private static final byte ARMOR_STAND_FLAG_MARKER = 0x10;

    private final ProtocolManager protocolManager;
    private final MinecraftVersion serverVersion;
    private final ProtocolProfile protocolProfile;
    private final String message;
    private final int entityId;
    private final UUID entityUuid;
    private final Set<Player> visiblePlayers;

    private final Location location;
    private boolean small = true;
    private boolean noBasePlate = true;
    private boolean marker = true;
    private boolean invisible = true;
    private boolean destroyed;

    public FakeArmorStand(String message, Location location) {
        this(
                message,
                location,
                ProtocolLibrary.getProtocolManager(),
                MessageOverHeadPlugin.SERVER_VERSION,
                Collections.newSetFromMap(new WeakHashMap<>())
        );
    }

    FakeArmorStand(
            String message,
            Location location,
            ProtocolManager protocolManager,
            MinecraftVersion serverVersion,
            Set<Player> visiblePlayers
    ) {
        this.message = Objects.requireNonNull(message, "message");
        this.location = Objects.requireNonNull(location, "location").clone();
        this.protocolManager = Objects.requireNonNull(protocolManager, "protocolManager");
        this.serverVersion = Objects.requireNonNull(serverVersion, "serverVersion");
        this.visiblePlayers = Objects.requireNonNull(visiblePlayers, "visiblePlayers");
        this.protocolProfile = ProtocolProfile.forVersion(serverVersion);
        this.entityId = ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE);
        this.entityUuid = UUID.randomUUID();
    }

    public void show(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed || !visiblePlayers.add(player)) {
            return;
        }

        try {
            protocolManager.sendServerPacket(player, createSpawnPacket());
            protocolManager.sendServerPacket(player, createMetadataPacket());
        } catch (RuntimeException | Error exception) {
            visiblePlayers.remove(player);
            throw exception;
        }
    }

    public void updatePosition(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed || !visiblePlayers.contains(player)) {
            return;
        }

        protocolManager.sendServerPacket(player, createTeleportPacket());
    }

    public void hide(Player player) {
        Objects.requireNonNull(player, "player");
        if (destroyed || !visiblePlayers.remove(player)) {
            return;
        }

        try {
            protocolManager.sendServerPacket(player, createDestroyPacket());
        } catch (RuntimeException | Error exception) {
            visiblePlayers.add(player);
            throw exception;
        }
    }

    public void destroy() {
        if (destroyed) {
            return;
        }

        visiblePlayers.forEach(player -> {
            try {
                protocolManager.sendServerPacket(player, createDestroyPacket());
            } catch (RuntimeException | Error exception) {
                visiblePlayers.add(player);
                throw exception;
            }
        });

        destroyed = true;
        visiblePlayers.clear();
    }

    public void setPosition(double x, double y, double z) {
        if (destroyed) {
            return;
        }

        this.location.setX(x);
        this.location.setY(y);
        this.location.setZ(z);
    }

    private PacketContainer createSpawnPacket() {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, entityId);
        packet.getUUIDs().write(0, entityUuid);

        if (protocolProfile.usesEntityTypeInSpawnPacket()) {
            packet.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        } else {
            // Numeric entity type used by the pre-1.14 spawn entity packet.
            packet.getIntegers().write(6, 78);
        }

        packet.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());
        return packet;
    }

    private PacketContainer createMetadataPacket() {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entityId);

        if (protocolProfile.usesDataValues()) {
            packet.getDataValueCollectionModifier().write(0, createMetadataValues());
        } else {
            packet.getWatchableCollectionModifier().write(0, createWatchableMetadata());
        }
        return packet;
    }

    @SuppressWarnings("removal")
    private List<WrappedDataValue> createMetadataValues() {
        List<WrappedDataValue> values = new ArrayList<>(4);
        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);

        values.add(new WrappedDataValue(
                ENTITY_FLAGS_INDEX,
                byteSerializer,
                invisible ? ENTITY_FLAG_INVISIBLE : (byte) 0
        ));
        values.add(customNameDataValue());
        values.add(new WrappedDataValue(
                CUSTOM_NAME_VISIBLE_INDEX,
                booleanSerializer,
                true
        ));
        values.add(new WrappedDataValue(
                armorStandFlagsIndex(),
                byteSerializer,
                armorStandFlags()
        ));
        return values;
    }

    @SuppressWarnings("removal")
    private WrappedDataValue customNameDataValue() {
        if (protocolProfile.usesOptionalChatComponent()) {
            WrappedDataWatcher.Serializer serializer =
                    WrappedDataWatcher.Registry.getChatComponentSerializer(true);
            Optional<?> customName = Optional.of(
                    WrappedChatComponent.fromChatMessage(message)[0].getHandle()
            );
            return new WrappedDataValue(CUSTOM_NAME_INDEX, serializer, customName);
        }

        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(String.class);
        return new WrappedDataValue(CUSTOM_NAME_INDEX, serializer, message);
    }

    @SuppressWarnings("removal")
    private List<WrappedWatchableObject> createWatchableMetadata() {
        List<WrappedWatchableObject> values = new ArrayList<>(4);
        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);

        values.add(new WrappedWatchableObject(
                new WrappedDataWatcher.WrappedDataWatcherObject(ENTITY_FLAGS_INDEX, byteSerializer),
                invisible ? ENTITY_FLAG_INVISIBLE : (byte) 0
        ));
        values.add(customNameWatchableObject());
        values.add(new WrappedWatchableObject(
                new WrappedDataWatcher.WrappedDataWatcherObject(
                        CUSTOM_NAME_VISIBLE_INDEX,
                        booleanSerializer
                ),
                true
        ));
        values.add(new WrappedWatchableObject(
                new WrappedDataWatcher.WrappedDataWatcherObject(
                        armorStandFlagsIndex(),
                        byteSerializer
                ),
                armorStandFlags()
        ));
        return values;
    }

    @SuppressWarnings("removal")
    private WrappedWatchableObject customNameWatchableObject() {
        if (protocolProfile.usesOptionalChatComponent()) {
            WrappedDataWatcher.Serializer serializer =
                    WrappedDataWatcher.Registry.getChatComponentSerializer(true);
            Optional<?> customName = Optional.of(
                    WrappedChatComponent.fromChatMessage(message)[0].getHandle()
            );
            return new WrappedWatchableObject(
                    new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serializer),
                    customName
            );
        }

        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(String.class);
        return new WrappedWatchableObject(
                new WrappedDataWatcher.WrappedDataWatcherObject(CUSTOM_NAME_INDEX, serializer),
                message
        );
    }

    private PacketContainer createTeleportPacket() {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_TELEPORT);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, entityId);

        if (protocolProfile.usesPositionMoveRotation()) {
            writeModernTeleportData(packet);
            return packet;
        }

        packet.getDoubles()
                .write(0, location.getX())
                .write(1, location.getY())
                .write(2, location.getZ());
        packet.getBooleans().write(0, false);
        return packet;
    }

    /**
     * Since 1.21.2 ENTITY_TELEPORT stores position, velocity and rotation in
     * a nested PositionMoveRotation record. ProtocolLib 5.4 exposes that
     * record as an InternalStructure.
     */
    private void writeModernTeleportData(PacketContainer packet) {
        InternalStructure movement = packet.getStructures().readSafely(0);
        if (movement == null) {
            throw new IllegalStateException(
                    "ProtocolLib cannot access PositionMoveRotation on Minecraft "
                            + serverVersion
            );
        }

        movement.getVectors()
                .write(0, location.toVector())
                .write(1, new Vector());
        movement.getFloat()
                .write(0, location.getYaw())
                .write(1, location.getPitch());
        packet.getBooleans().write(0, false);
    }

    private PacketContainer createDestroyPacket() {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        if (protocolProfile.usesEntityIdListForDestroy()) {
            packet.getIntLists().write(0, Collections.singletonList(entityId));
        } else {
            packet.getIntegerArrays().write(0, new int[]{entityId});
        }
        return packet;
    }

    private int armorStandFlagsIndex() {
        return protocolProfile.armorStandFlagsIndex();
    }

    private byte armorStandFlags() {
        int flags = 0;
        if (small) {
            flags |= ARMOR_STAND_FLAG_SMALL;
        }
        if (noBasePlate) {
            flags |= ARMOR_STAND_FLAG_NO_BASE_PLATE;
        }
        if (marker) {
            flags |= ARMOR_STAND_FLAG_MARKER;
        }
        return (byte) flags;
    }

    private enum ProtocolProfile {
        V1_12(11, false, false, false, false, false),
        V1_13(11, false, true, false, false, false),
        V1_14(13, true, true, false, false, false),
        V1_15_TO_1_16(14, true, true, false, false, false),
        V1_17_TO_1_19_2(15, true, true, true, false, false),
        V1_19_3_TO_1_21_1(15, true, true, true, true, false),
        V1_21_2_PLUS(15, true, true, true, true, true);

        private final int armorStandFlagsIndex;
        private final boolean entityTypeInSpawnPacket;
        private final boolean optionalChatComponent;
        private final boolean entityIdListForDestroy;
        private final boolean dataValues;
        private final boolean positionMoveRotation;

        ProtocolProfile(
                int armorStandFlagsIndex,
                boolean entityTypeInSpawnPacket,
                boolean optionalChatComponent,
                boolean entityIdListForDestroy,
                boolean dataValues,
                boolean positionMoveRotation
        ) {
            this.armorStandFlagsIndex = armorStandFlagsIndex;
            this.entityTypeInSpawnPacket = entityTypeInSpawnPacket;
            this.optionalChatComponent = optionalChatComponent;
            this.entityIdListForDestroy = entityIdListForDestroy;
            this.dataValues = dataValues;
            this.positionMoveRotation = positionMoveRotation;
        }

        static ProtocolProfile forVersion(MinecraftVersion version) {
            if (version.getMajor() != 1
                    || version.isOlderThan(MinecraftVersion.VERSION_1_12)
                    || version.getMinor() > 21) {
                throw new IllegalArgumentException(
                        "FakeArmorStand supports Minecraft 1.12 through 1.21.x; got " + version
                );
            }
            if (version.isAtLeast(MinecraftVersion.VERSION_1_21_2)) {
                return V1_21_2_PLUS;
            }
            if (version.isAtLeast(MinecraftVersion.VERSION_1_19_3)) {
                return V1_19_3_TO_1_21_1;
            }
            if (version.isAtLeast(MinecraftVersion.VERSION_1_17)) {
                return V1_17_TO_1_19_2;
            }
            if (version.isAtLeast(MinecraftVersion.VERSION_1_15)) {
                return V1_15_TO_1_16;
            }
            if (version.isAtLeast(MinecraftVersion.VERSION_1_14)) {
                return V1_14;
            }
            if (version.isAtLeast(MinecraftVersion.VERSION_1_13)) {
                return V1_13;
            }
            return V1_12;
        }

        int armorStandFlagsIndex() {
            return armorStandFlagsIndex;
        }

        boolean usesEntityTypeInSpawnPacket() {
            return entityTypeInSpawnPacket;
        }

        boolean usesOptionalChatComponent() {
            return optionalChatComponent;
        }

        boolean usesEntityIdListForDestroy() {
            return entityIdListForDestroy;
        }

        boolean usesDataValues() {
            return dataValues;
        }

        boolean usesPositionMoveRotation() {
            return positionMoveRotation;
        }
    }

}
