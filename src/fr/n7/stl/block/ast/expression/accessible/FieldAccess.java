/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;

import java.util.List;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractField;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.instruction.declaration.TypeDeclaration;
import fr.n7.stl.block.ast.type.NamedType;
import fr.n7.stl.block.ast.type.RecordType;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.block.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Implementation of the Abstract Syntax Tree node for accessing a field in a record.
 * @author Marc Pantel
 *
 */
public class FieldAccess extends AbstractField implements Expression {

	/**
	 * Construction for the implementation of a record field access expression Abstract Syntax Tree node.
	 * @param _record Abstract Syntax Tree for the record part in a record field access expression.
	 * @param _name Name of the field in the record field access expression.
	 */
	public FieldAccess(Expression _record, String _name) {
		super(_record, _name);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		
		Type target = this.record.getType();
		while (target instanceof NamedType) {
			target = ((NamedType) target).getType(); 
		}
		int i = 0;
		List<FieldDeclaration> l = ((RecordType) target).getFields();
		//Recherche de l'indice correspondant
		for(int k=0; k < l.size(); k++) {
			if (l.get(k).getName().equals(this.name)) {
				i = k;
			}
		}
		//Longueur du field
		int longueur = l.get(i).getType().length();
		
		//Avant le field
		int avant = 0;
		for(int k=0; k<i ; k++) {
			avant+= l.get(k).getType().length();
		}
		
		//Apres le field
		int apres = 0;
		for(int k=i+1; k<l.size() ; k++) {
			apres+= l.get(k).getType().length();
		}
		Fragment fragment = _factory.createFragment();
        fragment.append(record.getCode(_factory));
        fragment.add(_factory.createPop(0, apres));
        fragment.add(_factory.createPop(longueur, avant));
        return fragment;
		
	}

}

