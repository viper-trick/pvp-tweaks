package net.minecraft.command.permission;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;

public class OrPermissionPredicate implements PermissionPredicate {
	private final ReferenceSet<PermissionPredicate> predicates = new ReferenceArraySet<>();

	OrPermissionPredicate(PermissionPredicate a, PermissionPredicate b) {
		this.predicates.add(a);
		this.predicates.add(b);
		this.validate();
	}

	private OrPermissionPredicate(ReferenceSet<PermissionPredicate> predicates, PermissionPredicate predicate) {
		this.predicates.addAll(predicates);
		this.predicates.add(predicate);
		this.validate();
	}

	private OrPermissionPredicate(ReferenceSet<PermissionPredicate> a, ReferenceSet<PermissionPredicate> b) {
		this.predicates.addAll(a);
		this.predicates.addAll(b);
		this.validate();
	}

	@Override
	public boolean hasPermission(Permission permission) {
		for (PermissionPredicate permissionPredicate : this.predicates) {
			if (permissionPredicate.hasPermission(permission)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public PermissionPredicate or(PermissionPredicate other) {
		return other instanceof OrPermissionPredicate orPermissionPredicate
			? new OrPermissionPredicate(this.predicates, orPermissionPredicate.predicates)
			: new OrPermissionPredicate(this.predicates, other);
	}

	@VisibleForTesting
	public ReferenceSet<PermissionPredicate> getPredicates() {
		return new ReferenceArraySet<>(this.predicates);
	}

	private void validate() {
		for (PermissionPredicate permissionPredicate : this.predicates) {
			if (permissionPredicate instanceof OrPermissionPredicate) {
				throw new IllegalArgumentException("Cannot have PermissionSetUnion within another PermissionSetUnion");
			}
		}
	}
}
