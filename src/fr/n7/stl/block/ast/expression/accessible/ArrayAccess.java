/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;

import fr.n7.stl.block.ast.expression.AbstractArray;
import fr.n7.stl.block.ast.expression.BinaryOperator;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for accessing an array element.
 * @author Marc Pantel
 *
 */
public class ArrayAccess extends AbstractArray implements AccessibleExpression {

	/**
	 * Construction for the implementation of an array element access expression Abstract Syntax Tree node.
	 * @param _array Abstract Syntax Tree for the array part in an array element access expression.
	 * @param _index Abstract Syntax Tree for the index part in an array element access expression.
	 */
	public ArrayAccess(Expression _array, Expression _index) {
		super(_array,_index);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = this.array.getCode(_factory);
		frag.addSuffix(";valeur de l'indice");
		frag.append(this.index.getCode(_factory));
		frag.addSuffix(";Taille de " + this.getType());
		frag.add(_factory.createLoadL(this.getType().length()));
		frag.add(TAMFactory.createBinaryOperator(BinaryOperator.Multiply));
		frag.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));
		frag.add(_factory.createLoadI(1));
		return frag;
	}

}
