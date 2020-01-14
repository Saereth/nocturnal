package com.breakinblocks.nocturnal.core;

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.logging.Logger;

@IFMLLoadingPlugin.Name(NocturnalCore.CORE_MOD_NAME)
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"com.breakinblocks.nocturnal.core"})
public class NocturnalCore implements IFMLLoadingPlugin, IFMLCallHook {
	public static final String CORE_MOD_NAME = "NocturnalCore";
	public static final Logger log = Logger.getLogger(CORE_MOD_NAME);

	public static boolean OBFUSCATED = true;

	@Override
	public String[] getASMTransformerClass() {
		return new String[0];
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		OBFUSCATED = (boolean) data.get("runtimeDeobfuscationEnabled");
	}

	@Override
	public String getAccessTransformerClass() {
		return "com.breakinblocks.nocturnal.core.NocturnalTransformer";
	}

	@Override
	public Void call() throws Exception {
		return null;
	}
}
