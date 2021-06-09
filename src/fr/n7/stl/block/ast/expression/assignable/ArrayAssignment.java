/**
 * 
 */
package fr.n7.stl.block.ast.expression.assignable;

import fr.n7.stl.block.ast.expression.AbstractArray;
import fr.n7.stl.block.ast.expression.BinaryOperator;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for an expression whose computation assigns a cell in an array.
 * @author Marc Pantel
 */
public class ArrayAssignment extends AbstractArray implements AssignableExpression {

	/**
	 * Construction for the implementation of an array element assignment expression Abstract Syntax Tree node.
	 * @param _array Abstract Syntax Tree for the array part in an array element assignment expression.
	 * @param _index Abstract Syntax Tree for the index part in an array element assignment expression.
	 */
	public ArrayAssignment(AssignableExpression _array, Expression _index) {
		super(_array, _index);
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.impl.ArrayAccessImpl#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment frag = _factory.createFragment();
		frag.append(this.array.getCode(_factory));
		frag.addSuffix(";valeur de l'indice");
		frag.addComment("ArrayAssignement " + this.array);
		frag.append(this.index.getCode(_factory));
		frag.addSuffix(";taille de " +this.getType());
		frag.add(_factory.createLoadL(this.getType().length()));
		frag.addSuffix("; indice * taille(type élément) ");
		frag.add(TAMFactory.createBinaryOperator(BinaryOperator.Multiply));
		frag.addSuffix("; ajout à l'adresse de début du tableau");
		frag.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));
		frag.add(_factory.createStoreI(1));
		return frag;
	}

	
}
