package fr.n7.stl.miniJava.declaration;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

public interface ContainerDeclaration extends Declaration{
	
    public boolean collect(HierarchicalScope<Declaration> _scope) ;
    
    public boolean resolve(HierarchicalScope<Declaration> _scope) ;

    public Type getType();

    public Fragment getCode(TAMFactory _factory);
}
