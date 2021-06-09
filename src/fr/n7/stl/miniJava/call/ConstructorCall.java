package fr.n7.stl.miniJava.call;

import fr.n7.stl.block.ast.SemanticsUndefinedException;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.miniJava.declaration.ClasseDeclaration;
import fr.n7.stl.miniJava.definition.Attribut;
import fr.n7.stl.miniJava.definition.Definition;
import fr.n7.stl.miniJava.methode.Constructor;
import fr.n7.stl.miniJava.methode.Methode;
import fr.n7.stl.miniJava.type.Instanciation;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

import java.util.*;

public class ConstructorCall implements Expression {
	
	Instanciation instanciation;

    public Instanciation getInstanciation() {
		return instanciation;
	}
	public void setInstanciation(Instanciation instanciation) {
		this.instanciation = instanciation;
	}

	List<Expression> parametres;
	private Type type;


    public ConstructorCall(Type type){
        super();
        this.type = type;

    }
    public ConstructorCall(Type type, List<Expression> parametres){
        super();
        this.parametres = parametres;
        this.type = type;

    }

    @Override
    public boolean collect(HierarchicalScope<Declaration> _scope) {

    	return true;
    	
    }
	@Override
	public String toString() {
		String _result = "( ";
		Iterator<Expression> _iter = this.parametres.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next().getType();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next().getType();
			}
		}
		return _result + " )" ;
	}
    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
        boolean result = true;
        boolean constructorFound = false;
        result = type.resolve(_scope);
        
        if (this.type instanceof Instanciation) {
        	
        	Instanciation in = (Instanciation)this.type;
        	if(_scope.knows(in.getName())){
        		ClasseDeclaration c = (ClasseDeclaration) _scope.get(in.getName());
        		for(Constructor d : c.getConstructorList()){
        			
        			if(d.getParameterDeclarationList().size()!= this.parametres.size()){
        				continue;
        			} else {
        				for(int i = 0 ; i < this.parametres.size();i++){
                    	
        					if(! this.parametres.get(i).getType().compatibleWith(d.getParameterDeclarationList().get(i).getType()))
        						break;
        					if (i == this.parametres.size() -1) {
        						constructorFound = true;
        					}
                        	
        				}
                    
        			}
        		}
        		//Verifier que le constructeur a été trouvé
        		if (!constructorFound && this.parametres.size()!= 0) {
        			Logger.error("Compatible constructor not found : "+ in.getName() +this.toString());
        		}
        			
        	} else {
        		Logger.error("Class " + in.getName()+" not found ..");
        	}
        if(this.parametres != null){
            for (Expression e : this.parametres){
                result = result && e.resolve(_scope);
            }
        }
        }
        return result;
    }
    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {

        Fragment frag = _factory.createFragment();
        int id = _factory.createLabelNumber();
        // Empiler les paramètres
        if(this.parametres !=null)
        for (Expression arg : this.parametres){
            frag.append(arg.getCode(_factory));
        }

        // call
/*
      //  Instanciation inst = (Instanciation) this.getType();
        List<Attribut> attributList = new LinkedList<Attribut>();
        if (instanciation.getDeclaration() instanceof ClasseDeclaration) {
        	if(((ClasseDeclaration)instanciation.getDeclaration()).getDefinitionList()!=null){
        		List<Definition>  definitionList = ((ClasseDeclaration)instanciation.getDeclaration()).getDefinitionList();

        		for(Definition d : definitionList){
        			if(d.getAttribut() != null){
        				attributList.add(d.getAttribut());
        			}
        		}
        	}
    }
        int sizeOfAllParams = 0;
        if(attributList.size()!=0){

            for(Attribut a : attributList){
                sizeOfAllParams += a.getType().length();
            }
        }
        sizeOfAllParams +=1;

        frag.add(_factory.createLoadL(sizeOfAllParams));
        frag.add(Library.MAlloc);
        frag.add(_factory.createCall("FUNC_"+this.instanciation.getName()+"_START", Register.SB));
        //frag.add(_factory.createJump("FUNC_"+inst.getName()+"_"+1+"_START"));
        frag.addComment("Function "+this.instanciation.getName()+" call");
*/
        return frag;
    }
}
