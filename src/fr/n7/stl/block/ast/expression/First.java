/**
 * 
 */
package fr.n7.stl.block.ast.expression;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.block.ast.type.CoupleType;
import fr.n7.stl.util.Logger;

/**
 * Abstract Syntax Tree node for an expression extracting the first component in a couple.
 * @author Marc Pantel
 *
 */
public class First implements Expression {

	/**
	 * AST node for the expression whose value must whose first element is extracted by the expression.
	 */
	protected Expression target;

	/**
	 * Builds an Abstract Syntax Tree node for an expression extracting the first component of a couple.
	 * @param _target : AST node for the expression whose value must whose first element is extracted by the expression.
	 */
	public First(Expression _target) {
		this.target = _target;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(fst " + this.target + ")";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean collect(HierarchicalScope<Declaration> _scope) {
		return target.collect(_scope);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return target.resolve(_scope);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		if (this.target.getType() instanceof CoupleType) {
			return ((CoupleType)this.target.getType()).getFirst();
		} else {
			Logger.error(this.target +" is not a couple type.");
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		frag.append(target.getCode(_factory));
		frag.addComment("fst");
		frag.add(_factory.createPop(0, 1));
		return frag;
	}

}
