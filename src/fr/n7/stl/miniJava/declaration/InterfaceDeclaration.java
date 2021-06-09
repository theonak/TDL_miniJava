package fr.n7.stl.miniJava.declaration;
import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.scope.SymbolTable;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.miniJava.definition.Definition;
import fr.n7.stl.miniJava.methode.MethodeSignature;
import fr.n7.stl.miniJava.type.Instanciation;
import fr.n7.stl.miniJava.type.PooType;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

import java.util.*;
public class InterfaceDeclaration implements ContainerDeclaration {

    String nom;
    List<MethodeSignature> entetes;

    public InterfaceDeclaration( String nom,  List<MethodeSignature> entetes) {
        super();
        this.nom = nom;
        this.entetes = entetes;
    }
    public boolean collect(HierarchicalScope<Declaration> _scope) {

        if(_scope.knows(this.nom)){
            Logger.error("The name " +this.nom+" of this Interface is already used ");
            return false;
        }
        _scope.register(this);
        return true;
    }

    public boolean resolve(HierarchicalScope<Declaration> _scope) {
        boolean result = true;
        
        HierarchicalScope<Declaration> tbs = new SymbolTable(_scope);
        for(MethodeSignature m : this.entetes){
            result = result && m.resolve(tbs);
        }
        return result;
    }

    public boolean checkType(){
    	// Une interface n'a pas de type ?
    	return true;
    	//throw new SemanticsUndefinedException("checktype in InterfaceDeclaration");
    }

    public Type getType(){
        return PooType.InterfaceType;
    }

    public Fragment getCode(TAMFactory _factory)
    {
        Fragment frag = _factory.createFragment();
        for(MethodeSignature m: this.entetes)
        {


        }
        return frag;
    }


    public List<MethodeSignature> getEntetes() {
        return entetes;
    }


    public void setEntetes(List<MethodeSignature> entetes) {
        this.entetes = entetes;
    }
	@Override
	public String getName() {
		return this.nom;
	}

}
