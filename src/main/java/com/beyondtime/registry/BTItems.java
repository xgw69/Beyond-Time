package com.beyondtime.registry;

import com.beyondtime.content.item.MicroscopeBlockItem;
import com.beyondtime.content.item.PetriDishItem;
import com.beyondtime.content.item.SamplerItem;
import com.beyondtime.content.item.TissueSampleItem;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Weapon;
import net.neoforged.neoforge.registries.DeferredItem;

/** Items added by Beyond-Time. */
public final class BTItems {
    /** The petri dish (P-02). Clean dishes stack to 64; a dish holding a sample does not stack. */
    public static final DeferredItem<Item> PETRI_DISH =
            BTRegistries.ITEMS.registerItem(
                    "petri_dish", PetriDishItem::new, properties -> properties.stacksTo(PetriDishItem.CLEAN_STACK_SIZE));

    /**
     * The microscope as an item.
     *
     * <p>Stack size depends on the individual stack: an empty microscope stacks to 64, one that holds
     * a dish does not stack at all.
     */
    public static final DeferredItem<MicroscopeBlockItem> MICROSCOPE = BTRegistries.ITEMS.registerItem(
            "microscope",
            properties -> new MicroscopeBlockItem(BTBlocks.MICROSCOPE.get(), properties),
            Item.Properties::useBlockDescriptionPrefix);

    /**
     * The sampler (T-01).
     *
     * <p>Three-ish damage, 64 durability, no enchantability. The {@code WEAPON} component is what
     * makes every hit cost one durability; the sixteen-point killing cost is applied by
     * {@code SamplerEvents}. Deliberately no {@code ENCHANTABLE} component, so it cannot be enchanted.
     */
    public static final DeferredItem<SamplerItem> SAMPLER = BTRegistries.ITEMS.registerItem(
            "sampler",
            SamplerItem::new,
            properties -> properties
                    .durability(SamplerItem.DURABILITY)
                    .component(DataComponents.WEAPON, new Weapon(1))
                    .attributes(ItemAttributeModifiers.builder()
                            .add(
                                    Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(
                                            Item.BASE_ATTACK_DAMAGE_ID,
                                            SamplerItem.ATTACK_DAMAGE - 1.0,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .add(
                                    Attributes.ATTACK_SPEED,
                                    new AttributeModifier(
                                            Item.BASE_ATTACK_SPEED_ID,
                                            -2.0,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build()));

    /** A sealed tissue sample (T-02). Never stacks; the payload is the {@code beyondtime:tissue} component. */
    public static final DeferredItem<TissueSampleItem> SEALED_TISSUE_SAMPLE = BTRegistries.ITEMS.registerItem(
            "sealed_tissue_sample", TissueSampleItem::new, properties -> properties.stacksTo(1));

    /** The basic bio-analyzer as an item. */
    public static final DeferredItem<net.minecraft.world.item.BlockItem> BASIC_ANALYZER =
            BTRegistries.ITEMS.registerSimpleBlockItem("basic_analyzer", BTBlocks.BASIC_ANALYZER);

    private BTItems() {}

    /** Forces this class to initialise so that the items above are declared in time. */
    public static void bootstrap() {}
}
