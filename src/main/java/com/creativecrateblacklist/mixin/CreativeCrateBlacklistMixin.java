package com.creativecrateblacklist.mixin;

import com.simibubi.create.content.logistics.crate.BottomlessItemHandler;
import com.creativecrateblacklist.config.BlacklistConfig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BottomlessItemHandler.class)
public class CreativeCrateBlacklistMixin {

    @Inject(
            method = "isItemValid",
            at = @At("HEAD"),
            cancellable = true
    )
    private void CreativeCrateBlacklist$checkItem(
            int slot,
            ItemStack stack,
            CallbackInfoReturnable<Boolean> cir
    ) {

        if (BlacklistConfig.isBlacklisted(stack)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "insertItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void CreativeCrateBlacklist$preventInsertion(
            int slot,
            ItemStack stack,
            boolean simulate,
            CallbackInfoReturnable<ItemStack> cir
    ) {

        if (BlacklistConfig.isBlacklisted(stack)) {
            cir.setReturnValue(stack);
        }
    }
}