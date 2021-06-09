package fr.n7.stl.miniJava.definition;

import fr.n7.stl.block.ast.expression.accessible.VariableAccess;
import fr.n7.stl.block.ast.expression.assignable.AssignableExpression;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.miniJava.declaration.ClasseDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

public class ThisAssignement implements AssignableExpression{
	ClasseDeclaration declaration;
	
	public ThisAssignement() {
		
	}

	@Override
	public boolean collect(HierarchicalScope<Declaration> _scope) {
		/* This is the backward resolve part. 
		//System.out.println("ThisAssignement : collect, ");
		if (((HierarchicalScope<Declaration>)_scope).knows(this.name)) {
			this.declaration = _scope.get(this.name);
			//System.out.println(this.declaration.getType());
			
			/* These kinds are handled by partial resolve. 
			if (declaration instanceof VariableDeclaration) {
				
				this.expression = new VariableAccess((VariableDeclaration) declaration);
			}
		}*/
		boolean _result = this.declaration.collect(_scope);
		return _result;
	}
	
	public void setClassDeclaration(ClasseDeclaration c) {
		
		this.declaration = c;
	}
	public ClasseDeclaration getClassDeclaration( ) {
		
		return this.declaration ;
	}
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return true;
	}

	@Override
	public Type getType() {
		//System.out.println("ThisAssignement: getType, " + this.declaration.getType());
		return this.declaration.getType();
	}

	@Override
	public Fragment getCode(TAMFactory _factory) {
		// TODO Auto-generated method stub
		return null;
	}

}
