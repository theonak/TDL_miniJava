package fr.n7.stl.miniJava.declaration;

import java.util.List;

import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.block.ast.Block;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.block.ast.SemanticsUndefinedException;

public class MainClass implements Type , ContainerDeclaration{

		private Block bloc;
		private List<ParameterDeclaration> parametres;

		public MainClass(List<ParameterDeclaration> parametres, Block bloc) {
			this.bloc = bloc;
			this.parametres = parametres;
		}
	@Override
	public String getName() {
		return "main";
	}

	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		throw new SemanticsUndefinedException("MainClasss");
	}

	@Override
	public Fragment getCode(TAMFactory _factory) {
		throw new SemanticsUndefinedException("MainClasss");
	}

	@Override
	public boolean equalsTo(Type _other) {
		throw new SemanticsUndefinedException("MainClasss equalsTo");
	}

	@Override
	public boolean compatibleWith(Type _other) {
		throw new SemanticsUndefinedException("MainClasss");
	}

	@Override
	public Type merge(Type _other) {
		throw new SemanticsUndefinedException("MainClasss");
	}

	@Override
	public int length() {
		throw new SemanticsUndefinedException("MainClasss");
	}
	
	public boolean collect(HierarchicalScope<Declaration> _scope) {
		return this.bloc.collect(_scope);
	}
	@Override
	public boolean resolve(HierarchicalScope<Declaration> _scope) {
		return this.bloc.resolve(_scope);
	}
	public boolean checkType() {
		return this.bloc.checkType();
	}
	

}
