package com.breakinblocks.nocturnal.util.entity;

import net.minecraft.entity.Entity;

public class DistanceEntity<T extends Entity> {
	public final double dist;
	public final T ent;

	public DistanceEntity(double dist, T ent) {
		this.dist = dist;
		this.ent = ent;
	}
}
