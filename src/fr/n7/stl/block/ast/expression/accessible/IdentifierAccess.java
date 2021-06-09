/**
 * 
 */
package fr.n7.stl.block.ast.expression.accessible;

import fr.n7.stl.block.ast.expression.AbstractIdentifier;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.AbstractAccess;
import fr.n7.stl.block.ast.instruction.declaration.ConstantDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.type.declaration.LabelDeclaration;
import fr.n7.stl.miniJava.declaration.ClasseDeclaration;
import fr.n7.stl.miniJava.definition.Attribut;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for an identifier access expression (can be a variable,
 * a parameter, a constant, a function, ...).
 * Will be connected to an appropriate object after resolving the identifier to know to which kind of element
 * it is associated (variable, parameter, constant, function, ...).
 * @author Marc Pantel
 * TODO : Should also hold a function and not only a variable.
 */
public class IdentifierAccess extends AbstractIdentifier implements AccessibleExpression {
	private Declaration declaration;
	protected AbstractAccess expression;
	protected Type type; //used only for enumeration type
	
	protected Attribut attribut;

	public Attribut getAttribut() {
		return attribut;
	}

	public void setAttribut(Attribut attribut) {
		this.attribut = attribut;
	}
	
	/**
	 * Creates a variable use expression Abstract Syntax Tree node.
	 * @param _name Name of the used variable.
	 */
	public IdentifierAccess(String _name) {
		super(_name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override 
	public String toString() {
		return this.name;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean collect(HierarchicalScope<Declaration> _scope) {
		/* This is the backward resolve part. */
		//System.out.println("IdentifierAccess : collect, " + this.name + " known ? " + ((HierarchicalScope<Declaration>)_scope).knows(this.name));
		if (((HierarchicalScope<Declaration>)_scope).knows(this.name)) {
			this.declaration = _scope.get(this.name);
			//System.out.println(this.declaration.getType());
			
			/* These kinds are handled by partial resolve. */
			if (declaration instanceof VariableDeclaration) {
				
				this.expression = new VariableAccess((VariableDeclaration) declaration);
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		/* This is the full resolve part that complements the backward resolve. */
		/* If the resolution has not been done by the backward resolve */
		//System.out.println("IdentifierAccess: resolve, " + this.expression.toString());
		if  (this.expression == null) {
			if (((HierarchicalScope<Declaration>)_scope).knows(this.name)) {
				Declaration _declaration = _scope.get(this.name);
				/* This kind should have been handled by partial resolve. */
				if (_declaration instanceof VariableDeclaration) {
					throw new SemanticsUndefinedException( "Collect and partial resolve have probably not been implemented correctly. The identifier " + this.name + " should have not been resolved previously.");
				} else {
					/* These kinds are handled by full resolve. */
					if (_declaration instanceof ConstantDeclaration) { 
						// TODO : refactor the management of Constants
						this.expression = new ConstantAccess((ConstantDeclaration) _declaration);
						return true;
					} else if  (_declaration instanceof ParameterDeclaration){
	                    this.expression = new ParameterAccess((ParameterDeclaration) _declaration);
					    return true;
					} else if  (_declaration instanceof LabelDeclaration){
						this.type = ((LabelDeclaration)_declaration).getType();
					    return true;
					} else {
						Logger.error("The declaration for " + this.name + " is of the wrong kind.");
						return false;
	                }
				}
			} else {
				Logger.error("The identifier " + this.name + " has not been found.");
				return false;	
			}
		} else {  /* The resolution has been done previously */
			return true;
		}
	}
	public Declaration getDeclaration() {
		return this.declaration;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		//System.out.println("IdentifierAccess: getType, " + this.declaration);
		if (this.expression != null) {
			return this.expression.getType();
		} else if (this.type != null){
			return this.type;
		}else {
			System.out.println("Identifieraccess: getType, " + this.declaration);
			if (this.declaration instanceof ClasseDeclaration)
				System.out.println("yes");
			return null;
			//throw new SemanticsUndefinedException( "Collect and Resolve have probably not been implemented correctly. The identifier " + this.name + " has not been resolved.");
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		return this.expression.getCode(_factory);
	}

}
