package com.breakinblocks.nocturnal.core;

import com.breakinblocks.nocturnal.core.modules.Thaumcraft;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

public class NocturnalTransformer implements IClassTransformer {

	public static final ImmutableSet<NocturnalCoreModuleBase> transformerModules = ImmutableSet.of(
			new Thaumcraft()
	);

	public final ImmutableMultimap<String, Consumer<ClassNode>> transformers = collectTransformers();

	/**
	 * Loads all the transformers from the transformer modules
	 *
	 * @return Mapping Classes -> Transformers that operate on them
	 */
	private ImmutableMultimap<String, Consumer<ClassNode>> collectTransformers() {
		ImmutableMultimap.Builder<String, Consumer<ClassNode>> build = ImmutableMultimap.builder();
		transformerModules.stream().forEach(module -> Arrays.stream(module.getClass().getDeclaredMethods())
				.filter(m -> m.isAnnotationPresent(Transformer.class))
				.forEach(m -> {
					final Class<?>[] parameterTypes = m.getParameterTypes();
					if (parameterTypes.length != 1 || !ClassNode.class.isAssignableFrom(parameterTypes[0]))
						throw new RuntimeException("Method '" + m + "' should only have a single ClassNode parameter");
					final String className = m.getAnnotation(Transformer.class).transformedName();
					final NocturnalCoreModuleBase instance = Modifier.isStatic(m.getModifiers()) ? null : module;
					final Consumer<ClassNode> transformer = (classNode) -> {
						try {
							NocturnalCore.log.info("Transforming " + className + " using " + m);
							m.invoke(instance, classNode);
						} catch (IllegalAccessException | InvocationTargetException e) {
							throw new RuntimeException("Error transforming " + className + " using " + m, e);
						}
					};
					build.put(className, transformer);
				}));
		return build.build();
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (!transformers.containsKey(transformedName)) return basicClass;

		final ClassNode node = new ClassNode();
		final ClassReader reader = new ClassReader(basicClass);
		reader.accept(node, 0);

		transformers.get(transformedName).forEach(transformer -> transformer.accept(node));

		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		node.accept(writer);
		final byte[] outputClass = writer.toByteArray();
		if (!NocturnalCore.OBFUSCATED) {
			final File folder = new File("./NocturnalTransformer/");
			if (folder.exists() || folder.mkdirs()) {
				final File file = new File(folder, transformedName + ".class");
				try (final FileOutputStream fileOutputStream = new FileOutputStream(file)) {
					fileOutputStream.write(outputClass);
				} catch (IOException e) {
					throw new RuntimeException("Failed to save: " + file, e);
				}
			} else {
				throw new RuntimeException("Could not create all folder(s): " + folder);
			}
		}
		return outputClass;
	}
}
