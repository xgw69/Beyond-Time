package com.beyondtime.content.microbe;

import com.beyondtime.BeyondTime;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * One kind of microbe.
 *
 * <p>Microbes are pure data (rule R3): they are not entities, they have no block and they are never
 * drawn in the world. They only appear as an icon and a percentage on the microscope screen.
 *
 * <p>Microbes are not a registry yet. Eleven of them are pinned down in {@link Microbes}, and each
 * one is just an id, a language key and a 32x32 icon, so adding one later is a one-line change.
 *
 * @param id the id of this microbe, e.g. {@code beyondtime:ancient_water}
 * @param appearance the microbe this one is indistinguishable from, or {@code null} when it always
 *     looks like itself. {@code false_ancient_water} uses this: early on the microscope reports it as
 *     {@code ancient_water}, and the journal only later reveals that there are two of them.
 */
public record Microbe(Identifier id, @Nullable Microbe appearance) {
    /** {@return a microbe with the given path in this mod's namespace, looking like itself} */
    public static Microbe of(String path) {
        return new Microbe(Identifier.fromNamespaceAndPath(BeyondTime.MODID, path), null);
    }

    /** {@return this microbe, but reported as {@code other} until the journal reveals it} */
    public Microbe appearsAs(Microbe other) {
        return new Microbe(this.id, other);
    }

    /** {@return what the microscope should show for this microbe} */
    public Microbe visibleAs() {
        return this.appearance == null ? this : this.appearance;
    }

    /** {@return the translation key of this microbe's name} */
    public String nameKey() {
        return "microbe." + this.id.getNamespace() + "." + this.id.getPath();
    }

    /** {@return the icon of this microbe, relative to {@code assets/beyondtime/}} */
    public Identifier icon() {
        return Identifier.fromNamespaceAndPath(this.id.getNamespace(), "textures/microbe/" + this.id.getPath() + ".png");
    }
}
