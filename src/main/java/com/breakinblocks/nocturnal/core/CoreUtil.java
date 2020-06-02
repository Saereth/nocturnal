package com.breakinblocks.nocturnal.core;

import com.google.common.collect.ImmutableSet;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class CoreUtil {
	private static final ImmutableSet<String> PRIMATIVE_DESCRIPTORS = new ImmutableSet.Builder<String>()
			.add("V", "Z", "C", "B", "S", "I", "F", "J", "D")
			.build();

	public static MethodNode getMethodNode(ClassNode cn, String name, String desc) {
		return cn.methods.stream()
				.filter((m) -> m.name.equals(name) && m.desc.equals(desc))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("Method with name " + name + " desc " + desc + " not found"));
	}

	public static boolean isLabelOrLine(AbstractInsnNode n) {
		return n.getType() == AbstractInsnNode.LABEL || n.getType() == AbstractInsnNode.LINE;
	}

	public static boolean isNotLabelOrLine(AbstractInsnNode n) {
		return !isLabelOrLine(n);
	}

	public static String typeToPath(String typeName) {
		return typeName.replace('.', '/');
	}

	private static String typeToDescriptor(String typeName) {
		if (PRIMATIVE_DESCRIPTORS.contains(typeName)) return typeName;
		return "L" + typeToPath(typeName) + ";";
	}

	public static String createMethodDescriptor(String returnType, String... paramTypes) {
		return "(" +
				Arrays.stream(paramTypes).map(CoreUtil::typeToDescriptor).collect(Collectors.joining()) +
				")" + typeToDescriptor(returnType);
	}
}
