package fr.n7.stl.miniJava.call;

import fr.n7.stl.block.ast.expression.AbstractField;
import fr.n7.stl.block.ast.expression.Expression;
import fr.n7.stl.block.ast.expression.accessible.IdentifierAccess;
import fr.n7.stl.block.ast.instruction.declaration.FunctionDeclaration;
import fr.n7.stl.block.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.block.ast.scope.Declaration;
import fr.n7.stl.block.ast.scope.HierarchicalScope;
import fr.n7.stl.block.ast.type.Type;
import fr.n7.stl.miniJava.declaration.ClasseDeclaration;
import fr.n7.stl.miniJava.declaration.ContainerDeclaration;
import fr.n7.stl.miniJava.definition.Definition;
import fr.n7.stl.miniJava.methode.Methode;
import fr.n7.stl.miniJava.type.Instanciation;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

import java.util.List;

public class MethodCall implements Expression {

    Expression expression;
    String methode;
    List<Expression> parametres;
	private Type type;

    public MethodCall( Expression expression, List<Expression> parametres){
        super();
        this.expression = expression;
        this.parametres = parametres;
    }


    @Override
    public boolean collect(HierarchicalScope<Declaration> _scope) {

        return this.expression.collect(_scope);
    }

    @Override
    public boolean resolve(HierarchicalScope<Declaration> _scope) {
    	boolean methodFound = false;
		 String methodName = "  "
;    	this.expression.resolve(_scope);
    	 if (this.expression instanceof AbstractField) {
    		 Expression e = ((AbstractField)expression).getRecord();
    		 if(e instanceof IdentifierAccess) {
    			 methodName = ((AbstractField)this.expression).getFieldName();
    			 VariableDeclaration d = (VariableDeclaration)((IdentifierAccess)e).getDeclaration();
    			 ClasseDeclaration _class =(ClasseDeclaration)((Instanciation)d.getType()).getDeclaration();
    			 List<Methode> methods = _class.getMethodsOfClass();
    			 for (Methode m : methods) {
    				 if (m.getName().equals(methodName)) {
    					 if(m.getEntete().getParameterDeclarations().size()!= this.parametres.size()){
    	        				continue;
    	        			} else {
    	        				if (this.parametres.size()==0)
    	        					return true;
    	        				for(int i = 0 ; i < this.parametres.size();i++){
    	                    	
    	        					if(! this.parametres.get(i).getType().compatibleWith(m.getEntete().getParameterDeclarations().get(i).getType()))
    	        						break;
    	        					if (i == this.parametres.size() -1) {
    	        						methodFound = true;
    	        						this.type = m.getType();
    	        					}
    	                        	
    	        				}
    	                    
    	        			}
    				 }
    			 }
    			 
    		 }
    	 }
    	 if (!methodFound  ) {
 			Logger.error("Compatible method not found for : "+ methodName );
 		}
        return true;

    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public Fragment getCode(TAMFactory _factory) {

        Fragment frag = _factory.createFragment();
        for(Expression expr : this.parametres) {
            frag.append(expr.getCode(_factory));
        }

   //     frag.add(_factory.createCall("function_"+this.methode, Register.LB));
        frag.add(_factory.createCall("FUNC_"+this.methode+"_START", Register.LB));

        return frag;
    }
}
