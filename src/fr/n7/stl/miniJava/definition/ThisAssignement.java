package fr.n7.stl.miniJava.definition;

import fr.n7.stl.block.ast.expression.assignable.AssignableExpression;
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
		// TODO Auto-generated method stub
		return true;
	}
	
	public void setClassDeclaration(ClasseDeclaration c) {
		
		this.declaration = c;
	}
	public ClasseDeclaration getClassDeclaration( ) {
		
		return this.declaration ;
	}
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return this.declaration.resolve(_scope);
	}

	@Override
	public Type getType() {
		return this.declaration.getType();
	}

	@Override
	public Fragment getCode(TAMFactory _factory) {
		// TODO Auto-generated method stub
		return null;
	}

}
