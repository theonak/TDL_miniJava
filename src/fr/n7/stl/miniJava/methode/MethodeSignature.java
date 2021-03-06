package fr.n7.stl.miniJava.methode;

import fr.n7.stl.block.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.util.Logger;

import java.util.*;

public class MethodeSignature implements Declaration {
	
    Type type;
    int isAbstract;
    String name;
    List<ParameterDeclaration> parameterDeclarations ;
    
	public MethodeSignature( String name, Type type, List<ParameterDeclaration> parameterDeclarations) {
        super();
        this.type = type;
        this.name = name;
        this.parameterDeclarations = parameterDeclarations;
        
        
    }
	@Override
	public String toString() {
		String _result = this.name + "( ";
		Iterator<ParameterDeclaration> _iter = this.parameterDeclarations.iterator();
		if (_iter.hasNext()) {
			_result += _iter.next();
			while (_iter.hasNext()) {
				_result += " ," + _iter.next();
			}
		}
		return _result + " )";
	}
    public void setType(Type type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ParameterDeclaration> getParameterDeclarations() {
		return parameterDeclarations;
	}

	public void setParameterDeclarations(List<ParameterDeclaration> parameterDeclarations) {
		this.parameterDeclarations = parameterDeclarations;
	}


    public boolean resolve(HierarchicalScope<Declaration> _scope){
        if(_scope.accepts(this)){
            _scope.register(this);
        }
        else{
            Logger.error("The name of this method " + this.getName() + " already exists");
            return false;
        }

        return true;
    }
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Type getType() {
        return this.type;
    }
    
    public boolean equals(Object obj) {
         if(this == obj)
            return true;
        if (obj == null)
            return false;
        MethodeSignature other = (MethodeSignature) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (parameterDeclarations == null) {
            if (other.parameterDeclarations != null)
                return false;
        } else if (!parameterDeclarations.equals(other.parameterDeclarations))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;

        return true;
    }
}
