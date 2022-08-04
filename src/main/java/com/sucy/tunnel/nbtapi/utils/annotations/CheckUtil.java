package com.sucy.tunnel.nbtapi.utils.annotations;

import java.lang.reflect.Method;

import com.sucy.tunnel.nbtapi.NbtApiException;
import com.sucy.tunnel.nbtapi.utils.MinecraftVersion;

public class CheckUtil {

	public static boolean isAvaliable(Method method) {
		if(MinecraftVersion.getVersion().getVersionId() < method.getAnnotation(AvailableSince.class).version().getVersionId())
			throw new NbtApiException("The Method '" + method.getName() + "' is only avaliable for the Versions " + method.getAnnotation(AvailableSince.class).version() + "+, but still got called!");
		return true;
	}
	
}
