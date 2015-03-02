package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerEntityEffect extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_EFFECT;
    
    public WrapperPlayServerEntityEffect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerEntityEffect(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     * @return The current Entity ID
     */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Entity ID.
     * @param value - new value.
     */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Effect ID.
     * <p>
     * Notes: see [[1]]
     * @return The current Effect ID
     */
    public byte getEffectId() {
        return handle.getBytes().read(0);
    }
    
    /**
     * Set Effect ID.
     * @param value - new value.
     */
    public void setEffectId(byte value) {
        handle.getBytes().write(0, value);
    }
    
    /**
     * Retrieve Amplifier.
     * @return The current Amplifier
     */
    public byte getAmplifier() {
        return handle.getBytes().read(1);
    }
    
    /**
     * Set Amplifier.
     * @param value - new value.
     */
    public void setAmplifier(byte value) {
        handle.getBytes().write(1, value);
    }
    
    /**
     * Retrieve Duration.
     * @return The current Duration
     */
    public int getDuration() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Duration.
     * @param value - new value.
     */
    public void setDuration(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Hide Particles.
     * @return The current Hide Particles
     */
    public boolean getHideParticles() {
        return handle.getBooleans().read(0);
    }
    
    /**
     * Set Hide Particles.
     * @param value - new value.
     */
    public void setHideParticles(boolean value) {
        handle.getBooleans().write(0, value);
    }
    
}